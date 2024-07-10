package com.nextdevv.lightrtp;

import com.nextdevv.lightrtp.commands.LightRtpAdminCommand;
import com.nextdevv.lightrtp.commands.RtpCommand;
import com.nextdevv.lightrtp.configs.ConfigLoader;
import com.nextdevv.lightrtp.configs.Messages;
import com.nextdevv.lightrtp.configs.Settings;
import com.nextdevv.lightrtp.metrics.Metrics;
import com.nextdevv.lightrtp.utils.VersionChecker;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@Setter
@Getter
public final class LightRTP extends JavaPlugin {
    Settings settings;
    Messages messages;
    VersionChecker versionChecker = new VersionChecker("NextDevv/LightRTP", this);

    @Override
    public void onEnable() {
        getLogger().info("=== LightRTP ===");

        getLogger().info("Loading configs...");
        ConfigLoader configLoader = new ConfigLoader(getDataFolder());
        settings = configLoader.loadSettings();
        messages = configLoader.loadMessages();

        getLogger().info("Loading commands...");
        Objects.requireNonNull(getCommand("rtp")).setExecutor(new RtpCommand(this));
        Objects.requireNonNull(getCommand("rtp")).setTabCompleter(new RtpCommand(this));
        Objects.requireNonNull(getCommand("lightrtp")).setExecutor(new LightRtpAdminCommand(this));
        Objects.requireNonNull(getCommand("lightrtp")).setTabCompleter(new LightRtpAdminCommand(this));

        getLogger().info("Loading metrics...");
        new Metrics(this, 22543);

        getLogger().info("=== LightRTP ===");
    }

    @Override
    public void onDisable() {
        getLogger().info("=== LightRTP ===");

        getLogger().info("Saving configs...");
        ConfigLoader configLoader = new ConfigLoader(getDataFolder());
        configLoader.saveSettings(settings);

        getLogger().info("Disabling plugin...");

        getLogger().info("=== LightRTP ===");
    }
}
