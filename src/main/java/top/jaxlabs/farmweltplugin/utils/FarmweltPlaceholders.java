package top.jaxlabs.farmweltplugin.utils;

import top.jaxlabs.farmweltplugin.Main;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class FarmweltPlaceholders extends PlaceholderExpansion {

    private final Main plugin;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public FarmweltPlaceholders(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "farmwelt";
    }

    @Override
    public String getAuthor() {
        return "dervonnebe";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }
    
    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }
        
        if (identifier.equals("world")) {
            String worldName = player.getWorld().getName();
            if (worldName.equals(plugin.getWorldName())) {
                return plugin.getLanguageString("farmwelt-name");
            } else if (worldName.equals(plugin.getNetherWorldName())) {
                return plugin.getLanguageString("nether-farmwelt-name");
            } else if (worldName.equals(plugin.getEndWorldName())) {
                return plugin.getLanguageString("end-farmwelt-name");
            } else {
                return worldName;
            }
        }
        
        if (identifier.equals("normal_next_reset")) {
            return getNextResetTime(plugin.getWorldName());
        }
        
        if (identifier.equals("nether_next_reset")) {
            return getNextResetTime(plugin.getNetherWorldName());
        }
        
        if (identifier.equals("end_next_reset")) {
            return getNextResetTime(plugin.getEndWorldName());
        }
        
        if (identifier.equals("normal_timer")) {
            return getResetTimer(plugin.getWorldName());
        }
        
        if (identifier.equals("nether_timer")) {
            return getResetTimer(plugin.getNetherWorldName());
        }
        
        if (identifier.equals("end_timer")) {
            return getResetTimer(plugin.getEndWorldName());
        }
        
        if (identifier.equals("normal_players")) {
            return getPlayersInWorld(plugin.getWorldName());
        }
        
        if (identifier.equals("nether_players")) {
            return getPlayersInWorld(plugin.getNetherWorldName());
        }
        
        if (identifier.equals("end_players")) {
            return getPlayersInWorld(plugin.getEndWorldName());
        }
                
        return null;
    }
    
    private String getNextResetTime(String worldName) {
        LocalDateTime nextReset = calculateNextReset(worldName);
        if (nextReset == null) {
            return plugin.getLanguageString("reset-not-scheduled");
        }
        
        return DATE_FORMAT.format(nextReset);
    }
    
    private String getResetTimer(String worldName) {
        LocalDateTime nextReset = calculateNextReset(worldName);
        if (nextReset == null) {
            return "";
        }
        
        LocalDateTime now = LocalDateTime.now(ZoneId.of(plugin.getConfig().getString("reset-schedule.timezone", "Europe/Berlin")));
        Duration duration = Duration.between(now, nextReset);
        
        if (duration.toHours() < 1) {
            long minutes = duration.toMinutes() % 60;
            long seconds = duration.getSeconds() % 60;
            return String.format("%02d:%02d", minutes, seconds);
        }
        
        return "";
    }
    
    private String getPlayersInWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return "0";
        }
        
        return String.valueOf(world.getPlayers().size());
    }
    
    private LocalDateTime calculateNextReset(String worldName) {
        ZonedDateTime cronReset = ScheduledReset.nextCronReset(plugin);
        if (cronReset != null) {
            return cronReset.toLocalDateTime();
        }

        if (!plugin.getConfig().getBoolean("reset-schedule.daily") && 
            !plugin.getConfig().getBoolean("reset-schedule.weekly") && 
            !plugin.getConfig().getBoolean("reset-schedule.monthly")) {
            return null;
        }
        
        LocalDateTime lastReset = plugin.getLastResetTime(worldName);
        if (lastReset == null) {
            return null;
        }
        
        LocalDateTime now = LocalDateTime.now(ZoneId.of(plugin.getConfig().getString("reset-schedule.timezone", "Europe/Berlin")));
        String resetTimeStr = plugin.getConfig().getString("reset-schedule.time", "00:00");
        int resetHour = 0;
        int resetMinute = 0;
        try {
            String[] parts = resetTimeStr.split(":");
            resetHour = Integer.parseInt(parts[0]);
            resetMinute = Integer.parseInt(parts[1]);
        } catch (Exception e) {
            resetHour = 0;
            resetMinute = 0;
        }

        LocalDateTime nextReset = null;

        if (plugin.getConfig().getBoolean("reset-schedule.daily")) {
            nextReset = lastReset.plusDays(1).withHour(resetHour).withMinute(resetMinute).withSecond(0);
        } else if (plugin.getConfig().getBoolean("reset-schedule.weekly")) {
            int targetDayOfWeek = plugin.getConfig().getInt("reset-schedule.day-of-week", 1);
            nextReset = lastReset.plusWeeks(1).withHour(resetHour).withMinute(resetMinute).withSecond(0);
            while (nextReset.getDayOfWeek().getValue() != targetDayOfWeek) {
                nextReset = nextReset.plusDays(1);
            }
        } else if (plugin.getConfig().getBoolean("reset-schedule.monthly")) {
            int targetDayOfMonth = plugin.getConfig().getInt("reset-schedule.day-of-month", 1);
            nextReset = lastReset.plusMonths(1).withDayOfMonth(targetDayOfMonth).withHour(resetHour).withMinute(resetMinute).withSecond(0);
        }

        if (nextReset != null && nextReset.isBefore(now)) {
            if (plugin.getConfig().getBoolean("reset-schedule.daily")) {
                nextReset = now.plusDays(1).withHour(resetHour).withMinute(resetMinute).withSecond(0);
            } else if (plugin.getConfig().getBoolean("reset-schedule.weekly")) {
                int targetDayOfWeek = plugin.getConfig().getInt("reset-schedule.day-of-week", 1);
                nextReset = now.withHour(resetHour).withMinute(resetMinute).withSecond(0);
                while (nextReset.getDayOfWeek().getValue() != targetDayOfWeek || nextReset.isBefore(now)) {
                    nextReset = nextReset.plusDays(1);
                }
            } else if (plugin.getConfig().getBoolean("reset-schedule.monthly")) {
                int targetDayOfMonth = plugin.getConfig().getInt("reset-schedule.day-of-month", 1);
                nextReset = now.withDayOfMonth(targetDayOfMonth).withHour(resetHour).withMinute(resetMinute).withSecond(0);
                if (nextReset.isBefore(now) || now.getDayOfMonth() > targetDayOfMonth) {
                    nextReset = nextReset.plusMonths(1);
                }
            }
        }

        return nextReset;
    }
} 