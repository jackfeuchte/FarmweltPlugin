# FarmweltPlugin
The perfect solution for automatically resetting farm worlds in Minecraft

**Officially developed by JaxLabs** - Package: `top.jaxlabs.farmweltplugin`

[![German Version](https://img.shields.io/badge/German-Version-red.svg)](https://www.spigotmc.org/resources/farmworld-plugin.115930/)

## ⚡ Features
✨ Separate farm worlds (Normal, Nether, and End)  
⏰ Configurable automatic resets (daily/weekly/monthly)  
🎲 Random teleportation in all farm worlds  
🌱 Rotating world seeds for diverse biomes  
🌍 GUI menu system for world selection  
⚠️ 5-minute warning before reset  
🔄 Safe player teleport system  
🌐 Multi-language support (German/English)  
📊 bStats integration (optional)  
💻 Multiverse-Core integration (optional)  

## Commands
• `/farmwelt` (or `/fw`) - Opens the farm world menu  
• `/farmwelt tp` - Teleports directly to normal farm world  
• `/farmwelt back` - Teleports you back (also in the menu)  
• `/nether` - Teleports to Nether farm world  
• `/end` - Teleports to End farm world  
• `/farmwelt reset` - Resets the farm world (admin)  
• `/farmwelt reload` - Reloads the configuration (admin)  
• `/farmwelt <enable|disable> <nether|end>` - Enables/disables Nether or End farm world (admin)  
• Admins get an admin button in the menu: click a world to enable/disable it, shift-click to reset it  

## Permissions
• `farmwelt.use` - Allows use of the /farmwelt command  
• `farmwelt.admin` - Access to admin commands  

## ⚙️ Configuration
```yaml
prefix: "&8[&6&lFarmwelt&8]&r "
farmwelt-world: "farmwelt"
random-teleport-distance: 1000
language: "en" # de or en

# Rotating seeds for farm worlds
rotating-seeds:
  enabled: false # Set to true to enable rotating seeds
  normal-world-seeds:
    - 1234567890
    - 9876543210
  nether-world-seeds:
    - 1122334455
    - 5544332211
  end-world-seeds:
    - 6677889900
    - 0099887766

reset-schedule:
  timezone: "Europe/Berlin"
  daily: true
  weekly: false
  monthly: false
  day-of-week: 1    # 1 (Monday) to 7 (Sunday)
  day-of-month: 1   # 1 to 31
  cron: ""         # Optional Quartz cron, e.g. "0 0 3 ? * MON" (overrides daily/weekly/monthly)

use-bstats: true # Set to false to disable bStats
```

## Installation
1. Download the latest version from [here](https://github.com/dervonnebe/FarmweltPlugin/releases)
2. Copy the plugin to your plugins folder
3. For enhanced world management, install Multiverse-Core (recommended but not required)
4. For placeholder support, install PlaceholderAPI
5. Restart your server
6. Done!

## ⚠️ Requirements
• Spigot/Paper 1.16.5 or higher  
• Java 8 or higher  
• Optional: Multiverse-Core for enhanced world management  
• Optional: PlaceholderAPI for placeholders  

## Statistics
This plugin uses bStats for anonymous usage statistics.  
https://bstats.org/plugin/bukkit/FarmworldPlugin/24022

## Support
• Discord: dervonnebe
• https://github.com/dervonnebe/FarmweltPlugin/issues

## 🔧 Development / Building from Source
This project uses Gradle as its build system:

```bash
# Clone the repository
git clone https://github.com/dervonnebe/FarmweltPlugin.git
cd FarmweltPlugin

# Build the project
./gradlew build

# JAR file will be created at: build/libs/FarmweltPlugin-1.4.3.jar
```

For more information about the Gradle migration, see [GRADLE_MIGRATION.md](GRADLE_MIGRATION.md)

## ⭐ Ratings
If you like the plugin, please leave a positive rating!

## Updates
Regular updates and improvements planned.

Made with ❤️ by dervonnebe
