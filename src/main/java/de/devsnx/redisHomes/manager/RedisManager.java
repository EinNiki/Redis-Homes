package de.devsnx.redisHomes.manager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.logging.Logger;

/**
 * @author Marvin Hänel (DevSnx)
 * @since 03.12.2024 12:55
 */

public class RedisManager {

    private Jedis jedis;
    private String host;
    private int port;
    private String username;
    private String password;

    private Logger logger = Bukkit.getLogger();

    public RedisManager(@NotNull FileConfiguration config) {
        // Lade die Redis-Einstellungen aus der Konfigurationsdatei
        this.host = config.getString("redis.host");
        this.port = config.getInt("redis.port");
        this.username = config.getString("redis.username", "");
        this.password = config.getString("redis.password", "");

        connect();
    }

    /**
     * Stellt die Verbindung zu Redis her.
     */
    private void connect() {
        try {
            this.jedis = new Jedis(host, port);

            // Überprüfen, ob Benutzername und Passwort gesetzt sind
            if (username != null && !username.isEmpty()) {
                jedis.auth(username, password); //
                // Falls Redis ein Benutzername-Passwort-Paar erwartet
            } else if (password != null && !password.isEmpty()) {
                jedis.auth(password); // Nur Passwort verwenden, falls kein Benutzername benötigt wird
            }

            jedis.connect();
            System.out.println("Connected to Redis: " + host + ":" + port);
        } catch (JedisConnectionException e) {
            System.err.println("Error connecting to Redis: " + e.getMessage());
        }
    }

    /**
     * Schließt die Redis-Verbindung.
     */
    public void close() {
        if (jedis != null) {
            jedis.close();
            System.out.println("Redis connection closed.");
        }
    }

    /**
     * Versucht, die Verbindung zu Redis neu herzustellen.
     */
    private void reconnect() {
        System.out.println("Trying to reconnect to Redis...");
        connect();
    }

    /**
     * Veröffentlicht eine Nachricht auf einem angegebenen Redis-Kanal.
     * Diese Methode kann verwendet werden, um Nachrichten an alle Server zu senden, die den Kanal abonniert haben.
     *
     * @param channel Der Redis-Kanal, auf dem die Nachricht veröffentlicht werden soll.
     * @param message Die Nachricht, die veröffentlicht werden soll.
     */
    public void publish(String channel, String message) {
        if(jedis.isConnected()) {
            jedis.publish(channel, message);
        } else {
            System.out.println("Redis-Verbindung nicht Aktiv, publish Fehlgeschlagen");
        }
    }


    /**
     * Abonniert einen Redis-Kanal und führt die angegebene Listener-Logik aus, wenn eine Nachricht empfangen wird.
     * Diese Methode startet einen neuen Thread, um das Abonnement auszuführen, sodass es nicht blockierend ist.
     *
     * @param channel  Der Redis-Kanal, den du abonnieren möchtest.
     * @param listener Der Listener, der aufgerufen wird, wenn eine Nachricht auf dem abonnierten Kanal empfangen wird.
     */
    public void subscribe(String channel, JedisPubSub listener) {
        new Thread(() -> {
            try (Jedis jedis = new Jedis(host, port)) {
                // Authentifizierung, falls nötig
                if (username != null && !username.isEmpty()) {
                    jedis.auth(username, password);
                } else if (password != null && !password.isEmpty()) {
                    jedis.auth(password);
                }
                jedis.subscribe(listener, channel);
            }
        }).start();
    }
}
