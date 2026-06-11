# VelGuard

Movement and combat detection plugin for Paper 1.21+.

Monitors fly, speed, nofall, killaura, and reach. All checks use a **violation level (VL)** system — repeated offenses accumulate and decay over time, reducing false positives from lag spikes or edge cases. Alerts are sent to designated staff in real time and written to `alerts.log`.

---

## Requirements

- Paper 1.21 – 1.21.x
- Java 21+

---

## Installation

Drop the jar into your `plugins/` folder and restart.

---

## Checks

| Check | Method |
|---|---|
| **Fly** | Server-side block detection at player bounding box corners. Also flags sustained upward motion while airborne. |
| **Speed** | Horizontal distance per movement event with ping-adjusted threshold. |
| **NoFall** | Detects avoided fall damage via server-side landing confirmation. |
| **KillAura** | Rolling 1-second CPS window. Flags above configurable CPS limit. |
| **Reach** | Attack distance check with per-player ping compensation. |

Ground state is determined server-side (block passability check at player feet) rather than relying on the client-reported value.

---

## Commands

| Command | Permission | Description |
|---|---|---|
| `/antistaff add <player>` | op | Add a player to the alert list |
| `/antistaff remove <player>` | op | Remove a player from the alert list |
| `/antistaff list` | op | Show current alert recipients |
| `/veltest <type> <player>` | op | Send a test alert (`fly` / `speed` / `nofall` / `killaura` / `reach` / `all`) |

Players on the staff list receive alerts even without op. Ops receive alerts by default — disable with `alert-ops: false`.

---

## Config

`plugins/VelGuard/config.yml`

```yaml
alert-ops: true
vl-decay-per-second: 0.5      # VL removed per second when no violations occur

checks:
  fly:
    max-air-events: 20        # consecutive move events airborne before flagging
    vl-per-flag: 2.5
    vl-alert: 5.0             # VL threshold to send an alert
    vl-punish: 15.0           # VL threshold to run punish-command
    punish-command: "kick {player} [VelGuard] Fly detected."
  speed:
    base-max: 0.56            # max horizontal distance per move event
    vl-per-flag: 1.5
    vl-alert: 6.0
    vl-punish: 20.0
    punish-command: "kick {player} [VelGuard] Speed detected."
  killaura:
    max-cps: 16
    vl-per-flag: 3.0
    vl-alert: 5.0
    vl-punish: 18.0
    punish-command: "kick {player} [VelGuard] Combat modifications detected."
  reach:
    max-distance: 3.2         # base max attack reach in blocks
    vl-per-flag: 2.0
    vl-alert: 4.0
    vl-punish: 12.0
    punish-command: "kick {player} [VelGuard] Extended reach detected."
  no-fall:
    vl-per-flag: 3.0
    vl-alert: 3.0             # no punishment for nofall by default
```

Punishment commands support `{player}` as a placeholder and are run as console. Leave `punish-command` blank to alert only.

---

## Alert format

```
[VelGuard] PlayerName » Reach | dist=4.21 max=3.26 (vl:4.0)
```

Alerts include the current VL at the time of the flag. All alerts are also appended to `plugins/VelGuard/alerts.log` with timestamps.

---

## Building

```
mvn clean package
```

Output: `target/velguard-1.1.2.jar`

---

## License

MIT — see [LICENSE](LICENSE)
