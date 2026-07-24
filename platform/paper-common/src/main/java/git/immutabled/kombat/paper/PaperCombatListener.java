package git.immutabled.kombat.paper;

import git.immutabled.kombat.api.events.defaults.PlayerDamageEvent;
import git.immutabled.kombat.api.knockback.KnockbackProfile;
import git.immutabled.kombat.api.player.KombatPlayer;
import git.immutabled.kombat.core.KombatAPIImpl;
import git.immutabled.kombat.core.combat.CombatEngine;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

final class PaperCombatListener implements Listener {

    private final AbstractPaperKombatPlugin plugin;
    private final KombatAPIImpl api;
    private final PlayerDataStore playerDataStore;
    private final Map<UUID, Double> originalAttackSpeeds = new ConcurrentHashMap<>();

    PaperCombatListener(
            AbstractPaperKombatPlugin plugin,
            KombatAPIImpl api,
            PlayerDataStore playerDataStore
    ) {
        this.plugin = plugin;
        this.api = api;
        this.playerDataStore = playerDataStore;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        connect(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        api.getPlayer(event.getPlayer().getUniqueId()).ifPresent(playerDataStore::saveAsync);
        api.markPlayerOffline(event.getPlayer().getUniqueId());
        api.getCombatEngine().forget(event.getPlayer().getUniqueId());
        originalAttackSpeeds.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }
        Player attacker = resolveAttacker(event.getDamager());
        if (attacker == null || attacker.getUniqueId().equals(victim.getUniqueId())) {
            return;
        }
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK
                && !api.getConfig().isSweepEnabled()) {
            event.setCancelled(true);
            return;
        }

        KombatPlayer kombatAttacker = api.getPlayer(attacker.getUniqueId())
                .orElseGet(() -> api.registerPlayer(attacker.getUniqueId(), attacker.getName()));
        KombatPlayer kombatVictim = api.getPlayer(victim.getUniqueId())
                .orElseGet(() -> api.registerPlayer(victim.getUniqueId(), victim.getName()));
        boolean critical = event.getDamager() instanceof Player && isCritical(attacker);
        CombatEngine.HitResult result = api.getCombatEngine().processHit(
                kombatAttacker,
                kombatVictim,
                event.getDamage(),
                mapCause(event),
                critical
        );

        if (!result.accepted()) {
            if (result.rejectReason() != CombatEngine.RejectReason.DISABLED) {
                event.setCancelled(true);
            }
            return;
        }

        PlayerDamageEvent kombatEvent = result.event();
        event.setDamage(kombatEvent.getDamage());
        if (kombatEvent.isCritical() && api.getConfig().get("combat.critical-particles", true)) {
            victim.getWorld().spawnParticle(
                    Particle.CRIT,
                    victim.getLocation().add(0, victim.getHeight() * 0.65, 0),
                    8,
                    0.25,
                    0.25,
                    0.25,
                    0.08
            );
        }
        if (api.getConfig().get("combat.hit-sound", true)) {
            attacker.playSound(attacker.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 0.45F, 1.15F);
        }
        if (kombatEvent.hasKnockback()) {
            KnockbackProfile profile = kombatVictim.getKnockbackProfile() != null
                    ? kombatVictim.getKnockbackProfile()
                    : api.getKnockbackRegistry().defaultProfile();
            scheduleEntity(victim, () -> applyKnockback(attacker, victim, profile), 1);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getPlayer();
        KombatPlayer kombatVictim = api.getPlayer(victim.getUniqueId())
                .orElseGet(() -> api.registerPlayer(victim.getUniqueId(), victim.getName()));
        Player killer = victim.getKiller();
        KombatPlayer kombatKiller = killer == null
                ? null
                : api.getPlayer(killer.getUniqueId())
                        .orElseGet(() -> api.registerPlayer(killer.getUniqueId(), killer.getName()));
        api.getCombatEngine().recordKill(kombatKiller, kombatVictim);
    }

    void connect(Player player) {
        KombatPlayer kombatPlayer = api.registerPlayer(player.getUniqueId(), player.getName());
        playerDataStore.load(kombatPlayer, api);
        applyAttackSpeed(player);
    }

    void refreshOnlinePlayers() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            scheduleEntity(player, () -> applyAttackSpeed(player), 0);
        }
    }

    void restoreAllAttackSpeeds() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Double original = originalAttackSpeeds.remove(player.getUniqueId());
            if (original != null) {
                scheduleEntity(player, () -> setAttackSpeed(player, original), 0);
            }
        }
    }

    private void applyAttackSpeed(Player player) {
        AttributeInstance attribute = player.getAttribute(Attribute.ATTACK_SPEED);
        if (attribute == null) {
            return;
        }
        originalAttackSpeeds.putIfAbsent(player.getUniqueId(), attribute.getBaseValue());
        attribute.setBaseValue(api.getConfig().getAttackSpeed());
    }

    private static void setAttackSpeed(Player player, double value) {
        AttributeInstance attribute = player.getAttribute(Attribute.ATTACK_SPEED);
        if (attribute != null) {
            attribute.setBaseValue(value);
        }
    }

    private void applyKnockback(Player attacker, Player victim, KnockbackProfile profile) {
        if (!victim.isValid() || victim.isDead() || (!profile.isAirMovement() && !victim.isOnGround())) {
            return;
        }

        Vector direction = victim.getLocation().toVector().subtract(attacker.getLocation().toVector());
        direction.setY(0);
        if (direction.lengthSquared() < 0.0001) {
            direction = attacker.getLocation().getDirection().setY(0);
        }
        direction.normalize();

        double horizontal = profile.getHorizontalKnockback();
        if (profile.isSprintKnockback() && attacker.isSprinting()) {
            horizontal *= profile.getSprintMultiplier();
        }

        Vector current = victim.getVelocity().multiply(profile.getFriction());
        Vector velocity = current.add(direction.multiply(horizontal));
        velocity.setY(Math.max(profile.getVerticalKnockback(), current.getY()));
        if (velocity.length() > profile.getMaxDistance()) {
            velocity.normalize().multiply(profile.getMaxDistance());
        }
        victim.setVelocity(velocity);
    }

    private void scheduleEntity(Entity entity, Runnable action, long delayTicks) {
        try {
            if (delayTicks <= 0) {
                entity.getScheduler().run(plugin, ignored -> action.run(), null);
            } else {
                entity.getScheduler().runDelayed(plugin, ignored -> action.run(), null, delayTicks);
            }
        } catch (NoSuchMethodError ignored) {
            if (delayTicks <= 0) {
                plugin.getServer().getScheduler().runTask(plugin, action);
            } else {
                plugin.getServer().getScheduler().runTaskLater(plugin, action, delayTicks);
            }
        }
    }

    private static Player resolveAttacker(Entity damager) {
        if (damager instanceof Player player) {
            return player;
        }
        if (damager instanceof Projectile projectile) {
            ProjectileSource shooter = projectile.getShooter();
            if (shooter instanceof Player player) {
                return player;
            }
        }
        return null;
    }

    private static boolean isCritical(Player attacker) {
        return attacker.getFallDistance() > 0
                && !attacker.isOnGround()
                && !attacker.isInsideVehicle()
                && !attacker.isGliding()
                && !attacker.hasPotionEffect(PotionEffectType.BLINDNESS);
    }

    private static PlayerDamageEvent.DamageCause mapCause(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            return PlayerDamageEvent.DamageCause.SWEEP;
        }
        if (event.getDamager() instanceof Projectile) {
            return PlayerDamageEvent.DamageCause.PROJECTILE;
        }
        return PlayerDamageEvent.DamageCause.MELEE;
    }
}
