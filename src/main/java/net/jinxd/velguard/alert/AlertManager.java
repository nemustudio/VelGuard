package net.jinxd.velguard.alert;

import net.jinxd.velguard.VelGuard;
import net.jinxd.velguard.check.CheckType;
import net.jinxd.velguard.staff.StaffManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AlertManager {

    private static final String PREFIX = "<dark_gray>[<red>VelGuard<dark_gray>] ";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final VelGuard plugin;
    private final StaffManager staffManager;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final File logFile;

    public AlertManager(VelGuard plugin, StaffManager staffManager) {
        this.plugin = plugin;
        this.staffManager = staffManager;
        this.logFile = new File(plugin.getDataFolder(), "alerts.log");
    }

    public void flag(Player player, CheckType type, String detail) {
        String rich = PREFIX + "<gray>" + player.getName()
            + " <dark_gray>» <yellow>" + type.getDisplayName()
            + " <dark_gray>| <white>" + detail;
        String plain = "[" + LocalDateTime.now().format(DATE_FMT) + "] ["
            + type.name() + "] " + player.getName() + " - " + detail;

        staffManager.getAll().forEach((uuid, name) -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) p.sendMessage(mm.deserialize(rich));
        });

        if (plugin.getConfig().getBoolean("alert-ops", true)) {
            Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.isOp() && !staffManager.isStaff(p.getUniqueId()))
                .forEach(p -> p.sendMessage(mm.deserialize(rich)));
        }

        writeLog(plain);
    }

    private void writeLog(String line) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(logFile, true))) {
            pw.println(line);
        } catch (IOException e) {
            plugin.getLogger().warning("Alert log write failed: " + e.getMessage());
        }
    }
}
