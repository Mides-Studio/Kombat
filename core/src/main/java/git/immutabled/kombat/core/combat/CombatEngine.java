package git.immutabled.kombat.core.combat;

import git.immutabled.kombat.api.configuration.KombatConfig;
import git.immutabled.kombat.api.events.defaults.PlayerDamageEvent;
import git.immutabled.kombat.api.player.KombatPlayer;
import git.immutabled.kombat.core.KombatAPIImpl;
import git.immutabled.kombat.core.events.PlayerDamageEventImpl;

import java.time.Clock;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Platform-neutral hit processing, combo tracking and statistics.
 */
public final class CombatEngine {

    private final KombatAPIImpl api;
    private final Clock clock;
    private final ConcurrentMap<HitKey, Long> lastHits = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, Long> lastCombos = new ConcurrentHashMap<>();

    public CombatEngine(KombatAPIImpl api) {
        this(api, Clock.systemUTC());
    }

    CombatEngine(KombatAPIImpl api, Clock clock) {
        this.api = Objects.requireNonNull(api, "api");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public HitResult processHit(
            KombatPlayer attacker,
            KombatPlayer victim,
            double baseDamage,
            PlayerDamageEvent.DamageCause cause,
            boolean critical
    ) {
        Objects.requireNonNull(attacker, "attacker");
        Objects.requireNonNull(victim, "victim");
        Objects.requireNonNull(cause, "cause");
        if (!Double.isFinite(baseDamage) || baseDamage < 0) {
            throw new IllegalArgumentException("Base damage must be positive and finite");
        }
        if (!api.isEnabled() || !attacker.isKombatEnabled() || !victim.isKombatEnabled()) {
            return HitResult.rejected(RejectReason.DISABLED);
        }

        long now = clock.millis();
        KombatConfig config = api.getConfig();
        HitKey hitKey = new HitKey(attacker.getUniqueId(), victim.getUniqueId());
        long hitDelay = Math.max(0, config.get("combat.hit-delay-ms", 450L));
        Long previousHit = lastHits.get(hitKey);
        if (previousHit != null && now - previousHit < hitDelay) {
            return HitResult.rejected(RejectReason.COOLDOWN);
        }

        boolean isCritical = critical && config.get("combat.critical-enabled", true);
        double damage = baseDamage * config.get("combat.base-damage-multiplier", 1.0D);
        if (isCritical) {
            damage *= config.get("combat.critical-damage-multiplier", 1.5D);
        }
        if (attacker.getComboCount() == 0) {
            damage *= config.get("combat.first-hit-damage-boost", 1.0D);
        }

        PlayerDamageEventImpl event = new PlayerDamageEventImpl(
                now,
                attacker,
                victim,
                damage,
                cause,
                isCritical,
                config.get("features.knockback", true)
        );
        api.getEventBus().fire(event);
        if (event.isCancelled()) {
            return HitResult.rejected(RejectReason.CANCELLED);
        }

        lastHits.put(hitKey, now);
        updateCombo(attacker, victim, now, config);
        if (config.isStatisticsEnabled()) {
            attacker.getStatistics().recordHitLanded(event.getDamage());
            victim.getStatistics().recordHitTaken(event.getDamage());
        }
        return HitResult.accepted(event);
    }

    public void recordKill(KombatPlayer killer, KombatPlayer victim) {
        Objects.requireNonNull(victim, "victim");
        victim.setComboCount(0);
        if (!api.getConfig().isStatisticsEnabled()) {
            return;
        }
        victim.getStatistics().recordDeath();
        if (killer != null && !killer.getUniqueId().equals(victim.getUniqueId())) {
            killer.getStatistics().recordKill();
        }
    }

    public void forget(UUID uniqueId) {
        lastCombos.remove(uniqueId);
        lastHits.keySet().removeIf(key ->
                key.attacker().equals(uniqueId) || key.victim().equals(uniqueId)
        );
    }

    public void clear() {
        lastHits.clear();
        lastCombos.clear();
    }

    private void updateCombo(KombatPlayer attacker, KombatPlayer victim, long now, KombatConfig config) {
        victim.setComboCount(0);
        if (!config.isComboSystemEnabled()) {
            attacker.setComboCount(0);
            return;
        }
        Long previousCombo = lastCombos.put(attacker.getUniqueId(), now);
        int nextCombo = previousCombo != null && now - previousCombo <= config.getComboTimeout()
                ? attacker.getComboCount() + 1
                : 1;
        attacker.setComboCount(nextCombo);
    }

    public enum RejectReason {
        DISABLED,
        COOLDOWN,
        CANCELLED
    }

    public record HitResult(PlayerDamageEvent event, RejectReason rejectReason) {
        public static HitResult accepted(PlayerDamageEvent event) {
            return new HitResult(Objects.requireNonNull(event, "event"), null);
        }

        public static HitResult rejected(RejectReason reason) {
            return new HitResult(null, Objects.requireNonNull(reason, "reason"));
        }

        public boolean accepted() {
            return event != null;
        }
    }

    private record HitKey(UUID attacker, UUID victim) {
    }
}
