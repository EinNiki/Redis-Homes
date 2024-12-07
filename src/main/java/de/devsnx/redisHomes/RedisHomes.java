package de.devsnx.redisHomes;

import de.devsnx.redisHomes.commands.HomeCommand;
import de.devsnx.redisHomes.manager.DatabaseManager;
import de.devsnx.redisHomes.manager.HomeManager;
import de.devsnx.redisHomes.manager.RedisManager;
import eu.thesimplecloud.api.CloudAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

public final class RedisHomes extends JavaPlugin {

    public static RedisHomes instance;
    private RedisManager redisManager;
    private DatabaseManager databaseManager;
    private HomeManager homeManager;

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("==------ Redis Homes ------==");
        saveDefaultConfig();
        redisManager = new RedisManager(getConfig());
        getLogger().info("Redis connected.");
        databaseManager = new DatabaseManager(getConfig());
        getLogger().info("Database connected.");
        homeManager = new HomeManager(this.redisManager, this.databaseManager);

        getCommand("sethome").setExecutor(new HomeCommand(homeManager));
        getCommand("home").setExecutor(new HomeCommand(homeManager));
        getCommand("delhome").setExecutor(new HomeCommand(homeManager));

        getLogger().info("==------ Redis Homes ------==");
    }

    @Override
    public void onDisable() {
        getLogger().info("==------ Redis Homes ------==");
        if (redisManager != null) {
            redisManager.close();
            getLogger().info("Redis connection closed.");
        }
        if (databaseManager != null) {
            databaseManager.close();
            getLogger().info("Database connection closed.");
        }
        getLogger().info("==------ Redis Homes ------==");
        instance = null;

        subscribeToRedis();
    }

    public void subscribeToRedis() {
        new Thread(() -> {
            redisManager.getJedis().subscribe(new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    if (!channel.equals("home-teleport")) return;

                    getLogger().severe("Teleport anfrage erhalten von Redis");

                    // Nachricht analysieren
                    String[] parts = message.split(":");
                    if (parts.length != 9) return;

                    String uuid = parts[0];
                    String playerName = parts[1];
                    String worldName = parts[3];
                    double x = Double.parseDouble(parts[4]);
                    double y = Double.parseDouble(parts[5]);
                    double z = Double.parseDouble(parts[6]);
                    float yaw = Float.parseFloat(parts[7]);
                    float pitch = Float.parseFloat(parts[8]);

                    // Verzögert ausführen, falls der Spieler noch nicht verbunden ist
                    Bukkit.getScheduler().runTaskLater(getInstance(), () -> {
                        Player player = Bukkit.getPlayer(playerName);
                        if (player == null || !UUID.fromString(uuid).equals(player.getUniqueId())) return;

                        World world = Bukkit.getWorld(worldName);
                        if (world == null) {
                            getLogger().warning("Welt " + worldName + " nicht gefunden für Teleportation von Spieler " + playerName);
                            return;
                        }

                        Location location = new Location(world, x, y, z, yaw, pitch);
                        player.teleport(location);
                        player.sendMessage("Du wurdest zu deinem Home teleportiert.");
                    }, 40L);
                }
            }, "home-teleport");
        }).start();
    }

    public DatabaseManager getDatabaseManager() {
        return this.databaseManager;
    }

    public HomeManager getHomeManager() {
        return this.homeManager;
    }

    public static RedisHomes getInstance() {
        return instance;
    }

    public RedisManager getRedisManager() {
        return this.redisManager;
    }

}