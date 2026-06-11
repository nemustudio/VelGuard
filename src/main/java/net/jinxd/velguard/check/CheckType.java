package net.jinxd.velguard.check;

public enum CheckType {
    FLY("Fly"),
    SPEED("Speed"),
    NO_FALL("NoFall"),
    KILL_AURA("KillAura");

    private final String displayName;

    CheckType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
