package top.jaxlabs.farmweltplugin.commands;

import top.jaxlabs.farmweltplugin.Main;
import top.jaxlabs.farmweltplugin.utils.FarmweltMenu;
import top.jaxlabs.farmweltplugin.utils.WorldUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FarmweltCommand implements CommandExecutor, TabCompleter {
    private final Main plugin;
    private final WorldUtils worldUtils;
    private final FarmweltMenu farmweltMenu;

    public FarmweltCommand(Main plugin, WorldUtils worldUtils, FarmweltMenu farmweltMenu) {
        this.plugin = plugin;
        this.worldUtils = worldUtils;
        this.farmweltMenu = farmweltMenu;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("command-player-only"));
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("menu")) {
            if (!player.hasPermission(plugin.getConfig().getString("default-permission", "farmwelt.use"))) {
                player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("no-permission"));
                return true;
            }

            farmweltMenu.openMenu(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("tp")) {
            if (!player.hasPermission(plugin.getConfig().getString("default-permission", "farmwelt.use"))) {
                player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("no-permission"));
                return true;
            }

            // Check cooldown
            if (plugin.getCooldownManager().hasCooldown(player.getUniqueId())) {
                long remaining = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId());
                String message = plugin.getLanguageString("cooldown-active");
                message = message.replace("%seconds%", String.valueOf(remaining));
                player.sendMessage(plugin.getLanguageString("prefix") + message);
                return true;
            }

            if (worldUtils.worldExists(plugin.getWorldName())) {
                worldUtils.teleportToWorld(player, plugin.getWorldName());
                plugin.getCooldownManager().setCooldown(player.getUniqueId());
                player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("world-teleport"));
            } else {
                player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("no-world"));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("reset")) {
            if (!player.hasPermission(plugin.getConfig().getString("admin-permission", "farmwelt.admin"))) {
                player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("no-permission"));
                return true;
            }

            if (args.length >= 2) {
                String worldType = args[1].toLowerCase();
                
                if (worldType.equals("normal")) {
                    if (worldUtils.worldExists(plugin.getWorldName())) {
                        worldUtils.resetWorld(plugin.getWorldName());
                        player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("admin-reset-normal"));
                    } else {
                        player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("no-world"));
                    }
                } else if (worldType.equals("nether")) {
                    if (worldUtils.worldExists(plugin.getNetherWorldName())) {
                        worldUtils.resetWorld(plugin.getNetherWorldName());
                        player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("admin-reset-nether"));
                    } else {
                        player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("no-nether-world"));
                    }
                } else if (worldType.equals("end")) {
                    if (worldUtils.worldExists(plugin.getEndWorldName())) {
                        worldUtils.resetWorld(plugin.getEndWorldName());
                        player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("admin-reset-end"));
                    } else {
                        player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("no-end-world"));
                    }
                } else {
                    player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("invalid-world-type"));
                }
                
                return true;
            }

            if (worldUtils.worldExists(plugin.getWorldName())) {
                worldUtils.resetWorld(plugin.getWorldName());
                player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("admin-reset"));
            } else {
                player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("no-world"));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission(plugin.getConfig().getString("admin-permission", "farmwelt.admin"))) {
                player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("no-permission"));
                return true;
            }

            plugin.reloadConfig();
            player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("admin-reload"));
            return true;
        }

        if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("disable")) {
            if (!player.hasPermission(plugin.getConfig().getString("admin-permission", "farmwelt.admin"))) {
                player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("no-permission"));
                return true;
            }

            if (args.length < 2 || !(args[1].equalsIgnoreCase("nether") || args[1].equalsIgnoreCase("end"))) {
                player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("toggle-usage"));
                return true;
            }

            String type = args[1].toLowerCase();
            boolean enable = args[0].equalsIgnoreCase("enable");
            plugin.getConfig().set("menu." + type + "-world.enabled", enable);
            plugin.saveConfig();

            if (enable) {
                String worldName = type.equals("nether") ? plugin.getNetherWorldName() : plugin.getEndWorldName();
                if (worldUtils.worldExists(worldName)) {
                    worldUtils.loadWorld(worldName);
                } else {
                    worldUtils.createWorld(worldName, type.equals("nether") ? World.Environment.NETHER : World.Environment.THE_END);
                    plugin.updateLastResetTime(worldName);
                }
            }

            player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString(type + (enable ? "-enabled" : "-disabled")));
            return true;
        }

        if (args[0].equalsIgnoreCase("back")) {
            if (!plugin.isBackEnabled()) {
                player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("back-disabled"));
                return true;
            }
            Location backLoc = plugin.getBackLocation(player.getUniqueId());
            if (backLoc != null) {
                player.teleport(backLoc);
                plugin.clearBackLocation(player.getUniqueId());
                player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("back-success"));
            } else {
                player.sendMessage(plugin.getLanguageString("prefix") + plugin.getLanguageString("back-no-location"));
            }
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.add("menu");
            completions.add("tp");
            if (plugin.isBackEnabled()) {
                completions.add("back");
            }
            if (sender.hasPermission(plugin.getConfig().getString("admin-permission", "farmwelt.admin"))) {
                completions.add("reset");
                completions.add("reload");
                completions.add("enable");
                completions.add("disable");
            }
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("disable")) &&
                sender.hasPermission(plugin.getConfig().getString("admin-permission", "farmwelt.admin"))) {
            completions.add("nether");
            completions.add("end");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("reset") && 
                sender.hasPermission(plugin.getConfig().getString("admin-permission", "farmwelt.admin"))) {
            completions.add("normal");
            if (plugin.getConfig().getBoolean("menu.nether-world.enabled", false)) {
                completions.add("nether");
            }
            if (plugin.getConfig().getBoolean("menu.end-world.enabled", false)) {
                completions.add("end");
            }
        }
        
        return completions;
    }
}