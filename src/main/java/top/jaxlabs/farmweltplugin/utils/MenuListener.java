package top.jaxlabs.farmweltplugin.utils;

import top.jaxlabs.farmweltplugin.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuListener implements Listener {
    private final Main plugin;
    private final WorldUtils worldUtils;

    public MenuListener(Main plugin, WorldUtils worldUtils) {
        this.plugin = plugin;
        this.worldUtils = worldUtils;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        String title = event.getView().getTitle();
        
        if (!title.equals(plugin.getLanguageString("menu-title"))) {
            return;
        }

        event.setCancelled(true);
        
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) {
            return;
        }

        ItemMeta meta = clickedItem.getItemMeta();
        String displayName = meta.getDisplayName();
        
        if (displayName.equals(plugin.getLanguageString("menu-back"))) {
            player.closeInventory();
            player.performCommand("farmwelt back");
            return;
        }

        // Normal Farmwelt
        String normalName = plugin.getConfig().getString("menu.normal-world.name", "&aFarmwelt").replace("&", "§");
        if (displayName.equals(normalName)) {
            player.closeInventory();
            if (worldUtils.worldExists(plugin.getWorldName())) {
                worldUtils.teleportToWorld(player, plugin.getWorldName());
                player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("world-teleport"));
            } else {
                player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("no-world"));
            }
            return;
        }
        
        // Nether Farmwelt
        String netherName = plugin.getConfig().getString("menu.nether-world.name", "&cNether Farmwelt").replace("&", "§");
        if (displayName.equals(netherName)) {
            player.closeInventory();
            if (worldUtils.worldExists(plugin.getNetherWorldName())) {
                worldUtils.teleportToWorld(player, plugin.getNetherWorldName());
                player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("nether-teleport"));
            } else {
                player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("no-nether-world"));
            }
            return;
        }
        
        // End Farmwelt
        String endName = plugin.getConfig().getString("menu.end-world.name", "&5End Farmwelt").replace("&", "§");
        if (displayName.equals(endName)) {
            player.closeInventory();
            if (worldUtils.worldExists(plugin.getEndWorldName())) {
                worldUtils.teleportToWorld(player, plugin.getEndWorldName());
                player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("end-teleport"));
            } else {
                player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("no-end-world"));
            }
        }
    }
} 