package com.comonier.netheritesilkspawners;

import org.bukkit.Bukkit;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordWebhook {

    private final Main plugin;

    public DiscordWebhook(Main plugin) {
        this.plugin = plugin;
    }

    public void sendAsync(String content) {
        String webhookUrl = plugin.getConfig().getString("discord-webhook-url");
        if (webhookUrl == null || webhookUrl.isEmpty() || webhookUrl.equals("SUA_URL_AQUI")) return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL(webhookUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "NSS-Webhook");
                connection.setDoOutput(true);

                String json = "{\"content\": \"" + content + "\"}";

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = json.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                connection.getResponseCode();
                connection.disconnect();
            } catch (Exception ignored) {}
        });
    }
}
