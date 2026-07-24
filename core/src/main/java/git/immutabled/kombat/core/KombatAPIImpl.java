package git.immutabled.kombat.core;

import git.immutabled.kombat.api.KombatAPI;
import git.immutabled.kombat.api.KombatProvider;
import git.immutabled.kombat.api.configuration.KombatConfig;
import git.immutabled.kombat.api.events.EventBus;
import git.immutabled.kombat.api.platform.Platform;
import git.immutabled.kombat.api.player.KombatPlayer;
import git.immutabled.kombat.core.events.EventBusImpl;
import git.immutabled.kombat.core.knockback.KnockbackRegistry;
import git.immutabled.kombat.core.player.PlayerRegistry;
import git.immutabled.kombat.core.combat.CombatEngine;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Default runtime implementation shared by every platform adapter.
 */
public final class KombatAPIImpl implements KombatAPI, AutoCloseable {

    private final Platform platform;
    private final KombatConfig config;
    private final EventBus eventBus;
    private final PlayerRegistry playerRegistry;
    private final KnockbackRegistry knockbackRegistry;
    private final CombatEngine combatEngine;
    private volatile boolean enabled = true;

    public KombatAPIImpl(Platform platform, KombatConfig config) {
        this.platform = Objects.requireNonNull(platform, "platform");
        this.config = Objects.requireNonNull(config, "config");
        this.eventBus = new EventBusImpl();
        this.playerRegistry = new PlayerRegistry();
        this.knockbackRegistry = new KnockbackRegistry(
                config.getKnockbackProfiles(),
                config.getDefaultKnockbackProfile().getName()
        );
        this.combatEngine = new CombatEngine(this);
    }

    @Override
    public Platform getPlatform() {
        return platform;
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public KombatConfig getConfig() {
        return config;
    }

    @Override
    public Optional<KombatPlayer> getPlayer(UUID uuid) {
        return playerRegistry.find(uuid);
    }

    @Override
    public Optional<KombatPlayer> getPlayer(String name) {
        return playerRegistry.find(name);
    }

    @Override
    public Collection<KombatPlayer> getPlayers() {
        return playerRegistry.players();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void reload() {
        config.reload();
        knockbackRegistry.replaceAll(
                config.getKnockbackProfiles(),
                config.getDefaultKnockbackProfile().getName()
        );
    }

    public KombatPlayer registerPlayer(UUID uniqueId, String name) {
        return playerRegistry.register(uniqueId, name);
    }

    public void markPlayerOffline(UUID uniqueId) {
        playerRegistry.markOffline(uniqueId);
    }

    public KnockbackRegistry getKnockbackRegistry() {
        return knockbackRegistry;
    }

    public CombatEngine getCombatEngine() {
        return combatEngine;
    }

    /**
     * Publishes this runtime through the static API provider.
     */
    public void publish() {
        KombatProvider.set(this);
    }

    @Override
    public void close() {
        enabled = false;
        combatEngine.clear();
        eventBus.clear();
        playerRegistry.clear();
        if (KombatProvider.isLoaded() && KombatProvider.get() == this) {
            KombatProvider.unset();
        }
    }
}
