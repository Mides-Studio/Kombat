# Kombat

Premium Minecraft combat plugin that brings back the classic 1.8 PvP experience to modern Minecraft versions (1.20+).

## 🚀 Installation

1. Download the latest release from [Releases](https://github.com/immutable/kombat/releases)
2. Place the JAR in your server's `plugins` folder
3. Start/restart your server
4. Configure settings in `plugins/Kombat/config.yml`
5. Reload with `/kombat reload`

## ⚙️ Configuration

```yaml
combat:
  # Attack System
  attack-speed: 16.0      # Attack speed multiplier (1.0 = vanilla, 16.0 = 1.8 style)
  enable-sweep: false     # Enable sweep attacks
  attack-reach: 3.0
  attack-reach-sprinting: 3.5

  # Damage System
  base-damage-multiplier: 1.0
  critical-damage-multiplier: 1.5
  first-hit-damage-boost: 1.2

  # Critical Hits
  enable-critical: true
  critical-while-falling: true
  critical-particles: true

  # Hit Detection
  hit-delay: 500                  # ms between hits
  enable-hit-sound: true
  damage-indicator: true

# Knockback settings
# Maybe be added more values in the future
knockback:
  default-profile: "classic"
  profiles:
    classic:
      horizontal: 0.4
      vertical: 0.4
      friction: 0.6
      sprint-multiplier: 1.5

# Feature toggles
features:
  knockback: true
  statistics: true

# Localization
language: en
```

## 📚 Usage

### Getting the API

```java
import git.immutabled.kombat.api.KombatAPI;
import git.immutabled.kombat.api.KombatProvider;

public class YourPlugin extends JavaPlugin {
    
    @Override
    public void onEnable() {
        // Retrieve the api instance
        KombatAPI api = KombatProvider.get();
        
        // Use the API
        api.getCombatManager().setAttackSpeed(20.0);
    }
}
```

### Listening to Events

```java
import git.immutabled.kombat.api.events.defaults.PlayerDamageEvent;

KombatAPI api = KombatProvider.get();

api.getEventBus().register(PlayerDamageEvent.class, event -> {
    // Increase damage for critical hits
    if (event.isCritical()) {
        event.setDamage(event.getDamage() * 1.5);
    }
});
```

### Custom Knockback Profiles

```java
import git.immutabled.kombat.api.knockback.KnockbackProfile;

KnockbackProfile customProfile = KnockbackProfile.builder()
    .name("custom")
    .horizontal(0.5)
    .vertical(0.3)
    .friction(0.7)
    .sprintMultiplier(1.8)
    .build();

api.getCombatManager().setKnockbackProfile(player, customProfile);
```

## 📄 License

This project is proprietary software. All rights reserved.

---
