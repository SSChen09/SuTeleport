package su.luochen.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import su.luochen.SuTeleport;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TpaManager {

    private final SuTeleport plugin;
    private final PreviousLocationManager previousLocationManager;
    // key: 请求接收者UUID, value: TpaRequest
    private final Map<UUID, TpaRequest> pendingRequests = new HashMap<>();
    private final long requestTimeoutTicks;

    public TpaManager(SuTeleport plugin, PreviousLocationManager previousLocationManager) {
        this.plugin = plugin;
        this.previousLocationManager = previousLocationManager;
        int timeoutSeconds = plugin.getConfig().getInt("tpa.timeout", 60);
        this.requestTimeoutTicks = timeoutSeconds * 20L;
    }

    /**
     * 发送传送请求
     * @param sender 发送者
     * @param target 目标玩家
     * @param here true表示tpahere(把目标传送到自己), false表示tpa(自己传送到目标)
     */
    public void sendRequest(Player sender, Player target, boolean here) {
        UUID targetId = target.getUniqueId();

        // 取消该目标之前的请求
        if (pendingRequests.containsKey(targetId)) {
            cancelRequest(targetId);
        }

        TpaRequest request = new TpaRequest(sender.getUniqueId(), targetId, here);
        pendingRequests.put(targetId, request);

        // 60秒后自动过期
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (pendingRequests.containsKey(targetId)
                    && pendingRequests.get(targetId).equals(request)) {
                pendingRequests.remove(targetId);
                Player s = Bukkit.getPlayer(sender.getUniqueId());
                Player t = Bukkit.getPlayer(targetId);
                if (s != null) {
                    s.sendMessage("§c你的传送请求已超时！");
                }
                if (t != null) {
                    t.sendMessage("§c来自 " + sender.getName() + " 的传送请求已超时！");
                }
            }
        }, requestTimeoutTicks);
    }

    /**
     * 接受传送请求
     * @param target 接受请求的玩家
     * @return 是否成功接受
     */
    public boolean acceptRequest(Player target) {
        TpaRequest request = pendingRequests.remove(target.getUniqueId());
        if (request == null) {
            return false;
        }

        Player sender = Bukkit.getPlayer(request.getSender());
        if (sender == null || !sender.isOnline()) {
            target.sendMessage("§c发送请求的玩家已离线！");
            return false;
        }

        if (request.isHere()) {
            // tpahere: 把发送者传送到目标
            previousLocationManager.saveLocation(sender);
            sender.teleport(target.getLocation());
            sender.sendMessage("§a你已传送到 " + target.getName() + " 的位置！");
            target.sendMessage("§a你已接受 " + sender.getName() + " 的传送请求！");
        } else {
            // tpa: 把目标传送到发送者的位置（实际上是发送者传送到目标）
            // 标准TPA: 发送者传送到目标
            previousLocationManager.saveLocation(sender);
            sender.teleport(target.getLocation());
            sender.sendMessage("§a你已传送到 " + target.getName() + " 的位置！");
            target.sendMessage("§a你已接受 " + sender.getName() + " 的传送请求！");
        }

        return true;
    }

    /**
     * 拒绝传送请求
     * @param target 拒绝请求的玩家
     * @return 是否成功拒绝
     */
    public boolean denyRequest(Player target) {
        TpaRequest request = pendingRequests.remove(target.getUniqueId());
        if (request == null) {
            return false;
        }

        Player sender = Bukkit.getPlayer(request.getSender());
        if (sender != null && sender.isOnline()) {
            sender.sendMessage("§c" + target.getName() + " 拒绝了你的传送请求！");
        }
        target.sendMessage("§c你已拒绝传送请求。");

        return true;
    }

    private void cancelRequest(UUID targetId) {
        TpaRequest old = pendingRequests.remove(targetId);
        if (old != null) {
            Player sender = Bukkit.getPlayer(old.getSender());
            if (sender != null && sender.isOnline()) {
                sender.sendMessage("§c你的传送请求已被新的请求覆盖。");
            }
        }
    }

    public boolean hasRequest(UUID targetId) {
        return pendingRequests.containsKey(targetId);
    }
}
