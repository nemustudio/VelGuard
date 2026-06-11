package net.jinxd.velguard.check;

import net.jinxd.velguard.alert.AlertManager;

public abstract class Check {

    protected final AlertManager alertManager;

    protected Check(AlertManager alertManager) {
        this.alertManager = alertManager;
    }
}
