package net.jinxd.velguard.check.combat;

import net.jinxd.velguard.VelGuard;
import net.jinxd.velguard.alert.AlertManager;
import net.jinxd.velguard.check.Check;
import net.jinxd.velguard.check.CheckType;
import net.jinxd.velguard.data.PlayerData;
import net.jinxd.velguard.punishment.PunishmentManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ReachCheck extends Check {

    private final double baseMaxDist;
    private final double vlPerFlag;
    private final double vlAlert;
    private final double vlPunish;

    public ReachCheck(AlertManager alertManager, PunishmentManager punishmentManager, VelGuard plugin) {
        super(alertManager, punishmentManager);
        this.baseMaxDist = plugin.getConfig().getDouble("checks.reach.max-distance", 3.2);
        this.vlPerFlag   = plugin.getConfig().getDouble("checks.reach.vl-per-flag", 2.0);
        this.vlAlert     = plugin.getConfig().getDouble("checks.reach.vl-alert", 4.0);
        this.vlPunish    = plugin.getConfig().getDouble("checks.reach.vl-punish", 12.0);
    }

    public void check(Player attacker, Entity victim, PlayerData data) {
        double dist = attacker.getLocation().distance(victim.getLocation());
        int ping = Math.min(attacker.getPing(), 300);
        double max = baseMaxDist + ping * 0.0002;

        if (dist > max) {
            double vl = data.addVl(CheckType.REACH, vlPerFlag);
            if (vl >= vlAlert) {
                alertManager.flag(attacker, CheckType.REACH,
                    String.format("dist=%.2f max=%.2f", dist, max), vl);
            }
            if (vl >= vlPunish) {
                punishmentManager.execute(attacker, CheckType.REACH);
                data.resetVl(CheckType.REACH);
            }
        }
    }
}
