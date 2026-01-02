# Skyforged Dreams - NeoForge Mod

## Project Overview

This is a Minecraft mod built for NeoForge 1.21.1. The mod is currently in early development (version 0.1.0-alpha).

**Mod ID:** `skyforged_dreams`
**Mod Name:** Skyforged Dreams
**Version:** 0.1.0-alpha
**License:** MIT
**Group ID:** io.github.insomniacteam

## Technical Stack

- **Minecraft Version:** 1.21.1
- **NeoForge Version:** 21.1.217
- **Java Version:** 21
- **Gradle Version:** 8.8
- **Build System:** Gradle with NeoForge ModDev plugin (2.0.136)
- **Mappings:** Parchment 2025.12.20 for Minecraft 1.21.11

## Project Structure

```
skyforged-dreams/
├── src/
│   ├── main/
│   │   ├── java/io/github/insomniacteam/skyforgeddreams/
│   │   │   ├── SkyforgedDreams.java    # Main mod class
│   │   │   └── Config.java             # Configuration handler
│   │   ├── resources/
│   │   │   ├── assets/skyforged_dreams/
│   │   │   │   └── lang/
│   │   │   │       └── en_us.json      # English translations
│   │   │   └── skyforged_dreams.mixins.json  # Mixin configuration
│   │   └── templates/META-INF/
│   │       └── neoforge.mods.toml      # Mod metadata template
│   └── generated/resources/            # Auto-generated resources from data generators
├── build.gradle                        # Gradle build configuration
├── gradle.properties                   # Project properties
└── settings.gradle                     # Gradle settings
```

## Key Files

### Main Mod Class
- **Location:** `src/main/java/io/github/insomniacteam/skyforgeddreams/SkyforgedDreams.java`
- **Purpose:** Entry point for the mod, handles initialization and event registration
- **Features:**
  - Event bus registration
  - Configuration registration
  - Client and server event handlers

### Configuration
- **Location:** `src/main/java/io/github/insomniacteam/skyforgeddreams/Config.java`
- **Type:** Common configuration (loads on both client and server)

### Mixins
- **Config Location:** `src/main/resources/skyforged_dreams.mixins.json`
- **Purpose:** Allows runtime bytecode modifications for compatibility and features

## Development Environment

### Run Configurations

The project includes several pre-configured run tasks:

1. **Client** - Launches the Minecraft client with the mod loaded
2. **Server** - Launches a dedicated server (with `--nogui`)
3. **Data** - Runs data generators for generating mod resources
4. **GameTestServer** - Runs automated game tests

### System Properties

- Game test namespaces: `skyforged_dreams`
- Forge logging markers: `REGISTRIES`
- Log level: `DEBUG`

### Important Gradle Tasks

- `./gradlew runClient` - Run the Minecraft client
- `./gradlew runServer` - Run the dedicated server
- `./gradlew runData` - Generate mod resources
- `./gradlew build` - Build the mod JAR

## Dependencies

Currently using only the NeoForge and Minecraft dependencies:
- NeoForge: version range `[21,)`
- Minecraft: version range `[1.21.1,1.22)`

## Build Configuration

### Memory Settings
- JVM Args: `-Xmx2G`
- Gradle daemon: Enabled
- Parallel builds: Enabled
- Build cache: Enabled
- Configuration cache: Enabled

### Output
- Archives base name: `skyforged_dreams`
- Generated resources included in build

## Development Guidelines

### Code Organization
- Main package: `io.github.insomniacteam.skyforgeddreams`
- Follow Java 21 conventions
- Use NeoForge event system for mod hooks

### Logging
- Use SLF4J logger via `LogUtils.getLogger()`
- Logger available as `LOG` constant in main class

### Configuration
- Common config registered in mod constructor
- Config spec defined in `Config.SPEC`

### Events
- Main event bus: `NeoForge.EVENT_BUS`
- Mod event bus: Injected in constructor
- Client-only events: Use `@EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)`

## Testing

The project supports game tests through the `gameTestServer` run configuration. Tests should be registered under the `skyforged_dreams` namespace.

## Publishing

Maven publishing is configured to output to `./repo` directory. The publication is registered as `mavenJava`.

## IDE Setup

The project is configured for IntelliJ IDEA with:
- Automatic source downloads
- Automatic Javadoc downloads
- Gradle integration

## Current Status

The mod is in early alpha stage with the following features implemented:
- Main mod class initialized
- Configuration system set up
- Event bus registration complete
- Build system configured
- Development environment ready
- **World Epochs System** - Dynamic world state management

## Features

### World Epochs System

The mod features a dynamic world epoch (era) system that changes gameplay over time:

#### Implemented Epochs
- **Age of Wonders** - An epoch of magic and miracles
- **Age of Nightmares** - An epoch of darkness and horror
- **Age of Myths** - An epoch of legends and ancient powers

#### Mechanics
- Epochs automatically cycle every configurable number of in-game days (default: 20)
- System tracks game time (dayTime), so sleeping accelerates progression
- Random transitions between different epochs (never repeats the same epoch)
- Handles time manipulation commands (`/time set`, `/time add`) correctly
- Data persists between world reloads using NBT saved data

#### Configuration
The epoch duration can be configured in `config/skyforged_dreams-common.toml`:

```toml
[epochs]
    # Duration of each epoch in in-game days (default: 20)
    # Range: 1 ~ 1000
    epochDurationDays = 20
```

#### Commands
All commands require OP level 2 (admin/host). The mod uses `/sd` as its main command prefix:

```
/sd epoch info              # Shows current epoch and days remaining
/sd epoch set <epoch_name>  # Forces a specific epoch (wonders/nightmares/myths)
```

Example usage:
```
/sd epoch info
/sd epoch set wonders
/sd epoch set nightmares
/sd epoch set myths
```

#### Technical Implementation
- **Package**: `io.github.insomniacteam.skyforgeddreams.worldstate`
- **Key Classes**:
  - `WorldEpoch` - Enum defining available epochs
  - `EpochManager` - Handles tick updates and epoch transitions
  - `EpochSavedData` - NBT persistence for epoch state
  - `EpochCommand` - Admin commands for epoch management

#### Future Expansion
The epoch system is designed to be extensible. Future updates can add:
- Custom events fired on epoch transitions
- Epoch-specific mob spawns, structures, or mechanics
- Visual effects during epoch changes
- More epochs with unique gameplay features

## Next Steps

This section can be updated as development progresses to track planned features and improvements.

## Notes for AI Assistants

- This is a NeoForge mod, NOT Forge or Fabric - use NeoForge-specific APIs
- Java 21 features are available and should be used where appropriate
- Mixins are configured and can be used for advanced modifications
- Data generators are available via the `runData` task
- The project uses Parchment mappings for better named parameters
- Configuration uses NeoForge's config system (not Forge's old system)
- Events use the NeoForge event bus system
