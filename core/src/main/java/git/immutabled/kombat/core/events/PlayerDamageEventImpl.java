package git.immutabled.kombat.core.events;

import git.immutabled.kombat.api.events.defaults.PlayerDamageEvent;
import git.immutabled.kombat.api.player.KombatPlayer;

import java.util.Objects;

public final class PlayerDamageEventImpl implements PlayerDamageEvent {

    private final long timestamp;
    private final KombatPlayer attacker;
    private final KombatPlayer victim;
    private final DamageCause cause;
    private double damage;
    private boolean critical;
    private boolean knockback;
    private boolean cancelled;

    public PlayerDamageEventImpl(
            long timestamp,
            KombatPlayer attacker,
            KombatPlayer victim,
            double damage,
            DamageCause cause,
            boolean critical,
            boolean knockback
    ) {
        this.timestamp = timestamp;
        this.attacker = Objects.requireNonNull(attacker, "attacker");
        this.victim = Objects.requireNonNull(victim, "victim");
        this.cause = Objects.requireNonNull(cause, "cause");
        setDamage(damage);
        this.critical = critical;
        this.knockback = knockback;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public KombatPlayer getAttacker() {
        return attacker;
    }

    @Override
    public KombatPlayer getVictim() {
        return victim;
    }

    @Override
    public double getDamage() {
        return damage;
    }

    @Override
    public void setDamage(double damage) {
        if (!Double.isFinite(damage) || damage < 0) {
            throw new IllegalArgumentException("Damage must be positive and finite");
        }
        this.damage = damage;
    }

    @Override
    public DamageCause getCause() {
        return cause;
    }

    @Override
    public boolean isCritical() {
        return critical;
    }

    @Override
    public void setCritical(boolean critical) {
        this.critical = critical;
    }

    @Override
    public boolean hasKnockback() {
        return knockback;
    }

    @Override
    public void setKnockback(boolean knockback) {
        this.knockback = knockback;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
