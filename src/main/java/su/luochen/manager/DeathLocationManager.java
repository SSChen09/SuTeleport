package su.luochen.manager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import su.luochen.SuTeleport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DeathLocationManager {

    private final SuTeleport plugin;
    private final Map<UUID, Location> deathLocations = new HashMap<>();
    private final Set<UUID> burnDeaths = new HashSet<>();

    public DeathLocationManager(SuTeleport plugin) {
        this.plugin = plugin;
    }

    /**
     * 记录玩家死亡位置
     */
    public void setDeathLocation(Player player, Location location, boolean burnDeath) {
        deathLocations.put(player.getUniqueId(), location);
        if (burnDeath) {
            burnDeaths.add(player.getUniqueId());
        }
    }

    /**
     * 获取玩家死亡位置
     */
    public Location getDeathLocation(Player player) {
        return deathLocations.get(player.getUniqueId());
    }

    /**
     * 清除玩家死亡位置（传送后清除）
     */
    public void clearDeathLocation(Player player) {
        UUID uuid = player.getUniqueId();
        deathLocations.remove(uuid);
        burnDeaths.remove(uuid);
    }

    /**
     * 玩家是否有死亡位置记录
     */
    public boolean hasDeathLocation(Player player) {
        return deathLocations.containsKey(player.getUniqueId());
    }

    /**
     * 玩家是否因灼烧（岩浆/火/灵魂火）死亡
     */
    public boolean isBurnDeath(Player player) {
        return burnDeaths.contains(player.getUniqueId());
    }
}
