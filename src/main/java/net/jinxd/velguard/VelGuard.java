package net.jinxd.velguard;

import net.jinxd.velguard.alert.AlertManager;
import net.jinxd.velguard.check.combat.KillAuraCheck;
import net.jinxd.velguard.check.combat.ReachCheck;
import net.jinxd.velguard.check.movement.FlyCheck;
import net.jinxd.velguard.check.movement.NoFallCheck;
import net.jinxd.velguard.check.movement.SpeedCheck;
import net.jinxd.velguard.command.AntiStaffCommand;
import net.jinxd.velguard.command.VelTestCommand;
import net.jinxd.velguard.data.DataManager;
import net.jinxd.velguard.listener.CombatListener;
import net.jinxd.velguard.listener.MovementListener;
import net.jinxd.velguard.punishment.PunishmentManager;
import net.jinxd.velguard.staff.StaffManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class VelGuard extends JavaPlugin {

    private static VelGuard instance;

    private DataManager dataManager;
    private StaffManager staffManager;
    private AlertManager alertManager;
    private PunishmentManager punishmentManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        dataManager       = new DataManager();
        staffManager      = new StaffManager(this);
        punishmentManager = new PunishmentManager(this);
        alertManager      = new AlertManager(this, staffManager);

        FlyCheck      flyCheck      = new FlyCheck(alertManager, punishmentManager, this);
        SpeedCheck    speedCheck    = new SpeedCheck(alertManager, punishmentManager, this);
        NoFallCheck   noFallCheck   = new NoFallCheck(alertManager, punishmentManager, this);
        KillAuraCheck killAuraCheck = new KillAuraCheck(alertManager, punishmentManager, this);
        ReachCheck    reachCheck    = new ReachCheck(alertManager, punishmentManager, this);

        getServer().getPluginManager().registerEvents(
            new MovementListener(dataManager, flyCheck, speedCheck, noFallCheck), this);
        getServer().getPluginManager().registerEvents(
            new CombatListener(dataManager, killAuraCheck, reachCheck), this);

        double decay = getConfig().getDouble("vl-decay-per-second", 0.5);
        getServer().getScheduler().runTaskTimer(this, () ->
            getServer().getOnlinePlayers().forEach(p -> dataManager.get(p).decayVl(decay)),
        20L, 20L);

        var antiStaff = getCommand("antistaff");
        var velTest   = getCommand("veltest");
        if (antiStaff != null) antiStaff.setExecutor(new AntiStaffCommand(staffManager));
        if (velTest != null)   velTest.setExecutor(new VelTestCommand(alertManager));

        getLogger().info("VelGuard " + getPluginMeta().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        staffManager.save();
        getLogger().info("VelGuard disabled.");
    }

    public static VelGuard getInstance() { return instance; }

    public DataManager getDataManager()         { return dataManager; }
    public StaffManager getStaffManager()       { return staffManager; }
    public AlertManager getAlertManager()       { return alertManager; }
    public PunishmentManager getPunishmentManager() { return punishmentManager; }
}
