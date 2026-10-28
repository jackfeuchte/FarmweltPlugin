# FarmweltPlugin

Ein leistungsstarkes Spigot-Plugin für automatisch resettende Farmwelten. Ideal für Survival-Server, die eine separate, regelmäßig zurückgesetzte Welt für das Farmen von Ressourcen benötigen.

**Offiziell entwickelt von JaxLabs** - Package: `top.jaxlabs.farmweltplugin`

## Features
- 🌍 Separate Farmwelt mit zufälligem Teleport
- ⏰ Automatische Reset-Zeitpläne (täglich, wöchentlich oder monatlich)
- 🎯 Zufällige Teleportation innerhalb der Farmwelt
- 🌱 Rotierende Welt-Seeds für abwechslungsreiche Biome und Strukturen
- 🎨 Support für Custom World Generatoren (z.B. TerraformGenerator)
- 🏷️ PlaceholderAPI-Integration für Anzeige von Reset-Zeiten und Spielerzahlen
- 🛠️ Einfache Konfiguration
- 💻 Multiverse-Core Integration (optional)
- 📊 bStats Integration für anonyme Nutzungsstatistiken

## Voraussetzungen
- Spigot/Paper Server (1.16.5 oder höher)
- Java 8 oder höher
- Optional: Multiverse-Core Plugin für erweiterte Weltenverwaltung
- Optional: PlaceholderAPI für Placeholder-Funktionen

