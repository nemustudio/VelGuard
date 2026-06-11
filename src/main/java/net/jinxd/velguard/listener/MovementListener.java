package net.jinxd.velguard.listener;

import net.jinxd.velguard.check.movement.FlyCheck;
import net.jinxd.velguard.check.movement.NoFallCheck;
import net.jinxd.velguard.check.movement.SpeedCheck;
import net.jinxd.velguard.data.DataManager;
import net.jinxd.velguard.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MovementListener implements Listener {

    private final DataManager dataManager;
    private final FlyCheck flyCheck;
    private final SpeedCheck speedCheck;
    private final NoFallCheck noFallCheck;

    public MovementListener(DataManager dataManager,
                            FlyCheck flyCheck, SpeedCheck speedCheck, NoFallCheck noFallCheck) {
        this.dataManager = dataManager;
        this.flyCheck = flyCheck;
        this.speedCheck = speedCheck;
        this.noFallCheck = noFallCheck;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        dataManager.get(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        dataManager.remove(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (!event.hasChangedPosition()) return;

        Player player = event.getPlayer();
        PlayerData data = dataManager.get(player);

        flyCheck.check(player, data, event.getFrom(), event.getTo());
        speedCheck.check(player, data, event.getFrom(), event.getTo());
        noFallCheck.check(player, data, event.getFrom(), event.getTo());

        data.setWasOnGround(player.isOnGround());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        dataManager.get(player).setExpectingFallDamage(false);
    }
}
