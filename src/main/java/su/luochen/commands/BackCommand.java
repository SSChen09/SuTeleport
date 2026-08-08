package su.luochen.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import su.luochen.SuTeleport;
import su.luochen.manager.CooldownManager;
import su.luochen.manager.DeathLocationManager;
import su.luochen.manager.PermissionManager;

import java.util.Random;

public class BackCommand implements CommandExecutor {

    private static final int BURN_SEARCH_RADIUS = 16;
    private static final int FIRE_RESISTANCE_SECONDS = 30;

    private final SuTeleport plugin;
    private final CooldownManager cooldownManager;
    private final PermissionManager permissionManager;
    private final DeathLocationManager deathLocationManager;
    private final Random random = new Random();

    public BackCommand(SuTeleport plugin, CooldownManager cooldownManager, PermissionManager permissionManager, DeathLocationManager deathLocationManager) {
        this.plugin = plugin;
        this.cooldownManager = cooldownManager;
        this.permissionManager = permissionManager;
        this.deathLocationManager = deathLocationManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "common.only-player");
            return true;
        }

        if (!permissionManager.hasPermission(player, "suteleport.back")) {
            plugin.getMessageManager().send(player, "common.no-permission");
            return true;
        }

        if (!deathLocationManager.hasDeathLocation(player)) {
            plugin.getMessageManager().send(player, "back.no-location");
            return true;
        }

        // 检查冷却
        if (!cooldownManager.checkCooldown(player, "back")) {
            return true;
        }

        Location deathLoc = deathLocationManager.getDeathLocation(player);
        boolean wasBurn = deathLocationManager.isBurnDeath(player);

        // 清除死亡位置记录
        deathLocationManager.clearDeathLocation(player);

        // 保存当前位置
        plugin.getPreviousLocationManager().saveLocation(player);

        if (wasBurn) {
            // 灼烧死亡（岩浆/火/灵魂火）：搜索安全位置
            Location safeLoc = findSafeLocation(deathLoc);
            if (safeLoc != null) {
                player.teleport(safeLoc);
                plugin.getMessageManager().send(player, "back.safety");
            } else {
                // 没有安全位置，给予抗火并传送到死亡位置
                player.teleport(deathLoc);
                player.addPotionEffect(new PotionEffect(
                        PotionEffectType.FIRE_RESISTANCE,
                        FIRE_RESISTANCE_SECONDS * 20, 0, false, true, true));
                plugin.getMessageManager().send(player, "back.fire", FIRE_RESISTANCE_SECONDS);
            }
        } else {
            player.teleport(deathLoc);
            plugin.getMessageManager().send(player, "back.success");
        }

        return true;
    }

    /**
     * 在指定位置周围搜索安全位置
     */
    private Location findSafeLocation(Location center) {
        World world = center.getWorld();
        if (world == null) return null;

        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();

        // 优先搜索Y轴附近，再扩展到最高方块
        for (int dy = 0; dy <= 16; dy++) {
            for (int attempt = 0; attempt < 32; attempt++) {
                int x = cx + random.nextInt(BURN_SEARCH_RADIUS * 2 + 1) - BURN_SEARCH_RADIUS;
                int z = cz + random.nextInt(BURN_SEARCH_RADIUS * 2 + 1) - BURN_SEARCH_RADIUS;

                // 从死亡高度向上和向下搜索
                for (int sign = -1; sign <= 1; sign += 2) {
                    int y = cy + dy * sign;
                    if (y < 1 || y >= world.getMaxHeight()) continue;

                    Location loc = new Location(world, x + 0.5, y + 1, z + 0.5);
                    if (isSafeLocation(loc)) {
                        return loc;
                    }
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

        Material below = world.getBlockAt(x, y - 1, z).getType();
        Material feet = world.getBlockAt(x, y, z).getType();
        Material head = world.getBlockAt(x, y + 1, z).getType();

        // 脚下必须是实体方块，不能是岩浆/火/空气
        if (!below.isSolid() || below == Material.LAVA || below == Material.FIRE || below == Material.SOUL_FIRE) {
            return false;
        }

        // 脚和头的位置必须可通行
        if (feet.isSolid() || feet == Material.LAVA) {
            return false;
        }
        if (head.isSolid() || head == Material.LAVA) {
            return false;
        }

        return true;
    }
}
