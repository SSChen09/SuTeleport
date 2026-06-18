package su.luochen.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import su.luochen.SuTeleport;
import su.luochen.manager.CooldownManager;
import su.luochen.manager.PermissionManager;

import java.util.Random;

public class TprCommand implements CommandExecutor {

    private final SuTeleport plugin;
    private final CooldownManager cooldownManager;
    private final PermissionManager permissionManager;
    private final Random random = new Random();
    private final int maxRadius;
    private final int maxHeight;
    private final int maxAttempts;
    private final boolean allowEnd;

    public TprCommand(SuTeleport plugin, CooldownManager cooldownManager, PermissionManager permissionManager) {
        this.plugin = plugin;
        this.cooldownManager = cooldownManager;
        this.permissionManager = permissionManager;
        this.maxRadius = plugin.getConfig().getInt("tpr.max-radius", 5000);
        this.maxHeight = plugin.getConfig().getInt("tpr.max-height", 256);
        this.maxAttempts = plugin.getConfig().getInt("tpr.max-attempts", 50);
        this.allowEnd = plugin.getConfig().getBoolean("tpr.allow-end", false);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c只有玩家才能使用此命令！");
            return true;
        }

        if (!permissionManager.hasPermission(player, "suteleport.tpr")) {
            player.sendMessage("§c你没有权限使用此命令！");
            return true;
        }

        // 检查是否在末地
        if (!allowEnd && player.getWorld().getEnvironment() == World.Environment.THE_END) {
            player.sendMessage("§c末地中无法使用随机传送！");
            return true;
        }

        // 检查冷却
        if (!cooldownManager.checkCooldown(player, "tpr")) {
            return true;
        }

        player.sendMessage("§e正在为你寻找随机位置...");

        // 异步寻找安全位置
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Location safeLoc = findSafeLocation(player);
            if (safeLoc != null) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    plugin.getPreviousLocationManager().saveLocation(player);
                    player.teleport(safeLoc);
                    player.sendMessage("§a已将你传送到随机位置！");
                    player.sendMessage("§7坐标: " + safeLoc.getBlockX() + ", " + safeLoc.getBlockY() + ", " + safeLoc.getBlockZ());
                });
            } else {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    player.sendMessage("§c无法找到安全的随机位置，请重试！");
                });
            }
        });

        return true;
    }

    private Location findSafeLocation(Player player) {
        World world = player.getWorld();
        int centerX = player.getLocation().getBlockX();
        int centerZ = player.getLocation().getBlockZ();

        for (int attempts = 0; attempts < maxAttempts; attempts++) {
            int x = centerX + random.nextInt(maxRadius * 2) - maxRadius;
            int z = centerZ + random.nextInt(maxRadius * 2) - maxRadius;
            int y = world.getHighestBlockYAt(x, z);

            if (y < maxHeight && y > 0) {
                Location loc = new Location(world, x + 0.5, y + 1, z + 0.5);
                if (isSafeLocation(loc)) {
                    return loc;
                }
            }
        }
        return null;
    }

    private boolean isSafeLocation(Location loc) {
        World world = loc.getWorld();
        if (world == null) return false;

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        Material feet = world.getBlockAt(x, y, z).getType();
        Material below = world.getBlockAt(x, y - 1, z).getType();

        // 检查脚下是否安全（非空气、非岩浆、非火焰等）
        if (below.isAir() || below == Material.LAVA || below == Material.FIRE) {
            return false;
        }

        // 检查脚和头的位置是否可通行
        if (!feet.isAir() && !feet.isTransparent()) {
            return false;
        }

        Material head = world.getBlockAt(x, y + 1, z).getType();
        if (!head.isAir() && !head.isTransparent()) {
            return false;
        }

        return true;
    }
}
