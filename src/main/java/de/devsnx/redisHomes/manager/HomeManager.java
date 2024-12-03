package de.devsnx.redisHomes.manager;

import com.zaxxer.hikari.HikariDataSource;
import net.md_5.bungee.api.ProxyServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * @author Marvin Hänel (DevSnx)
 * @since 03.12.2024 13:06
 */

public class HomeManager {

    private final HikariDataSource dataSource;
    private final Jedis jedis;
    private Logger logger = Bukkit.getLogger();

    public HomeManager(RedisManager redisManager, DatabaseManager databaseManager) {
        this.jedis = redisManager.getJedis();
        this.dataSource = databaseManager.getDataSource();
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
     * Überprüft, ob ein Home für den Spieler existiert.
     *
     * @param player   Der Spieler
     * @param homeName Der Name des Homes
     * @return true, wenn das Home existiert, andernfalls false
     */
    public boolean homeExists(Player player, String homeName) {
        String sql = "SELECT COUNT(*) FROM homes WHERE player_uuid = ? AND home_name = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, homeName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0; // Wenn count > 0, existiert das Home
                }
            }
        } catch (SQLException e) {
            logger.severe("Fehler beim Überprüfen, ob das Home existiert: " + e.getMessage());
        }
        return false; // Rückgabe false, wenn ein Fehler auftritt oder Home nicht gefunden wurde
    }

    /**
     * Fügt ein neues Home hinzu oder aktualisiert ein bestehendes.
     *
     * @param player   Der Spieler
     * @param homeName Der Name des Homes
     * @param server   Der Servername
     * @param x        X-Koordinate
     * @param y        Y-Koordinate
     * @param z        Z-Koordinate
     * @param yaw      Blickrichtung Yaw
     * @param pitch    Blickrichtung Pitch
     */
    public void saveHome(Player player, String homeName, String server, double x, double y, double z, float yaw, float pitch) {
        String sql = """
            INSERT INTO homes (player_uuid, home_name, server_name, x, y, z, yaw, pitch)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
            server_name = VALUES(server_name),
            x = VALUES(x),
            y = VALUES(y),
            z = VALUES(z),
            yaw = VALUES(yaw),
            pitch = VALUES(pitch)
            """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, homeName);
            statement.setString(3, server);
            statement.setDouble(4, x);
            statement.setDouble(5, y);
            statement.setDouble(6, z);
            statement.setFloat(7, yaw);
            statement.setFloat(8, pitch);

            statement.executeUpdate();

            // Redis-Update senden
            jedis.publish("home-updates", player.getUniqueId() + ":" + homeName);

            logger.info("Home gespeichert: " + homeName + " für " + player.getName());
        } catch (SQLException e) {
            logger.severe("Fehler beim Speichern eines Homes: " + e.getMessage());
        }
    }

    /**
     * Holt die Koordinaten eines Homes.
     *
     * @param player   Der Spieler
     * @param homeName Der Name des Homes
     * @return Home-Daten oder null, falls nicht gefunden
     */
    public Home getHome(Player player, String homeName) {
        String sql = "SELECT server_name, x, y, z, yaw, pitch FROM homes WHERE player_uuid = ? AND home_name = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, homeName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String server = resultSet.getString("server_name");
                    double x = resultSet.getDouble("x");
                    double y = resultSet.getDouble("y");
                    double z = resultSet.getDouble("z");
                    float yaw = resultSet.getFloat("yaw");
                    float pitch = resultSet.getFloat("pitch");

                    return new Home(homeName, server, x, y, z, yaw, pitch);
                }
            }
        } catch (SQLException e) {
            logger.severe("Fehler beim Abrufen eines Homes: " + e.getMessage());
        }
        return null;
    }

    /**
     * Entfernt ein Home eines Spielers.
     *
     * @param player   Der Spieler
     * @param homeName Der Name des Homes
     */
    public void removeHome(Player player, String homeName) {
        String sql = "DELETE FROM homes WHERE player_uuid = ? AND home_name = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, homeName);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                // Home erfolgreich entfernt, Redis-Update senden
                jedis.publish("home-deletes", player.getUniqueId() + ":" + homeName);
                player.sendMessage("§aHome '" + homeName + "' wurde erfolgreich entfernt.");
                logger.info("Home '" + homeName + "' für Spieler " + player.getName() + " entfernt.");
            } else {
                player.sendMessage("§cHome '" + homeName + "' konnte nicht gefunden werden.");
            }
        } catch (SQLException e) {
            player.sendMessage("§cFehler beim Entfernen des Homes.");
            logger.severe("Fehler beim Entfernen eines Homes: " + e.getMessage());
        }
    }

    /**
     * Teleportiert einen Spieler auf den entsprechenden Server und dann zu seinem Home.
     *
     * @param player    Der Spieler
     * @param homeName  Der Name des Homes
     */
    public void teleportToServerAndHome(Player player, String homeName) {
        Home home = getHome(player, homeName);

        if (home == null) {
            player.sendMessage("§cHome nicht gefunden.");
            return;
        }

        // Spieler zum richtigen Server teleportieren
        String targetServer = home.getServer();
        teleportToServer(player, targetServer);

        // Wenn der Spieler auf dem richtigen Server ist, teleportiere ihn zum Home
        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("RedisHomes"), () -> {
            if (Bukkit.getServer().getName().equals(targetServer)) {
                teleportToHome(player, home);
            } else {
                player.sendMessage("§cFehler: Du befindest dich nicht auf dem richtigen Server.");
            }
        }, 60L); // Warte 3 Sekunden (60 Ticks) auf den Serverwechsel
    }

    /**
     * Teleportiert den Spieler zu seinem Home auf dem Server.
     *
     * @param player Der Spieler
     * @param home   Das Home-Objekt
     */
    private void teleportToHome(Player player, Home home) {
        player.teleport(new Location(Bukkit.getWorld(home.getServer()), home.getX(), home.getY(), home.getZ(), home.getYaw(), home.getPitch()));
        player.sendMessage("§aDu wurdest zu deinem Home teleportiert!");
    }

    /**
     * Teleportiert den Spieler auf einen bestimmten BungeeCord-Server.
     *
     * @param player       Der Spieler
     * @param targetServer Der Name des Zielservers
     */
    private void teleportToServer(Player player, String targetServer) {
        // Hole den ProxyServer und benutze die connect-Methode, um den Spieler zu verbinden
        ProxyServer.getInstance().getPlayer(player.getUniqueId()).connect(ProxyServer.getInstance().getServerInfo(targetServer));
        player.sendMessage("§aDu wirst zu " + targetServer + " teleportiert!");
    }

    /**
     * Klasse für Home-Daten.
     */
    public static class Home {
        private final String name;
        private final String server;
        private final double x;
        private final double y;
        private final double z;
        private final float yaw;
        private final float pitch;

        public Home(String name, String server, double x, double y, double z, float yaw, float pitch) {
            this.name = name;
            this.server = server;
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