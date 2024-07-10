package com.nextdevv.lightrtp.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nextdevv.lightrtp.models.Release;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class VersionChecker {
    private final String repo;
    private final JavaPlugin plugin;

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public VersionChecker(String repo, JavaPlugin plugin) {
        this.repo = repo;
        this.plugin = plugin;
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
            if (!version.equals(latestVersion)) {
                plugin.getLogger().info("A new version is available: " + latestVersion);
                plugin.getLogger().info("Download it at: https://spigotmc.org/resources/giftblocks.12345");
            } else {
                plugin.getLogger().info("You are running the latest version.");
            }
        });
    }
}
