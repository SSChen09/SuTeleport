# SuTeleport

> 🌐 **English** | [简体中文](README.md)

A feature-rich Minecraft Spigot teleportation plugin supporting TPA, TPR, Warp, Home and more, with LuckPerms permission integration.

## Features

| Feature | Description |
|------|------|
| **TPA Teleport Request** | Send teleport requests to target players, supports mutual teleportation |
| **TPR Random Teleport** | Teleport to a random safe location on the map |
| **Warp** | Manage global warps, supports set / teleport / delete / list |
| **Home** | Personal home management, supports set / teleport / delete / list |
| **Back to Death Point** | `/back` return to your last death location |
| **Back to Previous** | `/sback` return to the previous location before teleporting |
| **Command Cooldown** | Independent cooldown time for all teleport commands |
| **Permission Management** | Integrates LuckPerms, falls back to Bukkit permissions if not installed |
| **Internationalization (i18n)** | Multi-language support, language files in the `language/` folder, built-in Chinese & English |
| **Hot Reload** | `/suteleport reload` hot-reloads configuration and messages |

## Compatibility

- **Server**: Spigot / Paper 1.21+
- **Java**: 17+
- **Optional Dependency**: LuckPerms (falls back to Bukkit native permissions when not installed)

## Commands

### TPA Teleport

| Command | Description |
|------|------|
| `/tpa <player>` | Send a teleport request to the target (you teleport to the target) |
| `/tpahere <player>` | Send a teleport request to the target (target teleports to you) |
| `/tpaccept` | Accept a teleport request |
| `/tpdeny` | Deny a teleport request |

### TPR Random Teleport

| Command | Description |
|------|------|
| `/tpr` | Teleport to a random safe location near your current position |

### Warp

| Command | Description |
|------|------|
| `/warp <name>` | Teleport to the specified warp |
| `/setwarp <name>` | Set a warp |
| `/delwarp <name>` | Delete a warp |
| `/warps` | List all warps |

### Home

| Command | Description |
|------|------|
| `/home <name>` | Teleport to your home |
| `/sethome [name]` | Set a home (default name "home") |
| `/delhome <name>` | Delete a home |
| `/homes` | List all your homes |

### Other

| Command | Description |
|------|------|
| `/back` | Return to your last death location |
| `/sback` | Return to the previous location before teleporting |
| `/suicide` (alias `/kill`) | Suicide |
| `/suteleport reload` (alias `/stp reload`) | Reload plugin configuration and messages |

## Permissions

| Permission | Default | Description |
|------|------|------|
| `suteleport.tpa` | ✅ All players | Use /tpa |
| `suteleport.tpahere` | ✅ All players | Use /tpahere |
| `suteleport.tpaccept` | ✅ All players | Use /tpaccept |
| `suteleport.tpdeny` | ✅ All players | Use /tpdeny |
| `suteleport.tpr` | ✅ All players | Use /tpr |
| `suteleport.warp` | ✅ All players | Use /warp |
| `suteleport.warp.set` | 🔒 OP | Use /setwarp |
| `suteleport.warp.delete` | 🔒 OP | Use /delwarp |
| `suteleport.warps` | ✅ All players | Use /warps |
| `suteleport.home` | ✅ All players | Use /home |
| `suteleport.home.set` | ✅ All players | Use /sethome |
| `suteleport.home.delete` | ✅ All players | Use /delhome |
| `suteleport.homes` | ✅ All players | Use /homes |
| `suteleport.back` | ✅ All players | Use /back |
| `suteleport.sback` | ✅ All players | Use /sback |
| `suteleport.suicide` | ✅ All players | Use /suicide |
| `suteleport.reload` | 🔒 OP | Use /suteleport reload |
| `suteleport.*` | 🔒 OP | All permissions |

## Configuration

On first run, the plugin automatically generates `config.yml` and the `language/` folder.

```yaml
# Language setting (corresponds to a language file in the language/ folder, e.g. zh_CN / en_US)
language: zh_CN

# Random teleport settings
tpr:
  max-radius: 5000        # Maximum teleport radius (blocks)
  max-height: 256         # Maximum height limit
  max-attempts: 50        # Maximum number of attempts
  allow-end: false        # Whether to allow usage in The End

# TPA request settings
tpa:
  timeout: 60             # Request timeout (seconds)

# Home settings
home:
  max-homes: 3            # Maximum number of homes per player

# Command cooldown (seconds), 0 means no cooldown
cooldown:
  tpa: 0
  tpahere: 0
  tpaccept: 0
  tpr: 0
  warp: 0
  home: 0
  back: 0
  sback: 0
```

### Language Files `language/`

Internationalization is supported: all player-facing messages are stored per language in the `language/` folder. The global language is selected via the `language` key in `config.yml`:

| File | Language |
|------|------|
| `zh_CN.yml` | Simplified Chinese (default) |
| `en_US.yml` | English |

Message structure inside a language file (example from `zh_CN.yml`):

```yaml
messages:
  common:
    only-player: '&cOnly players can use this command!'
    no-permission: '&cYou do not have permission to use this command!'
  home:
    not-exist: '&cHome {0} does not exist!'
    teleported: '&aTeleported to home {0}!'
```

- Supports **`&` color codes** (automatically converted to `§`), e.g. `&c` red, `&a` green, `&e` yellow, `&7` gray.
- Supports **positional placeholders** `{0}` `{1}` `{2}`..., replaced in order by the actual values. Their meaning varies per message; common cases:
  - Player name, home name, warp name (e.g. `{0}` in `home.teleported` = home name)
  - Count, seconds, coordinates (e.g. `{0}` `{1}` `{2}` in `tpr.coords` = X / Y / Z)

**Adding a new language**: copy a language file (e.g. `zh_CN.yml`) and rename it to `<language-code>.yml`, translate the content, then set `language` in `config.yml` to that code and run `/suteleport reload`.

## LuckPerms Integration

The plugin automatically detects LuckPerms:

- **Installed** → checks permissions via the LuckPerms API, supports group permission inheritance
- **Not installed** → automatically falls back to Bukkit native `player.hasPermission()`

LuckPerms usage examples:
```
/lp user Steve permission set suteleport.warp.set true
/lp group vip permission set suteleport.home set-home-limit 10
```

## Data Storage

| File | Purpose |
|------|------|
| `config.yml` | Plugin configuration (including language setting) |
| `language/` | Multi-language message files (zh_CN.yml / en_US.yml, extensible) |
| `warps.yml` | Global warp data |
| `homes.yml` | Player home data (grouped by UUID) |

## Building

```bash
# Clone the repository
git clone <repository-url>
cd SuTeleport

# Build
./gradlew build
```

The build artifact is located at `build/libs/SuTeleport-1.0.jar`.

> 🚀 This repository is configured with **GitHub Actions auto-build**: pushing code or opening a PR triggers an automatic build, and the jar artifact is uploaded to the corresponding Actions run where it can be downloaded directly. Releases are **published manually**.

### Publishing a Release

GitHub Actions only handles building; Releases are published manually:

1. Push code or open a PR — GitHub Actions builds automatically, and the artifact (`SuTeleport-1.0.jar`) can be downloaded from the **Artifacts** of the corresponding run.
2. Open the repository's **Releases** page → **Draft a new release**.
3. Fill in a version tag (e.g. `v1.0`), a title and description, and drag the jar file into the attachment area.
4. Click **Publish release** to finish.

## Installation

1. Put `SuTeleport-1.0.jar` into the server's `plugins/` folder
2. Restart the server or run `reload`
3. Adjust `plugins/SuTeleport/config.yml` and the language files under `plugins/SuTeleport/language/` as needed