## Installation
1. Lade die neueste Version des Plugins von [hier](https://github.com/jackfeuchte/FarmweltPlugin/releases) herunter
2. Platziere die .jar Datei in deinem Plugins-Ordner
3. Für erweiterte Weltenverwaltung installiere Multiverse-Core (empfohlen, aber nicht zwingend erforderlich)
4. Für Placeholder-Unterstützung installiere PlaceholderAPI
5. Starte deinen Server neu
6. Die Konfigurationsdatei wird automatisch erstellt

## Befehle
- `/farmwelt` (oder `/fw`) - Öffnet das Farmwelt-Menü
- `/farmwelt tp` - Teleportiert dich direkt zur normalen Farmwelt
- `/farmwelt back` - Teleportiert dich zurück (auch im Menü)
- `/nether` - Teleportiert dich zur Nether-Farmwelt
- `/end` - Teleportiert dich zur End-Farmwelt
- `/farmwelt reset` - Setzt die Farmwelt zurück (Admin)
- `/farmwelt reload` - Lädt die Konfiguration neu (Admin)
- `/farmwelt <enable|disable> <nether|end>` - Aktiviert/deaktiviert Nether- oder End-Farmwelt (Admin)
- Admins haben im Menü einen Admin-Knopf: Klick auf eine Welt aktiviert/deaktiviert sie, Shift-Klick setzt sie zurück

## Berechtigungen
- `farmwelt.use` - Erlaubt die Nutzung des /farmwelt Befehls
- `farmwelt.admin` - Erlaubt die Nutzung von Admin-Befehlen

## Placeholders
Das Plugin bietet PlaceholderAPI-Integration für Anzeige von Farmwelt-Informationen:

- `%farmwelt_world%` - Zeigt an, in welcher Welt sich der Spieler befindet
- `%farmwelt_normal_next_reset%` - Zeigt an, wann der nächste Reset der normalen Farmwelt ist
- `%farmwelt_normal_timer%` - Timer, wenn der Reset in der nächsten Stunde ist
- `%farmwelt_normal_players%` - Anzahl der Spieler in der normalen Farmwelt

Für eine vollständige Liste der verfügbaren Placeholders, siehe [PLACEHOLDERS.md](PLACEHOLDERS.md).

## Konfiguration
```yaml
prefix: "&8[&6&lFarmwelt&8]&r "
farmwelt-world: "farmwelt"
random-teleport-distance: 1000
use-bstats: true # Setze auf false, um bStats zu deaktivieren

farmwelt-settings:
  generator: "" # Custom generator name, e.g., "TerraformGenerator"

nether-world-settings:
  generator: ""

end-world-settings:
  generator: ""

# Rotierende Seeds für Farmwelten
rotating-seeds:
  enabled: false # Setze auf true, um rotierende Seeds zu aktivieren
  normal-world-seeds:
    - 1234567890
    - 9876543210
    - 1357924680
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
day-of-week: 1 # 1 (Montag) bis 7 (Sonntag)
day-of-month: 1 # 1 bis 31
cron: "" # Optional, z.B. "0 0 3 ? * MON" (überschreibt daily/weekly/monthly)
```

### Konfigurationsoptionen erklärt
- `prefix`: Der Prefix für Plugin-Nachrichten
- `farmwelt-world`: Name der Farmwelt
- `random-teleport-distance`: Maximale Entfernung vom Spawn beim zufälligen Teleport
- `use-bstats`: Aktiviert oder deaktiviert die bStats-Statistiksammlung
- `rotating-seeds`: Einstellungen für rotierende Welt-Seeds
  - `enabled`: Aktiviert oder deaktiviert die Verwendung von vordefinierten Seeds
  - `normal-world-seeds`: Liste von Seeds für die normale Farmwelt
  - `nether-world-seeds`: Liste von Seeds für die Nether-Farmwelt
  - `end-world-seeds`: Liste von Seeds für die End-Farmwelt
- `reset-schedule`: Einstellungen für automatische Resets
  - `timezone`: Deine Zeitzone
  - `daily`: Aktiviert tägliche Resets
  - `weekly`: Aktiviert wöchentliche Resets
  - `monthly`: Aktiviert monatliche Resets
  - `day-of-week`: Tag für wöchentliche Resets
  - `day-of-month`: Tag für monatliche Resets
  - `cron`: Optionaler Quartz-Cron-Ausdruck (Sekunde Minute Stunde Tag Monat Wochentag), überschreibt daily/weekly/monthly

## Häufige Probleme und Lösungen

### Die Farmwelt wird nicht erstellt
- Stelle sicher, dass der Server genügend Speicherplatz hat
- Prüfe die Konsole auf Fehlermeldungen
- Wenn Multiverse-Core installiert ist, überprüfe, ob es korrekt funktioniert

### Teleport funktioniert nicht
- Überprüfe die Berechtigungen des Spielers
- Stelle sicher, dass die Welt geladen ist
- Prüfe, ob der Weltname in der Konfiguration korrekt ist

## Support
Bei Fragen oder Problemen:
- Erstelle ein [GitHub Issue](https://github.com/jackfeuchte/FarmweltPlugin/issues)
- Kontaktiere mich auf Discord: dervonnebe

## Lizenz
Dieses Plugin hat keine Lizenz. Du kannst machen was du willst.

## Beitragen
Beiträge sind willkommen! Wenn du Verbesserungen vornehmen möchtest:
1. Forke das Repository
2. Erstelle einen Feature Branch
3. Committe deine Änderungen
4. Pushe zum Branch
5. Öffne einen Pull Request

### Entwicklung / Build aus Quellcode
Dieses Projekt verwendet Gradle als Build-System:

```bash
# Repository klonen
git clone https://github.com/jackfeuchte/FarmweltPlugin.git
cd FarmweltPlugin

# Projekt bauen
./gradlew build

# JAR-Datei wird erstellt in: build/libs/FarmweltPlugin-X.X.X.jar
```

Weitere Informationen zur Gradle-Migration findest du in [GRADLE_MIGRATION.md](GRADLE_MIGRATION.md)

## Statistiken
Dieses Plugin verwendet bStats für anonyme Nutzungsstatistiken. Die Statistiken können [hier](https://bstats.org/plugin/bukkit/FarmworldPlugin/24022) eingesehen werden.

Du kannst die Statistikerfassung direkt in der Plugin-Konfiguration deaktivieren, indem du den Wert `use-bstats` auf `false` setzt:
```yaml
use-bstats: false
```

Alternativ kannst du auch die bStats-Konfiguration unter `/plugins/bStats/config.yml` bearbeiten.
