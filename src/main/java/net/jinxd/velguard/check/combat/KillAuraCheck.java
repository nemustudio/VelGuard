package net.jinxd.velguard.check.combat;

import net.jinxd.velguard.VelGuard;
import net.jinxd.velguard.alert.AlertManager;
import net.jinxd.velguard.check.Check;
import net.jinxd.velguard.check.CheckType;
import net.jinxd.velguard.data.PlayerData;
import net.jinxd.velguard.punishment.PunishmentManager;
import org.bukkit.entity.Player;

public class KillAuraCheck extends Check {

    private final int maxCps;
    private final double vlPerFlag;
    private final double vlAlert;
    private final double vlPunish;

    public KillAuraCheck(AlertManager alertManager, PunishmentManager punishmentManager, VelGuard plugin) {
        super(alertManager, punishmentManager);
        this.maxCps    = plugin.getConfig().getInt("checks.killaura.max-cps", 16);
        this.vlPerFlag = plugin.getConfig().getDouble("checks.killaura.vl-per-flag", 3.0);
        this.vlAlert   = plugin.getConfig().getDouble("checks.killaura.vl-alert", 5.0);
        this.vlPunish  = plugin.getConfig().getDouble("checks.killaura.vl-punish", 18.0);
    }

    public void check(Player player, PlayerData data) {
        data.recordAttack();
        int cps = data.getCps();

        float currentYaw   = player.getLocation().getYaw();
        float currentPitch = player.getLocation().getPitch();
        float yawDelta     = Math.abs(currentYaw - data.getLastYaw());
        if (yawDelta > 180f) yawDelta = 360f - yawDelta;

        data.setLastYaw(currentYaw);
        data.setLastPitch(currentPitch);

        if (cps > maxCps) {
            double vl = data.addVl(CheckType.KILL_AURA, vlPerFlag);
            if (vl >= vlAlert) {
                alertManager.flag(player, CheckType.KILL_AURA,
                    "cps=" + cps + " yaw=" + String.format("%.1f", yawDelta), vl);
            }
            if (vl >= vlPunish) {
                punishmentManager.execute(player, CheckType.KILL_AURA);
                data.resetVl(CheckType.KILL_AURA);
            }
        }
    }
}
