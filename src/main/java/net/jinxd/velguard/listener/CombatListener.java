package net.jinxd.velguard.listener;

import net.jinxd.velguard.check.combat.KillAuraCheck;
import net.jinxd.velguard.check.combat.ReachCheck;
import net.jinxd.velguard.data.DataManager;
import net.jinxd.velguard.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CombatListener implements Listener {

    private final DataManager dataManager;
    private final KillAuraCheck killAuraCheck;
    private final ReachCheck reachCheck;

    public CombatListener(DataManager dataManager, KillAuraCheck killAuraCheck, ReachCheck reachCheck) {
        this.dataManager   = dataManager;
        this.killAuraCheck = killAuraCheck;
        this.reachCheck    = reachCheck;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        PlayerData data = dataManager.get(attacker);
        killAuraCheck.check(attacker, data);
        reachCheck.check(attacker, event.getEntity(), data);
    }
}
