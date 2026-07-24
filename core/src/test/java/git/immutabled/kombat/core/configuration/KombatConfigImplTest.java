package git.immutabled.kombat.core.configuration;

import git.immutabled.kombat.api.knockback.KnockbackProfile;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KombatConfigImplTest {

    @Test
    void exposesTypedCustomValues() {
        KnockbackProfile boxing = KnockbackProfile.builder().name("boxing").build();
        var snapshot = KombatConfigImpl.Snapshot.builder()
                .attackSpeed(32)
                .locale(Locale.forLanguageTag("es-CL"))
                .profiles(List.of(boxing))
                .defaultProfile("boxing")
                .value("combat.hit-delay-ms", 325)
                .build();
        KombatConfigImpl config = new KombatConfigImpl(snapshot, () -> { }, () -> { });

        assertEquals(32, config.getAttackSpeed());
        assertEquals("boxing", config.getDefaultKnockbackProfile().getName());
        assertEquals(325L, config.get("combat.hit-delay-ms", 0L));
    }

    @Test
    void rejectsUnknownDefaultProfile() {
        assertThrows(IllegalArgumentException.class, () ->
                KombatConfigImpl.Snapshot.builder()
                        .defaultProfile("missing")
                        .build()
        );
    }
}
