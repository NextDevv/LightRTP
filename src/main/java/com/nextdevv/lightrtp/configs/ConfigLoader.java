package com.nextdevv.lightrtp.configs;

import de.exlll.configlib.ConfigLib;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;

import java.io.File;

public class ConfigLoader {
    private final File folder;

    public ConfigLoader(File folder) {
        this.folder = folder;

        if(!folder.exists()) {
            boolean success = folder.mkdirs();
            if(!success) {
                throw new RuntimeException("Failed to create folder " + folder.getAbsolutePath());
            }
        }
    }

    public Settings loadSettings() {
        YamlConfigurationProperties properties = ConfigLib.BUKKIT_DEFAULT_PROPERTIES.toBuilder()
                .header("LightRTP Configuration")
                .footer("By NextDevv")
                .build();
        File settingsFile = new File(folder, "config.yml");

        return YamlConfigurations.update(
                settingsFile.toPath(),
                Settings.class,
                properties
        );
    }

    public Messages loadMessages() {
        YamlConfigurationProperties properties = ConfigLib.BUKKIT_DEFAULT_PROPERTIES.toBuilder()
                .header("LightRTP Messages")
                .footer("By NextDevv")
                .build();
        File messagesFile = new File(folder, "messages.yml");

        return YamlConfigurations.update(
                messagesFile.toPath(),
                Messages.class,
                properties
        );
    }

    public void saveSettings(Settings settings) {
        File settingsFile = new File(folder, "config.yml");

        YamlConfigurations.save(
                settingsFile.toPath(),
                Settings.class,
                settings
        );
    }
}
