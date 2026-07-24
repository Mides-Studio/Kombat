package git.immutabled.kombat.core.player;

import git.immutabled.kombat.api.player.KombatPlayer;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe registry for the platform-neutral player model.
 */
public final class PlayerRegistry {

    private final ConcurrentMap<UUID, KombatPlayerImpl> byId = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, UUID> byName = new ConcurrentHashMap<>();

    public KombatPlayer register(UUID uniqueId, String name) {
        KombatPlayerImpl player = byId.compute(uniqueId, (id, current) -> {
            if (current == null) {
                current = new KombatPlayerImpl(id, name);
            }
            current.updateIdentity(name, true);
            return current;
        });
        byName.entrySet().removeIf(entry -> entry.getValue().equals(uniqueId));
        byName.put(normalize(name), uniqueId);
        return player;
    }

    public void markOffline(UUID uniqueId) {
        KombatPlayerImpl player = byId.get(uniqueId);
        if (player != null) {
            player.setOnline(false);
        }
    }

    public Optional<KombatPlayer> find(UUID uniqueId) {
        return Optional.ofNullable(byId.get(uniqueId));
    }

    public Optional<KombatPlayer> find(String name) {
        if (name == null) {
            return Optional.empty();
        }
        UUID uniqueId = byName.get(normalize(name));
        return uniqueId == null ? Optional.empty() : find(uniqueId);
    }

    public Collection<KombatPlayer> players() {
        return ListView.copyOf(byId.values());
    }

    public void clear() {
        byId.clear();
        byName.clear();
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private static final class ListView {
        private static Collection<KombatPlayer> copyOf(Collection<? extends KombatPlayer> players) {
            return java.util.List.copyOf(players);
        }
    }
}
