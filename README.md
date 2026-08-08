# SuTeleport

> 🌐 [English](README_EN.md) | **简体中文**

一个功能全面的 Minecraft Spigot 传送插件，支持 TPA、TPR、Warp、Home 等多种传送方式，集成 LuckPerms 权限管理。

## 功能特性

| 功能 | 说明 |
|------|------|
| **TPA 传送请求** | 向目标玩家发送传送请求，支持互相传送 |
| **TPR 随机传送** | 传送到地图上的随机安全位置 |
| **传送点 (Warp)** | 管理全局传送点，支持设置/传送/删除/列表 |
| **家 (Home)** | 玩家个人家管理，支持设置/传送/删除/列表 |
| **回到死亡点** | `/back` 回到上次死亡的位置 |
| **回到传送前** | `/sback` 回到传送前的上一个位置 |
| **命令冷却** | 所有传送类命令支持独立冷却时间 |
| **权限管理** | 集成 LuckPerms，未安装时自动回退 Bukkit 权限 |
| **国际化 (i18n)** | 多语言支持，语言文件位于 `language/` 目录，默认内置中英文 |
| **热重载** | 支持 `/suteleport reload` 热重载配置与消息 |

## 兼容性

- **服务端**: Spigot / Paper 1.21+
- **Java**: 17+
- **可选依赖**: LuckPerms（未安装时使用 Bukkit 原生权限）

## 命令一览

### TPA 传送

| 命令 | 说明 |
|------|------|
| `/tpa <玩家名>` | 向目标发送传送请求（你传送到目标位置） |
| `/tpahere <玩家名>` | 向目标发送传送请求（目标传送到你这里） |
| `/tpaccept` | 接受传送请求 |
| `/tpdeny` | 拒绝传送请求 |

### TPR 随机传送

| 命令 | 说明 |
|------|------|
| `/tpr` | 传送到当前位置附近的随机安全位置 |

### 传送点 Warp

| 命令 | 说明 |
|------|------|
| `/warp <名称>` | 传送到指定传送点 |
| `/setwarp <名称>` | 设置一个传送点 |
| `/delwarp <名称>` | 删除一个传送点 |
| `/warps` | 列出所有传送点 |

### 家 Home

| 命令 | 说明 |
|------|------|
| `/home <名称>` | 传送到你的家 |
| `/sethome [名称]` | 设置家（默认名称 "home"） |
| `/delhome <名称>` | 删除家 |
| `/homes` | 列出你所有的家 |

### 其他

| 命令 | 说明 |
|------|------|
| `/back` | 回到上一次死亡的位置 |
| `/sback` | 回到传送前的上一个位置 |
| `/suicide` (别名 `/kill`) | 自杀 |
| `/suteleport reload` (别名 `/stp reload`) | 重载插件配置与消息 |

## 权限节点

| 权限 | 默认 | 说明 |
|------|------|------|
| `suteleport.tpa` | ✅ 所有玩家 | 使用 /tpa |
| `suteleport.tpahere` | ✅ 所有玩家 | 使用 /tpahere |
| `suteleport.tpaccept` | ✅ 所有玩家 | 使用 /tpaccept |
| `suteleport.tpdeny` | ✅ 所有玩家 | 使用 /tpdeny |
| `suteleport.tpr` | ✅ 所有玩家 | 使用 /tpr |
| `suteleport.warp` | ✅ 所有玩家 | 使用 /warp |
| `suteleport.warp.set` | 🔒 OP | 使用 /setwarp |
| `suteleport.warp.delete` | 🔒 OP | 使用 /delwarp |
| `suteleport.warps` | ✅ 所有玩家 | 使用 /warps |
| `suteleport.home` | ✅ 所有玩家 | 使用 /home |
| `suteleport.home.set` | ✅ 所有玩家 | 使用 /sethome |
| `suteleport.home.delete` | ✅ 所有玩家 | 使用 /delhome |
| `suteleport.homes` | ✅ 所有玩家 | 使用 /homes |
| `suteleport.back` | ✅ 所有玩家 | 使用 /back |
| `suteleport.sback` | ✅ 所有玩家 | 使用 /sback |
| `suteleport.suicide` | ✅ 所有玩家 | 使用 /suicide |
| `suteleport.reload` | 🔒 OP | 使用 /suteleport reload |
| `suteleport.*` | 🔒 OP | 拥有所有权限 |

## 配置文件

插件首次运行会自动生成 `config.yml` 与 `language/` 语言目录。

```yaml
# 语言设置（对应 language/ 目录下的语言文件，如 zh_CN / en_US）
language: zh_CN

# 随机传送设置
tpr:
  max-radius: 5000        # 最大传送半径（格）
  max-height: 256         # 最大高度限制
  max-attempts: 50        # 最大尝试次数
  allow-end: false        # 是否允许在末地使用

# TPA 传送请求设置
tpa:
  timeout: 60             # 请求超时时间（秒）

# 家设置
home:
  max-homes: 3            # 每个玩家最多可设置的家数量

# 传送类命令冷却时间（秒），0表示无冷却
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

### 语言文件 `language/`

支持国际化，所有发送给玩家的消息按语言存放在 `language/` 目录下，通过 `config.yml` 的 `language` 键选择全局语言：

| 文件 | 语言 |
|------|------|
| `zh_CN.yml` | 简体中文（默认） |
| `en_US.yml` | 英文 |

语言文件内的消息结构（以 `zh_CN.yml` 为例）：

```yaml
messages:
  common:
    only-player: '&c只有玩家才能使用此命令！'
    no-permission: '&c你没有权限使用此命令！'
  home:
    not-exist: '&c家 {0} 不存在！'
    teleported: '&a已传送到家 {0}！'
```

- 支持 **`&` 颜色代码**（自动转换为 `§`），例如 `&c` 红色、`&a` 绿色、`&e` 黄色、`&7` 灰色。
- 支持 **位置占位符** `{0}` `{1}` `{2}`...，按顺序被实际内容替换。不同消息中占位符含义不同，常见为：
  - 玩家名、家名、传送点名（如 `home.teleported` 的 `{0}` = 家名）
  - 数量、秒数、坐标（如 `tpr.coords` 的 `{0}` `{1}` `{2}` = X / Y / Z）

**添加新语言**：复制一份语言文件（如 `zh_CN.yml`）重命名为 `<语言代码>.yml`，翻译内容后将 `config.yml` 的 `language` 改为该语言代码，执行 `/suteleport reload` 即可生效。

## LuckPerms 集成

插件自动检测 LuckPerms：

- **已安装** → 通过 LuckPerms API 检查权限，支持组权限继承
- **未安装** → 自动回退 Bukkit 原生 `player.hasPermission()`

LuckPerms 使用示例：
```
/lp user Steve permission set suteleport.warp.set true
/lp group vip permission set suteleport.home set-home-limit 10
```

## 数据存储

| 文件 | 用途 |
|------|------|
| `config.yml` | 插件功能配置（含语言设置） |
| `language/` | 多语言消息文件（zh_CN.yml / en_US.yml，可自行添加） |
| `warps.yml` | 全局传送点数据 |
| `homes.yml` | 玩家家数据（按 UUID 分组） |

## 构建

```bash
# 克隆项目
git clone <仓库地址>
cd SuTeleport

# 构建
./gradlew build
```

构建产物位于 `build/libs/SuTeleport-1.2.jar`。

## 安装

1. 将 `SuTeleport-1.2.jar` 放入服务器 `plugins/` 目录
2. 重启服务器或执行 `reload`
3. 根据需要修改 `plugins/SuTeleport/config.yml` 与 `plugins/SuTeleport/language/` 下的语言文件
