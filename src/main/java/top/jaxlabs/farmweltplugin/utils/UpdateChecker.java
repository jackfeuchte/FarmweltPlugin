package top.jaxlabs.farmweltplugin.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker implements Listener {

    private final JavaPlugin plugin;
    private final String repoUrl = "https://api.github.com/repos/jackfeuchte/FarmweltPlugin/releases/latest";
    private String latestVersion;
    private String updateTitle;
    private boolean updateAvailable = false;

    public UpdateChecker(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL(repoUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "FarmweltPlugin");

                if (connection.getResponseCode() == 200) {
                    InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                    JsonObject json = new JsonParser().parse(reader).getAsJsonObject();
                    
                    latestVersion = json.get("tag_name").getAsString();
                    updateTitle = json.get("name").getAsString();
                    
                    String currentVersion = plugin.getDescription().getVersion();
                    
                    // Normalize versions by removing "v" prefix for comparison
                    String normalizedCurrent = normalizeVersion(currentVersion);
                    String normalizedLatest = normalizeVersion(latestVersion);
                    
                    if (isNewer(normalizedLatest, normalizedCurrent)) {
                        updateAvailable = true;
                        plugin.getLogger().info("--------------------------------------------------");
                        plugin.getLogger().info("A new version of FarmweltPlugin is available!");
                        plugin.getLogger().info("Current version: " + currentVersion);
                        plugin.getLogger().info("New version: " + latestVersion);
                        plugin.getLogger().info("Title: " + updateTitle);
                        plugin.getLogger().info("Download: https://github.com/jackfeuchte/FarmweltPlugin/releases/latest");
                        plugin.getLogger().info("--------------------------------------------------");
                    } else {
                         plugin.getLogger().info("FarmweltPlugin is up to date.");
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to check for updates: " + e.getMessage());
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!updateAvailable) return;
        if (!plugin.getConfig().getBoolean("update-notification", true)) return;

        Player player = event.getPlayer();
        if (player.hasPermission("farmwelt.admin") || player.hasPermission("farmwelt.update")) {
             player.sendMessage(ChatColor.GRAY + "--------------------------------------------------");
             player.sendMessage(ChatColor.GOLD + "FarmweltPlugin Update Available!");
             player.sendMessage(ChatColor.AQUA + "Version: " + ChatColor.WHITE + latestVersion);
             player.sendMessage(ChatColor.AQUA + "Title: " + ChatColor.WHITE + updateTitle);
             player.sendMessage(ChatColor.GREEN + "Download: " + ChatColor.UNDERLINE + "https://github.com/jackfeuchte/FarmweltPlugin/releases/latest");
             player.sendMessage(ChatColor.GRAY + "--------------------------------------------------");
        }
    }
    
    static boolean isNewer(String latest, String current) {
        String[] a = latest.split("[.-]");
        String[] b = current.split("[.-]");
        for (int i = 0; i < Math.max(a.length, b.length); i++) {
            int x = i < a.length ? parse(a[i]) : 0;
            int y = i < b.length ? parse(b[i]) : 0;
            if (x != y) {
                return x > y;
            }
        }
        return false;
    }

    private static int parse(String part) {
        String digits = part.replaceAll("\\D", "");
        return digits.isEmpty() ? 0 : Integer.parseInt(digits);
    }

    /**
     * Normalizes a version string by removing the "v" prefix and converting to lowercase.
     * This ensures consistent version comparison regardless of prefix format.
     * 
     * @param version The version string to normalize (e.g., "v1.4.4" or "1.4.4")
     * @return The normalized version string (e.g., "1.4.4")
     */
    private String normalizeVersion(String version) {
        if (version == null) {
            return "";
        }
        String normalized = version.toLowerCase().trim();
        return normalized.startsWith("v") ? normalized.substring(1) : normalized;
    }
}
