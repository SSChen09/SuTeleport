package su.luochen.manager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import su.luochen.SuTeleport;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeathLocationManager {

    private final SuTeleport plugin;
    private final Map<UUID, Location> deathLocations = new HashMap<>();

    public DeathLocationManager(SuTeleport plugin) {
        this.plugin = plugin;
    }

    /**
     * 记录玩家死亡位置
     */
    public void setDeathLocation(Player player, Location location) {
        deathLocations.put(player.getUniqueId(), location);
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
        deathLocations.remove(player.getUniqueId());
    }

    /**
     * 玩家是否有死亡位置记录
     */
    public boolean hasDeathLocation(Player player) {
        return deathLocations.containsKey(player.getUniqueId());
    }
}
