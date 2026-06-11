package net.jinxd.velguard.listener;

import net.jinxd.velguard.check.combat.KillAuraCheck;
import net.jinxd.velguard.data.DataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CombatListener implements Listener {

    private final DataManager dataManager;
    private final KillAuraCheck killAuraCheck;

    public CombatListener(DataManager dataManager, KillAuraCheck killAuraCheck) {
        this.dataManager = dataManager;
        this.killAuraCheck = killAuraCheck;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        killAuraCheck.check(attacker, dataManager.get(attacker));
    }
}
