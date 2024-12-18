package de.devsnx.redisHomes.manager;

import com.zaxxer.hikari.HikariDataSource;
import de.devsnx.redisHomes.RedisHomes;
import eu.thesimplecloud.api.CloudAPI;
import eu.thesimplecloud.api.service.ICloudService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static java.sql.DriverManager.getConnection;

/**
 * @author Marvin Hänel (DevSnx)
 * @since 03.12.2024 13:06
 */

public class HomeManager {

    private final HikariDataSource dataSource;
    private Logger logger = Bukkit.getLogger();
    private RedisManager redisManager;

    public HomeManager(RedisManager redisManager, DatabaseManager databaseManager) {
        this.dataSource = databaseManager.getDataSource();
        this.redisManager = redisManager;
        this.logger = logger;

        // Tabelle erstellen, falls sie nicht existiert
        createTable();
    }

    /**
     * Erstellt die Tabelle für Homes, falls sie nicht existiert.
     */
    private void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS homes (
                id INT AUTO_INCREMENT PRIMARY KEY,
                player_uuid VARCHAR(36) NOT NULL,
                home_name VARCHAR(50) NOT NULL,
                server_name VARCHAR(50) NOT NULL,
                world VARCHAR(50) NOT NULL,
                x DOUBLE NOT NULL,
                y DOUBLE NOT NULL,
                z DOUBLE NOT NULL,
                yaw FLOAT NOT NULL,
                pitch FLOAT NOT NULL
            )
            """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.execute();
            logger.info("Homes-Tabelle erfolgreich überprüft/erstellt.");
        } catch (SQLException e) {
            logger.severe("Fehler beim Erstellen der Homes-Tabelle: " + e.getMessage());
        }
    }

    /**
     * Fügt ein Home für einen Spieler hinzu oder aktualisiert es.
     */
    public void setHome(Player player, String homeName, Location location) {
        String sql = """
        REPLACE INTO homes (player_uuid, home_name, server_name, world, x, y, z, yaw, pitch) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, homeName);
            statement.setString(3, CloudAPI.getInstance().getCloudPlayerManager().getCachedCloudPlayer(player.getUniqueId()).getConnectedServer().getName()); // Ersetzt durch den Servernamen
            statement.setString(4, location.getWorld().getName());
            statement.setDouble(5, location.getX());
            statement.setDouble(6, location.getY());
            statement.setDouble(7, location.getZ());
            statement.setFloat(8, location.getYaw());
            statement.setFloat(9, location.getPitch());

            statement.executeUpdate();
            logger.info("Home " + homeName + " erfolgreich gesetzt für Spieler " + player.getName());
        } catch (SQLException e) {
            logger.severe("Fehler beim Setzen des Homes: " + e.getMessage());
        }
    }

    /**
     * Holt ein Home für einen Spieler.
     */
    public Home getHome(Player player, String homeName) {
        String sql = """
        SELECT * FROM homes WHERE player_uuid = ? AND home_name = ?
        """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, homeName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String server = resultSet.getString("server_name");
                    String worldName = resultSet.getString("world");
                    World world = Bukkit.getWorld(worldName);
                    double x = resultSet.getDouble("x");
                    double y = resultSet.getDouble("y");
                    double z = resultSet.getDouble("z");
                    float yaw = resultSet.getFloat("yaw");
                    float pitch = resultSet.getFloat("pitch");

                    if (world != null) {
                        return new Home(homeName, server, world, x, y, z, yaw, pitch);
                    } else {
                        logger.warning("Welt " + worldName + " für Home " + homeName + " nicht gefunden.");
                    }
                }
            }
        } catch (SQLException e) {
            logger.severe("Fehler beim Abrufen des Homes: " + e.getMessage());
        }
        return null;
    }

    /**
     * Überprüft, ob ein Home für einen Spieler existiert.
     *
     * @param player   Der Spieler, dessen Home überprüft werden soll.
     * @param homeName Der Name des Homes.
     * @return true, wenn das Home existiert, andernfalls false.
     */
    public boolean existsHome(Player player, String homeName) {
        String sql = """
    SELECT COUNT(*) AS count FROM homes WHERE player_uuid = ? AND home_name = ?
    """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, homeName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            logger.severe("Fehler beim Überprüfen des Homes: " + e.getMessage());
        }

        return false;
    }

    /**
     * Entfernt ein Home für einen Spieler.
     */
    public boolean deleteHome(Player player, String homeName) {
        String sql = """
        DELETE FROM homes WHERE player_uuid = ? AND home_name = ?
        """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, homeName);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Home " + homeName + " erfolgreich entfernt für Spieler " + player.getName());
                return true;
            }
        } catch (SQLException e) {
            logger.severe("Fehler beim Entfernen des Homes: " + e.getMessage());
        }
        return false;
    }

    /**
     * Teleportiert einen Spieler zu einem Home, wenn es auf dem aktuellen Server liegt.
     */
    public boolean teleportToHome(Player player, String homeName) {
        Home home = getHome(player, homeName);

        if (home == null) {
            return false;
        }

        if (!CloudAPI.getInstance().getCloudPlayerManager().getCachedCloudPlayer(player.getUniqueId()).getConnectedServer().getName().equals(home.getServer())) {
            return false;
        }

        Location location = new Location(home.getWorld(), home.getX(), home.getY(), home.getZ(), home.getYaw(), home.getPitch());
        player.teleport(location);
        player.sendMessage(RedisHomes.getInstance().getMessageManager().getMessage("messages.home.teleportet").replace("&", "§").replace("%home%", homeName));
        return true;
    }

    /**
     * Teleportiert einen Spieler zu einem Home, auch wenn sich dieses auf einem anderen Server befindet.
     */
    public void teleportToServerAndHome(Player player, String homeName) {
        Home home = getHome(player, homeName);

        if (home == null) {
            player.sendMessage("Home \"" + homeName + "\" wurde nicht gefunden.");
            return;
        }

        String currentServer = CloudAPI.getInstance()
                .getCloudPlayerManager()
                .getCachedCloudPlayer(player.getUniqueId())
                .getConnectedServer()
                .getName();
        String targetServer = home.getServer();

        // Spieler ist bereits auf dem Zielserver
        if (currentServer.equals(targetServer)) {
            teleportToHome(player, homeName);
            return;
        }

        // Zielserver ist offline
        ICloudService iCloudService = CloudAPI.getInstance()
                .getCloudServiceManager()
                .getCloudServiceByName(targetServer);
        if (iCloudService == null || !iCloudService.isOnline()) {
            player.sendMessage("Der Server, auf dem dein Home ist, ist offline!");
            return;
        }

        // Spieler auf den Zielserver verbinden
        CloudAPI.getInstance()
                .getCloudPlayerManager()
                .getCachedCloudPlayer(player.getUniqueId())
                .connect(iCloudService);

        // Redis-Nachricht vorbereiten
        String message = String.format("%s:%s:%s:%s:%s:%f:%f:%f:%f:%f",
                player.getUniqueId().toString(),
                player.getName(),
                targetServer,
                home.getName(),
                home.getWorld().getName(),
                home.getX(),
                home.getY(),
                home.getZ(),
                home.getYaw(),
                home.getPitch());

        try {
            redisManager.publish("home-teleport", message);
            logger.severe("Redis Publish: " + message);
        } catch (Exception e) {
            logger.severe("Fehler beim Senden der Redis-Nachricht: " + e.getMessage());
            player.sendMessage("Ein Fehler ist aufgetreten. Bitte versuche es später erneut.");
        }
    }

    /**
     * Holt alle Homes eines Spielers.
     */
    public List<Home> getAllHomes(Player player) {
        List<Home> homes = new ArrayList<>();
        String sql = """
        SELECT * FROM homes WHERE player_uuid = ?
        """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getUniqueId().toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String homeName = resultSet.getString("home_name");
                    String server = resultSet.getString("server_name");
                    String worldName = resultSet.getString("world");
                    World world = Bukkit.getWorld(worldName);
                    double x = resultSet.getDouble("x");
                    double y = resultSet.getDouble("y");
                    double z = resultSet.getDouble("z");
                    float yaw = resultSet.getFloat("yaw");
                    float pitch = resultSet.getFloat("pitch");

                    if (world != null) {
                        homes.add(new Home(homeName, server, world, x, y, z, yaw, pitch));
                    } else {
                        logger.warning("Welt " + worldName + " für Home " + homeName + " nicht gefunden.");
                    }
                }
            }
        } catch (SQLException e) {
            logger.severe("Fehler beim Abrufen der Homes: " + e.getMessage());
        }
        return homes;
    }

    /**
     * Methode zum Abfragen ob der Spieler ein Home hat.
     */
    public boolean hasHome(Player player) {
        String query = "SELECT EXISTS (SELECT 1 FROM homes WHERE player_uuid = ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, player.getUniqueId().toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }
        } catch (SQLException e) {
            logger.severe("Fehler beim abfragen ob der Spieler ein Home hat: " + e.getMessage());
        }

        return false;
    }


    /**
     * Klasse für Home-Daten.
     */
    public static class Home {
        private final String name;
        private final String server;
        private final World world;
        private final double x;
        private final double y;
        private final double z;
        private final float yaw;
        private final float pitch;

        public Home(String name,String server,  World world, double x, double y, double z, float yaw, float pitch) {
            this.name = name;
            this.server = server;
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
        }

        public String getName() {
            return name;
        }

        public String getServer() {
            return server;
        }

        public World getWorld() {
            return world;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }

        public float getYaw() {
            return yaw;
        }

        public float getPitch() {
            return pitch;
        }
    }
}