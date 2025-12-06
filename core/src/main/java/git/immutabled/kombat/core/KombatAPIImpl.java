package git.immutabled.kombat.core;

import git.immutabled.kombat.api.KombatAPI;
import git.immutabled.kombat.api.configuration.KombatConfig;
import git.immutabled.kombat.api.events.EventBus;
import git.immutabled.kombat.api.loader.Platform;
import git.immutabled.kombat.api.loader.PlatformLoaded;
import git.immutabled.kombat.api.player.KombatPlayer;
import git.immutabled.kombat.core.configuration.KombatConfigImpl;
import git.immutabled.kombat.core.events.EventBusImpl;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Getter @Setter
@RequiredArgsConstructor
public class KombatAPIImpl implements KombatAPI {

    private final Platform platform;

    private boolean enabled;
    private KombatConfig config = new KombatConfigImpl();
    private EventBus eventBus = new EventBusImpl();

    private Set<KombatPlayer> players = Set.of();
    private HashMap<UUID, Optional<KombatPlayer>> playersById = new HashMap<>();
    private HashMap<String, Optional<KombatPlayer>> playersByName = new HashMap<>();


    @Override
    public Optional<KombatPlayer> getPlayer(UUID uuid) {
        return this.playersById.get(uuid);
    }

    @Override
    public Optional<KombatPlayer> getPlayer(String name) {
        return this.playersByName.get(name);
    }


    @Override
    public void reload() {

        // reload all configurations

    }
}
