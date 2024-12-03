package de.devsnx.redisHomes;

import de.devsnx.redisHomes.commands.HomeCommand;
import de.devsnx.redisHomes.manager.DatabaseManager;
import de.devsnx.redisHomes.manager.HomeManager;
import de.devsnx.redisHomes.manager.LanguageManager;
import de.devsnx.redisHomes.manager.RedisManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class RedisHomes extends JavaPlugin {

    @Getter public static RedisHomes instance;
    @Getter private RedisManager redisManager;
    @Getter private DatabaseManager databaseManager;
    @Getter private LanguageManager languageManager;
    @Getter private HomeManager homeManager;

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("==------ Redis Homes ------==");
        saveDefaultConfig();
        redisManager = new RedisManager(getConfig());
        getLogger().info("Redis connected.");
        databaseManager = new DatabaseManager(getConfig());
        getLogger().info("Database connected.");
        languageManager = new LanguageManager();

        homeManager = new HomeManager(redisManager, databaseManager);

        getLogger().info("==------ Redis Homes ------==");

        getCommand("sethome").setExecutor(new HomeCommand(homeManager));
        getCommand("home").setExecutor(new HomeCommand(homeManager));
        getCommand("delhome").setExecutor(new HomeCommand(homeManager));
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
    }

}