# VelGuard

Movement and combat detection plugin for Paper 1.21+.

Monitors fly, speed, nofall, and killaura. Alerts are sent to designated staff members in real time and written to `alerts.log`.

---

## Requirements

- Paper 1.21 – 1.21.x
- Java 21+

---

## Installation

Drop the jar into your `plugins/` folder and restart. That's it.

---

## Commands

| Command | Permission | Description |
|---|---|---|
| `/antistaff add <player>` | op | Add a player to the alert list |
| `/antistaff remove <player>` | op | Remove a player from the alert list |
| `/antistaff list` | op | Show current alert recipients |
| `/veltest <type> <player>` | op | Send a test alert (`fly` / `speed` / `nofall` / `killaura` / `all`) |

Players on the staff list receive alerts even without op. Ops receive alerts by default — turn this off in config.

---

## Config

`plugins/VelGuard/config.yml`

```yaml
# Send alerts to ops who aren't on the staff list
alert-ops: true

checks:
  fly:
    air-tick-limit: 20       # consecutive airborne ticks before flagging
  speed:
    violation-limit: 5       # violations needed to trigger an alert
    base-max: 0.56           # max horizontal distance per move event (sprint ~0.28/tick)
  killaura:
    min-interval-ms: 55      # minimum ms between hits (~18 CPS threshold)
```

---

## Building

```
mvn clean package
```

Output: `target/VelGuard-1.1.2.jar`

---

## Alert format

```
[VelGuard] PlayerName » KillAura | cps~22 delta=44ms yaw=2.3
```

Alerts are also appended to `plugins/VelGuard/alerts.log` with timestamps.

---

## License

MIT — see [LICENSE](LICENSE)
