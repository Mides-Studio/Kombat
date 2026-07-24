package git.immutabled.kombat.minestorm;

import git.immutabled.kombat.api.player.KombatPlayer;
import git.immutabled.kombat.core.KombatAPIImpl;
import git.immutabled.kombat.core.configuration.KombatConfigImpl;
import git.immutabled.kombat.api.platform.Platform;
import git.immutabled.kombat.api.platform.PlatformLoaded;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class MinestomPlayerStoreTest {

    @TempDir
    Path dataDirectory;

    @Test
    void persistsPreferencesProfilesAndStatistics() {
        UUID uniqueId = UUID.randomUUID();
        KombatAPIImpl firstApi = api();
        KombatPlayer original = firstApi.registerPlayer(uniqueId, "Fighter");
        original.setKombatEnabled(false);
        original.setKnockbackProfile(firstApi.getKnockbackRegistry().defaultProfile());
        original.getStatistics().setKills(7);
        original.getStatistics().setDeaths(2);
        original.getStatistics().setDamageDealt(42.5);
        original.getStatistics().setBestCombo(6);

        MinestomPlayerStore firstStore = new MinestomPlayerStore(dataDirectory);
        firstStore.close(List.of(original));
        firstApi.close();

        KombatAPIImpl secondApi = api();
        KombatPlayer restored = secondApi.registerPlayer(uniqueId, "Fighter");
        MinestomPlayerStore secondStore = new MinestomPlayerStore(dataDirectory);
        secondStore.load(restored, secondApi);

        assertFalse(restored.isKombatEnabled());
        assertEquals("classic", restored.getKnockbackProfile().getName());
        assertEquals(7, restored.getStatistics().getKills());
        assertEquals(2, restored.getStatistics().getDeaths());
        assertEquals(42.5, restored.getStatistics().getDamageDealt());
        assertEquals(6, restored.getStatistics().getBestCombo());

        secondStore.close(List.of(restored));
        secondApi.close();
    }

    private static KombatAPIImpl api() {
        return new KombatAPIImpl(
                new Platform("test", PlatformLoaded.MINESTOM),
                new KombatConfigImpl()
        );
    }
}
