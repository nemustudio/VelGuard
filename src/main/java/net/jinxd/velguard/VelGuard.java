package net.jinxd.velguard;

import net.jinxd.velguard.alert.AlertManager;
import net.jinxd.velguard.check.combat.KillAuraCheck;
import net.jinxd.velguard.check.movement.FlyCheck;
import net.jinxd.velguard.check.movement.NoFallCheck;
import net.jinxd.velguard.check.movement.SpeedCheck;
import net.jinxd.velguard.command.AntiStaffCommand;
import net.jinxd.velguard.command.VelTestCommand;
import net.jinxd.velguard.data.DataManager;
import net.jinxd.velguard.listener.CombatListener;
import net.jinxd.velguard.listener.MovementListener;
import net.jinxd.velguard.staff.StaffManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class VelGuard extends JavaPlugin {

    private static VelGuard instance;

    private DataManager dataManager;
    private StaffManager staffManager;
    private AlertManager alertManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        dataManager = new DataManager();
        staffManager = new StaffManager(this);
        alertManager = new AlertManager(this, staffManager);

        int airLimit    = getConfig().getInt("checks.fly.air-tick-limit", 20);
        int speedVl     = getConfig().getInt("checks.speed.violation-limit", 5);
        double speedMax = getConfig().getDouble("checks.speed.base-max", 0.56);
        long kaInterval = getConfig().getLong("checks.killaura.min-interval-ms", 55);

        FlyCheck     flyCheck     = new FlyCheck(alertManager, airLimit);
        SpeedCheck   speedCheck   = new SpeedCheck(alertManager, speedVl, speedMax);
        NoFallCheck  noFallCheck  = new NoFallCheck(alertManager, this);
        KillAuraCheck killAuraCheck = new KillAuraCheck(alertManager, kaInterval);

        getServer().getPluginManager().registerEvents(
            new MovementListener(dataManager, flyCheck, speedCheck, noFallCheck), this);
        getServer().getPluginManager().registerEvents(
            new CombatListener(dataManager, killAuraCheck), this);

        var antiStaff = getCommand("antistaff");
        var velTest   = getCommand("veltest");
        if (antiStaff != null) antiStaff.setExecutor(new AntiStaffCommand(staffManager));
        if (velTest != null)   velTest.setExecutor(new VelTestCommand(alertManager));

        getLogger().info("VelGuard " + getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        staffManager.save();
        getLogger().info("VelGuard disabled.");
    }

    public static VelGuard getInstance() { return instance; }

    public DataManager getDataManager()   { return dataManager; }
    public StaffManager getStaffManager() { return staffManager; }
    public AlertManager getAlertManager() { return alertManager; }
}
