package top.jaxlabs.farmweltplugin.utils;

import top.jaxlabs.farmweltplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FarmweltMenu {
    private final Main plugin;
    private final WorldUtils worldUtils;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public FarmweltMenu(Main plugin, WorldUtils worldUtils) {
        this.plugin = plugin;
        this.worldUtils = worldUtils;
    }

    public static final int NORMAL_SLOT = 10;
    public static final int NETHER_SLOT = 13;
    public static final int END_SLOT = 16;
    public static final int ADMIN_SLOT = 18;
    public static final int BACK_SLOT = 22;

    private final Set<UUID> adminMode = new HashSet<>();

    public void openMenu(Player player) {
        adminMode.remove(player.getUniqueId());
        Inventory inventory = Bukkit.createInventory(null, 9 * 3, plugin.getLanguageString("menu-title"));
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, createPane(Material.GRAY_STAINED_GLASS_PANE));
        }
        render(inventory, player);
        player.openInventory(inventory);
    }

    public boolean isAdminMode(Player player) {
        return adminMode.contains(player.getUniqueId());
    }

    public void leaveAdminMode(Player player) {
        adminMode.remove(player.getUniqueId());
    }

    public boolean isAdmin(Player player) {
        return player.hasPermission(plugin.getConfig().getString("admin-permission", "farmwelt.admin"));
    }

    public void toggleAdminMode(Player player, Inventory inventory) {
        boolean admin = !adminMode.remove(player.getUniqueId());
        if (admin) {
            adminMode.add(player.getUniqueId());
        }
        Material pane = admin ? Material.RED_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE;

        new BukkitRunnable() {
            int column = 0;

            @Override
            public void run() {
                if (player.getOpenInventory().getTopInventory() != inventory) {
                    cancel();
                    return;
                }
                for (int row = 0; row < 3; row++) {
                    int slot = row * 9 + column;
                    ItemStack item = inventory.getItem(slot);
                    if (item != null && item.getType().name().endsWith("STAINED_GLASS_PANE")) {
                        inventory.setItem(slot, createPane(pane));
                    }
                }
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 0.5f, 1.0f + column * 0.1f);
                if (++column == 9) {
                    render(inventory, player);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    public void render(Inventory inventory, Player player) {
        boolean admin = isAdminMode(player);
        Material pane = admin ? Material.RED_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE;
        for (int slot : new int[]{NETHER_SLOT, END_SLOT, ADMIN_SLOT, BACK_SLOT}) {
            inventory.setItem(slot, createPane(pane));
        }

        inventory.setItem(NORMAL_SLOT, createWorldItem(
                Material.valueOf(plugin.getConfig().getString("menu.normal-world.material", "GRASS_BLOCK")),
                plugin.getConfig().getString("menu.normal-world.name", "&aFarmwelt"),
                plugin.getWorldName(),
                World.Environment.NORMAL
        ));

        boolean netherEnabled = plugin.getConfig().getBoolean("menu.nether-world.enabled", false);
        if (netherEnabled || admin) {
            inventory.setItem(NETHER_SLOT, createWorldItem(
                    Material.valueOf(plugin.getConfig().getString("menu.nether-world.material", "NETHERRACK")),
                    plugin.getConfig().getString("menu.nether-world.name", "&cNether Farmwelt"),
                    plugin.getNetherWorldName(),
                    World.Environment.NETHER
            ));
        }

        boolean endEnabled = plugin.getConfig().getBoolean("menu.end-world.enabled", false);
        if (endEnabled || admin) {
            inventory.setItem(END_SLOT, createWorldItem(
                    Material.valueOf(plugin.getConfig().getString("menu.end-world.material", "END_STONE")),
                    plugin.getConfig().getString("menu.end-world.name", "&5End Farmwelt"),
                    plugin.getEndWorldName(),
                    World.Environment.THE_END
            ));
        }

        if (admin) {
            addAdminLore(inventory.getItem(NORMAL_SLOT), null);
            addAdminLore(inventory.getItem(NETHER_SLOT), netherEnabled);
            addAdminLore(inventory.getItem(END_SLOT), endEnabled);
        }

        if (isAdmin(player)) {
            ItemStack adminItem = new ItemStack(admin ? Material.BARRIER : Material.COMMAND_BLOCK);
            ItemMeta adminMeta = adminItem.getItemMeta();
            adminMeta.setDisplayName(plugin.getLanguageString(admin ? "menu-admin-exit" : "menu-admin"));
            adminItem.setItemMeta(adminMeta);
            inventory.setItem(ADMIN_SLOT, adminItem);
        }

        if (!admin && plugin.isBackEnabled() && plugin.getBackLocation(player.getUniqueId()) != null) {
            ItemStack backItem = new ItemStack(Material.ENDER_PEARL);
            ItemMeta backMeta = backItem.getItemMeta();
            backMeta.setDisplayName(plugin.getLanguageString("menu-back"));
            backItem.setItemMeta(backMeta);
            inventory.setItem(BACK_SLOT, backItem);
        }
    }

    private void addAdminLore(ItemStack item, Boolean enabled) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore() != null ? meta.getLore() : new ArrayList<>();
        lore.add("");
        if (enabled != null) {
            lore.add(plugin.getLanguageString(enabled ? "menu-admin-status-enabled" : "menu-admin-status-disabled"));
            lore.add(plugin.getLanguageString("menu-admin-toggle"));
        }
        lore.add(plugin.getLanguageString("menu-admin-reset"));
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    private ItemStack createPane(Material material) {
        ItemStack pane = new ItemStack(material);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(" ");
        pane.setItemMeta(meta);
        return pane;
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
        ZonedDateTime cronReset = ScheduledReset.nextCronReset(plugin);
        if (cronReset != null) {
            return cronReset.format(DATE_FORMAT);
        }
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