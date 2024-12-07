package de.devsnx.redisHomes.manager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * @author Marvin Hänel (DevSnx)
 * @since 03.12.2024 12:55
 */

public class DatabaseManager {

    public HikariDataSource dataSource;
    private Logger logger = Bukkit.getLogger();

    public DatabaseManager(@NotNull FileConfiguration config) {
        // Lade die MySQL-Einstellungen aus der Konfigurationsdatei
        String host = config.getString("mysql.host");
        int port = config.getInt("mysql.port");
        String database = config.getString("mysql.database");
        String username = config.getString("mysql.username");
        String password = config.getString("mysql.password");
        int poolSize = config.getInt("mysql.poolSize", 10); // Standardwert für den Connection Pool

        setupDataSource(host, port, database, username, password, poolSize);
    }

    /**
     * Initialisiert die HikariCP-Datenquelle.
     */
    private void setupDataSource(String host, int port, String database, String username, String password, int poolSize) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=false");
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        //hikariConfig.setMaximumPoolSize(poolSize);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setIdleTimeout(30000); // Zeit, bis eine Verbindung geschlossen wird, wenn sie inaktiv ist
        hikariConfig.setMaxLifetime(1800000); // Maximale Lebenszeit einer Verbindung (30 Minuten)
        hikariConfig.setConnectionTimeout(10000); // Timeout für neue Verbindungen (10 Sekunden)
        hikariConfig.setLeakDetectionThreshold(2000); // Zeit, nach der ein Verbindungsleck erkannt wird

        try {
            this.dataSource = new HikariDataSource(hikariConfig);
            logger.info("MySQL connection pool established.");
        } catch (Exception e) {
            logger.severe("Failed to initialize MySQL connection pool: " + e.getMessage());
        }
    }

    /**
     * Gibt eine Verbindung aus dem Pool zurück.
     */
    public Connection getConnection() throws SQLException {
        if (dataSource != null) {
            return dataSource.getConnection();
        } else {
            throw new SQLException("DataSource is not initialized.");
        }
    }

    /**
     * Schließt die Datenquelle und beendet alle Verbindungen.
     */
    public void close() {
        if (dataSource != null) {
            dataSource.close();
            logger.info("MySQL connection pool closed.");
        }
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }
}