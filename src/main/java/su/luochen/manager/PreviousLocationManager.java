package su.luochen.manager;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 管理玩家传送前的上一个位置（用于 /sback 命令）
 */
public class PreviousLocationManager {

    private final Map<UUID, Location> previousLocations = new HashMap<>();

    /**
     * 保存玩家当前位置（在传送前调用）
     */
    public void saveLocation(Player player) {
        previousLocations.put(player.getUniqueId(), player.getLocation().clone());
    }

    /**
     * 获取玩家传送前的位置
     */
    public Location getPreviousLocation(Player player) {
        return previousLocations.get(player.getUniqueId());
    }

    /**
     * 清除玩家的上一个位置记录
     */
    public void clearLocation(Player player) {
        previousLocations.remove(player.getUniqueId());
    }

    /**
     * 玩家是否有上一个位置记录
     */
    public boolean hasPreviousLocation(Player player) {
        return previousLocations.containsKey(player.getUniqueId());
    }
}
