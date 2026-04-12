package com.comonier.netheritesilkspawners;

import org.bukkit.Bukkit;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Handles asynchronous communication with Discord Webhooks for remote logging.
 */
public class DiscordWebhook {

    private final Main plugin;

    public DiscordWebhook(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Sends a message to the configured Discord Webhook URL asynchronously.
     * @param content The plain text or JSON content to send.
     */
    public void sendAsync(String content) {
        String webhookUrl = plugin.getConfig().getString("discord-webhook-url");
        
        // Validation: Check if the URL is empty or remains as the default placeholder
        if (webhookUrl == null || webhookUrl.isEmpty() || webhookUrl.equals("YOUR_URL_HERE")) {
            return;
        }

        // Running asynchronously to prevent server lag/tps drop
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL(webhookUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "NSS-Webhook");
                connection.setDoOutput(true);

                // Basic JSON structure for Discord Webhook
                String json = "{\"content\": \"" + content + "\"}";

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = json.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                // Necessary to trigger the request
                connection.getResponseCode();
                connection.disconnect();
            } catch (Exception ignored) {
                // Silently ignore connection errors to avoid console spam
            }
        });
    }
}
