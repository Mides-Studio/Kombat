package git.immutabled.kombat.core.combat;

import git.immutabled.kombat.api.events.defaults.PlayerDamageEvent;
import git.immutabled.kombat.api.platform.Platform;
import git.immutabled.kombat.api.platform.PlatformLoaded;
import git.immutabled.kombat.api.player.KombatPlayer;
import git.immutabled.kombat.core.KombatAPIImpl;
import git.immutabled.kombat.core.configuration.KombatConfigImpl;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatEngineTest {

    @Test
    void processesDamageEventsCombosCooldownAndStatistics() {
        var config = new KombatConfigImpl(
                KombatConfigImpl.Snapshot.builder()
                        .comboTimeout(1_000)
                        .value("combat.hit-delay-ms", 300L)
                        .value("combat.base-damage-multiplier", 2.0D)
                        .value("combat.critical-damage-multiplier", 1.5D)
                        .build(),
                () -> { },
                () -> { }
        );
        var api = new KombatAPIImpl(new Platform("test", PlatformLoaded.PAPER), config);
        MutableClock clock = new MutableClock(10_000);
        CombatEngine engine = new CombatEngine(api, clock);
        KombatPlayer attacker = api.registerPlayer(UUID.randomUUID(), "Attacker");
        KombatPlayer victim = api.registerPlayer(UUID.randomUUID(), "Victim");
        api.getEventBus().register(PlayerDamageEvent.class, event -> event.setDamage(event.getDamage() + 1));

        CombatEngine.HitResult first = engine.processHit(
                attacker,
                victim,
                4,
                PlayerDamageEvent.DamageCause.MELEE,
                true
        );

        assertTrue(first.accepted());
        assertEquals(13, first.event().getDamage());
        assertEquals(1, attacker.getComboCount());
        assertEquals(13, attacker.getStatistics().getDamageDealt());

        CombatEngine.HitResult cooldown = engine.processHit(
                attacker,
                victim,
                4,
                PlayerDamageEvent.DamageCause.MELEE,
                false
        );
        assertFalse(cooldown.accepted());
        assertEquals(CombatEngine.RejectReason.COOLDOWN, cooldown.rejectReason());

        clock.advance(400);
        CombatEngine.HitResult second = engine.processHit(
                attacker,
                victim,
                4,
                PlayerDamageEvent.DamageCause.MELEE,
                false
        );
        assertTrue(second.accepted());
        assertEquals(2, attacker.getComboCount());
        assertEquals(2, attacker.getStatistics().getBestCombo());
    }

    @Test
    void recordsKillsAndDeaths() {
        var api = new KombatAPIImpl(
                new Platform("test", PlatformLoaded.MINESTOM),
                new KombatConfigImpl()
        );
        CombatEngine engine = api.getCombatEngine();
        KombatPlayer killer = api.registerPlayer(UUID.randomUUID(), "Killer");
        KombatPlayer victim = api.registerPlayer(UUID.randomUUID(), "Victim");

        engine.recordKill(killer, victim);

        assertEquals(1, killer.getStatistics().getKills());
        assertEquals(1, victim.getStatistics().getDeaths());
        assertEquals(1, killer.getStatistics().getKillstreak());
    }

    private static final class MutableClock extends Clock {
        private long epochMillis;

        private MutableClock(long epochMillis) {
            this.epochMillis = epochMillis;
        }

        void advance(long millis) {
            epochMillis += millis;
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return Instant.ofEpochMilli(epochMillis);
        }
    }
}
