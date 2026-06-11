package net.jinxd.velguard.check.combat;

import net.jinxd.velguard.alert.AlertManager;
import net.jinxd.velguard.check.Check;
import net.jinxd.velguard.check.CheckType;
import net.jinxd.velguard.data.PlayerData;
import org.bukkit.entity.Player;

public class KillAuraCheck extends Check {

    private final long minIntervalMs;

    public KillAuraCheck(AlertManager alertManager, long minIntervalMs) {
        super(alertManager);
        this.minIntervalMs = minIntervalMs;
    }

    public void check(Player player, PlayerData data) {
        long now = System.currentTimeMillis();
        long last = data.getLastAttackTime();

        float currentYaw = player.getLocation().getYaw();
        float yawDelta = Math.abs(currentYaw - data.getLastYaw());
        if (yawDelta > 180f) yawDelta = 360f - yawDelta;

        data.setLastYaw(currentYaw);

        if (last == 0) {
            data.setLastAttackTime(now);
            return;
        }

        long delta = now - last;
        data.setLastAttackTime(now);

        if (delta < minIntervalMs) {
            long cps = 1000L / Math.max(delta, 1);
            alertManager.flag(player, CheckType.KILL_AURA,
                "cps~" + cps + " delta=" + delta + "ms yaw=" + String.format("%.1f", yawDelta));
        }
    }
}
