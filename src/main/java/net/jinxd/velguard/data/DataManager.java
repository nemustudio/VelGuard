package net.jinxd.velguard.data;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DataManager {

    private final Map<UUID, PlayerData> map = new ConcurrentHashMap<>();

    public PlayerData get(Player player) {
        return map.computeIfAbsent(player.getUniqueId(), PlayerData::new);
    }

    public void remove(Player player) {
        map.remove(player.getUniqueId());
    }
}
