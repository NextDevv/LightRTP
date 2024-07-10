package com.nextdevv.lightrtp.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nextdevv.lightrtp.models.Release;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class VersionChecker implements Listener {
    private final String repo;
    private final JavaPlugin plugin;

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final String PERMISSION = "lightrtp.admin";
    private final String DOWNLOAD_LINK = "https://www.spigotmc.org/resources/lightrtp-light-weight-rtp-plugin.117822/";

    private boolean updateAvailable = false;

    public VersionChecker(String repo, JavaPlugin plugin) {
        this.repo = repo;
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(event.getPlayer().hasPermission(PERMISSION) && updateAvailable) {
            ChatUtils.msg(event.getPlayer(), "{prefix} &eYou are running an outdated version of LightRTP. &7[&ev"+ plugin.getDescription().getVersion() +"&7]");
            ChatUtils.msg(event.getPlayer(), "{prefix} &eA new version of LightRTP is available. Download it at: " + DOWNLOAD_LINK);
        }
    }

    public CompletableFuture<String> getLatestVersion() throws URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://api.github.com/repos/" + repo + "/releases/latest"))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(body -> gson.fromJson(body, Release.class).getTagName());
    }

    public void checkVersion(String version) throws URISyntaxException {
        getLatestVersion().thenAccept(latestVersion -> {
            if (!("v"+version).equals(latestVersion)) {
                plugin.getLogger().info("A new version is available: " + latestVersion);
                plugin.getLogger().info("Download it at: " + DOWNLOAD_LINK);
                updateAvailable = true;
            } else {
                plugin.getLogger().info("You are running the latest version.");
            }
        });
    }
}
