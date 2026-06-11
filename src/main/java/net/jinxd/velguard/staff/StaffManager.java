package net.jinxd.velguard.staff;

import net.jinxd.velguard.VelGuard;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class StaffManager {

    private final VelGuard plugin;
    private final Map<UUID, String> staff = new LinkedHashMap<>();
    private final File file;
    private YamlConfiguration config;

    public StaffManager(VelGuard plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "staff.yml");
        load();
    }

    private void load() {
        plugin.getDataFolder().mkdirs();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Could not create staff.yml: " + e.getMessage());
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("staff");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                try {
                    staff.put(UUID.fromString(key), section.getString(key, key));
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public boolean add(UUID uuid, String name) {
        if (staff.containsKey(uuid)) return false;
        staff.put(uuid, name);
        return true;
    }

    public boolean remove(UUID uuid) {
        return staff.remove(uuid) != null;
    }

    public boolean isStaff(UUID uuid) {
        return staff.containsKey(uuid);
    }

    public Map<UUID, String> getAll() {
        return staff;
    }

    public void save() {
        config.set("staff", null);
        ConfigurationSection section = config.createSection("staff");
        staff.forEach((uuid, name) -> section.set(uuid.toString(), name));
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save staff.yml: " + e.getMessage());
        }
    }
}
