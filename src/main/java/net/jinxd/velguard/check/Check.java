package net.jinxd.velguard.check;

import net.jinxd.velguard.alert.AlertManager;
import net.jinxd.velguard.punishment.PunishmentManager;

public abstract class Check {

    protected final AlertManager alertManager;
    protected final PunishmentManager punishmentManager;

    protected Check(AlertManager alertManager, PunishmentManager punishmentManager) {
        this.alertManager = alertManager;
        this.punishmentManager = punishmentManager;
    }
}
