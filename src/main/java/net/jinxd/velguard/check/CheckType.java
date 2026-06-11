package net.jinxd.velguard.check;

public enum CheckType {
    FLY("Fly", "fly"),
    SPEED("Speed", "speed"),
    NO_FALL("NoFall", "no-fall"),
    KILL_AURA("KillAura", "killaura"),
    REACH("Reach", "reach");

    private final String displayName;
    private final String configKey;

    CheckType(String displayName, String configKey) {
        this.displayName = displayName;
        this.configKey = configKey;
    }

    public String getDisplayName() { return displayName; }
    public String getConfigKey()   { return configKey; }
}
