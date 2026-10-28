package top.jaxlabs.farmweltplugin.utils;

import top.jaxlabs.farmweltplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FarmweltMenu {
    private final Main plugin;
    private final WorldUtils worldUtils;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public FarmweltMenu(Main plugin, WorldUtils worldUtils) {
        this.plugin = plugin;
        this.worldUtils = worldUtils;
    }

    public void openMenu(Player player) {
        int size = 9 * 3; // 3 Reihen
        String title = plugin.getLanguageString("menu-title");
        Inventory inventory = Bukkit.createInventory(null, size, title);

        // Normal Farmwelt
        ItemStack normalWorldItem = createWorldItem(
                Material.valueOf(plugin.getConfig().getString("menu.normal-world.material", "GRASS_BLOCK")),
                plugin.getConfig().getString("menu.normal-world.name", "&aFarmwelt"),
                plugin.getWorldName(),
                World.Environment.NORMAL
        );
        inventory.setItem(10, normalWorldItem);

        // Nether Farmwelt
        if (plugin.getConfig().getBoolean("menu.nether-world.enabled", false)) {
            ItemStack netherWorldItem = createWorldItem(
                    Material.valueOf(plugin.getConfig().getString("menu.nether-world.material", "NETHERRACK")),
                    plugin.getConfig().getString("menu.nether-world.name", "&cNether Farmwelt"),
                    plugin.getNetherWorldName(),
                    World.Environment.NETHER
            );
            inventory.setItem(13, netherWorldItem);
        }

        // End Farmwelt
        if (plugin.getConfig().getBoolean("menu.end-world.enabled", false)) {
            ItemStack endWorldItem = createWorldItem(
                    Material.valueOf(plugin.getConfig().getString("menu.end-world.material", "END_STONE")),
                    plugin.getConfig().getString("menu.end-world.name", "&5End Farmwelt"),
                    plugin.getEndWorldName(),
                    World.Environment.THE_END
            );
            inventory.setItem(16, endWorldItem);
        }

        if (plugin.isBackEnabled() && plugin.getBackLocation(player.getUniqueId()) != null) {
            ItemStack backItem = new ItemStack(Material.ENDER_PEARL);
            ItemMeta backMeta = backItem.getItemMeta();
            backMeta.setDisplayName(plugin.getLanguageString("menu-back"));
            backItem.setItemMeta(backMeta);
            inventory.setItem(22, backItem);
        }

        player.openInventory(inventory);
    }

    private ItemStack createWorldItem(Material material, String displayName, String worldName, World.Environment environment) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName.replace("&", "§"));

        List<String> lore = new ArrayList<>();
        
        // Zeige letztes Reset-Datum
        LocalDateTime lastReset = plugin.getLastResetTime(worldName);
        String lastResetStr = lastReset != null 
                ? lastReset.format(DATE_FORMAT) 
                : plugin.getLanguageString("menu-never-reset");
        
        lore.add(plugin.getLanguageString("menu-last-reset").replace("%date%", lastResetStr));
        
        // Zeige nächstes Reset-Datum
        String nextResetStr = getNextResetDate();
        lore.add(plugin.getLanguageString("menu-next-reset").replace("%date%", nextResetStr));
        
        // Anzahl der aktuellen Spieler
        int playerCount = 0;
        if (worldUtils.worldExists(worldName)) {
            World world = Bukkit.getWorld(worldName);
            if (world != null) {
                playerCount = world.getPlayers().size();
            }
        }
        lore.add(plugin.getLanguageString("menu-player-count").replace("%count%", String.valueOf(playerCount)));
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    
    private String getNextResetDate() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(plugin.getConfig().getString("reset-schedule.timezone", "Europe/Berlin")));
        LocalDateTime nextReset = null;
        
        if (plugin.getConfig().getBoolean("reset-schedule.daily")) {
            nextReset = now.toLocalDate().plusDays(1).atStartOfDay();
        } else if (plugin.getConfig().getBoolean("reset-schedule.weekly")) {
            int targetDay = plugin.getConfig().getInt("reset-schedule.day-of-week");
            int currentDay = now.getDayOfWeek().getValue();
            int daysToAdd = (targetDay - currentDay + 7) % 7;
            if (daysToAdd == 0) daysToAdd = 7;
            nextReset = now.toLocalDate().plusDays(daysToAdd).atStartOfDay();
        } else if (plugin.getConfig().getBoolean("reset-schedule.monthly")) {
            int targetDay = plugin.getConfig().getInt("reset-schedule.day-of-month");
            nextReset = now.withDayOfMonth(targetDay);
            if (now.getDayOfMonth() >= targetDay) {
                nextReset = nextReset.plusMonths(1);
            }
            nextReset = nextReset.toLocalDate().atStartOfDay();
        }
        
        return nextReset != null 
                ? nextReset.format(DATE_FORMAT) 
                : plugin.getLanguageString("menu-no-schedule");
    }
} 