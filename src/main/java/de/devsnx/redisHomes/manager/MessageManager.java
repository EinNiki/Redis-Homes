package de.devsnx.redisHomes.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * @author Marvin Hänel (DevSnx)
 * @since 07.12.2024 12:48
 */

public class MessageManager {

    private final File file;
    private FileConfiguration messages;

    public MessageManager(File dataFolder) {
        this.file = new File(dataFolder, "messages.yml");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                saveDefaultMessages();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.messages = YamlConfiguration.loadConfiguration(file);
    }

    private void saveDefaultMessages() throws IOException {
        // Inhalte aus Ressourcen extrahieren
        try (var inputStream = getClass().getResourceAsStream("/messages.yml")) {
            if (inputStream != null) {
                java.nio.file.Files.copy(inputStream, file.toPath());
            } else {
                file.createNewFile();
            }
        }
    }

    public String getMessage(String key) {
        return messages.getString(key, "&cNachricht nicht gefunden: " + key);
    }

    public void reload() {
        this.messages = YamlConfiguration.loadConfiguration(file);
    }
}