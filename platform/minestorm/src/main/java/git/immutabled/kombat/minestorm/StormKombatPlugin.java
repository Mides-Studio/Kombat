package git.immutabled.kombat.minestorm;

import git.immutabled.kombat.api.events.defaults.PlayerDamageEvent;
import git.immutabled.kombat.api.knockback.KnockbackProfile;
import git.immutabled.kombat.api.platform.Platform;
import git.immutabled.kombat.api.platform.PlatformLoaded;
import git.immutabled.kombat.api.player.KombatPlayer;
import git.immutabled.kombat.core.KombatAPIImpl;
import git.immutabled.kombat.core.combat.CombatEngine;
import git.immutabled.kombat.core.configuration.KombatConfigImpl;
import git.immutabled.kombat.minestorm.commands.StormKnockbackCommand;
import git.immutabled.kombat.minestorm.commands.StormKombatCommand;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.damage.EntityDamage;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.PlayerDeathEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Embeddable Minestom integration.
 *
 * <p>Construct this after {@link MinecraftServer#init()}, call {@link #install()},
 * and close it during host shutdown.</p>
 */
public final class StormKombatPlugin implements AutoCloseable {

    private static final long KILL_CREDIT_WINDOW_MS = Duration.ofSeconds(10).toMillis();

    private final Path dataDirectory;
    private final Predicate<CommandSender> administratorCheck;
    private final Map<UUID, LastAttacker> lastAttackers = new ConcurrentHashMap<>();

    private KombatConfigImpl config;
    private KombatAPIImpl kombat;
    private EventNode<Event> eventNode;
    private StormKombatCommand kombatCommand;
    private StormKnockbackCommand knockbackCommand;
    private MinestomPlayerStore playerStore;
    private boolean installed;

    public StormKombatPlugin(Path dataDirectory) {
        this(dataDirectory, sender -> !(sender instanceof Player));
    }

    /**
     * @param dataDirectory directory containing kombat.properties
     * @param administratorCheck permission hook supplied by the host
     */
    public StormKombatPlugin(Path dataDirectory, Predicate<CommandSender> administratorCheck) {
        this.dataDirectory = Objects.requireNonNull(dataDirectory, "dataDirectory");
        this.administratorCheck = Objects.requireNonNull(administratorCheck, "administratorCheck");
    }

    public synchronized StormKombatPlugin install() {
        if (installed) {
            throw new IllegalStateException("Kombat is already installed");
        }

        config = new KombatConfigImpl(
                MinestomConfigLoader.load(dataDirectory),
                this::reloadConfiguration,
                () -> { }
        );
        String minestomVersion = Objects.requireNonNullElse(
                MinecraftServer.class.getPackage().getImplementationVersion(),
                "embedded"
        );
        kombat = new KombatAPIImpl(
                new Platform(minestomVersion, PlatformLoaded.MINESTOM),
                config
        );
        kombat.publish();
        playerStore = new MinestomPlayerStore(dataDirectory);

        eventNode = EventNode.all("kombat");
        eventNode.addListener(PlayerSpawnEvent.class, event -> connect(event.getPlayer()));
        eventNode.addListener(PlayerDisconnectEvent.class, event -> disconnect(event.getPlayer()));
        eventNode.addListener(EntityAttackEvent.class, this::onAttack);
        eventNode.addListener(PlayerDeathEvent.class, this::onDeath);
        MinecraftServer.getGlobalEventHandler().addChild(eventNode);

        kombatCommand = new StormKombatCommand(this, kombat, administratorCheck);
        knockbackCommand = new StormKnockbackCommand(this, kombat, administratorCheck);
        MinecraftServer.getCommandManager().register(kombatCommand, knockbackCommand);
        MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(this::connect);
        installed = true;
        return this;
    }

    public KombatAPIImpl kombat() {
        if (!installed || kombat == null) {
            throw new IllegalStateException("Kombat has not been installed");
        }
        return kombat;
    }

    public void reload() {
        kombat().reload();
    }

    public void setDefaultProfile(String profileName) {
        MinestomConfigLoader.setDefaultProfile(dataDirectory, profileName);
        reload();
    }

    @Override
    public synchronized void close() {
        if (!installed) {
            return;
        }
        MinecraftServer.getGlobalEventHandler().removeChild(eventNode);
        MinecraftServer.getCommandManager().unregister(kombatCommand);
        MinecraftServer.getCommandManager().unregister(knockbackCommand);
        playerStore.close(kombat.getPlayers());
        lastAttackers.clear();
        kombat.close();
        installed = false;
    }

    private void reloadConfiguration() {
        config.replace(MinestomConfigLoader.load(dataDirectory));
    }

    private void connect(Player player) {
        KombatPlayer kombatPlayer = kombat.registerPlayer(player.getUuid(), player.getUsername());
        playerStore.load(kombatPlayer, kombat);
    }

    private void disconnect(Player player) {
        kombat.getPlayer(player.getUuid()).ifPresent(playerStore::saveAsync);
        kombat.markPlayerOffline(player.getUuid());
        kombat.getCombatEngine().forget(player.getUuid());
        lastAttackers.remove(player.getUuid());
    }

    private void onAttack(EntityAttackEvent event) {
        if (!(event.getEntity() instanceof Player attacker)
                || !(event.getTarget() instanceof Player victim)
                || attacker == victim) {
            return;
        }

        KombatPlayer kombatAttacker = kombat.getPlayer(attacker.getUuid())
                .orElseGet(() -> kombat.registerPlayer(attacker.getUuid(), attacker.getUsername()));
        KombatPlayer kombatVictim = kombat.getPlayer(victim.getUuid())
                .orElseGet(() -> kombat.registerPlayer(victim.getUuid(), victim.getUsername()));
        double baseDamage = attacker.getAttributeValue(Attribute.ATTACK_DAMAGE);
        boolean critical = !attacker.isOnGround() && attacker.getVelocity().y() < 0;
        CombatEngine.HitResult result = kombat.getCombatEngine().processHit(
                kombatAttacker,
                kombatVictim,
                baseDamage,
                PlayerDamageEvent.DamageCause.MELEE,
                critical
        );
        if (!result.accepted()) {
            return;
        }

        PlayerDamageEvent damageEvent = result.event();
        victim.damage(new EntityDamage(attacker, (float) damageEvent.getDamage()));
        if (damageEvent.hasKnockback()) {
            KnockbackProfile profile = kombatVictim.getKnockbackProfile() == null
                    ? kombat.getKnockbackRegistry().defaultProfile()
                    : kombatVictim.getKnockbackProfile();
            applyKnockback(attacker, victim, profile);
        }
        lastAttackers.put(victim.getUuid(), new LastAttacker(attacker.getUuid(), System.currentTimeMillis()));
    }

    private void onDeath(PlayerDeathEvent event) {
        Player victim = event.getPlayer();
        KombatPlayer kombatVictim = kombat.getPlayer(victim.getUuid())
                .orElseGet(() -> kombat.registerPlayer(victim.getUuid(), victim.getUsername()));
        LastAttacker lastAttacker = lastAttackers.remove(victim.getUuid());
        KombatPlayer killer = null;
        if (lastAttacker != null && System.currentTimeMillis() - lastAttacker.timestamp() <= KILL_CREDIT_WINDOW_MS) {
            killer = kombat.getPlayer(lastAttacker.uniqueId()).orElse(null);
        }
        kombat.getCombatEngine().recordKill(killer, kombatVictim);
    }

    private static void applyKnockback(Player attacker, Player victim, KnockbackProfile profile) {
        if (!profile.isAirMovement() && !victim.isOnGround()) {
            return;
        }
        Pos attackerPosition = attacker.getPosition();
        Pos victimPosition = victim.getPosition();
        Vec direction = new Vec(
                victimPosition.x() - attackerPosition.x(),
                0,
                victimPosition.z() - attackerPosition.z()
        );
        if (direction.lengthSquared() < 0.0001) {
            direction = attackerPosition.direction().withY(0);
        }
        direction = direction.normalize();

        double horizontal = profile.getHorizontalKnockback();
        if (profile.isSprintKnockback() && attacker.isSprinting()) {
            horizontal *= profile.getSprintMultiplier();
        }
        Vec current = victim.getVelocity().mul(profile.getFriction());
        Vec velocity = current.add(direction.mul(horizontal))
                .withY(Math.max(profile.getVerticalKnockback(), current.y()));
        if (velocity.length() > profile.getMaxDistance()) {
            velocity = velocity.normalize().mul(profile.getMaxDistance());
        }
        victim.setVelocity(velocity);
    }

    private record LastAttacker(UUID uniqueId, long timestamp) {
    }
}
