package git.immutabled.kombat.api.knockback;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KnockbackProfileTest {

    @Test
    void buildsNormalizedImmutableProfile() {
        KnockbackProfile profile = KnockbackProfile.builder()
                .name("  Boxing Classic ")
                .horizontal(0.42)
                .vertical(0.34)
                .build();

        assertEquals("boxing-classic", profile.getName());
        assertEquals(0.42, profile.getHorizontalKnockback());
        assertEquals(0.34, profile.getVerticalKnockback());
    }

    @Test
    void rejectsInvalidPhysicsValues() {
        assertThrows(IllegalArgumentException.class, () -> KnockbackProfile.builder()
                .name("invalid")
                .friction(1.5)
                .build());
        assertThrows(IllegalArgumentException.class, () -> KnockbackProfile.builder()
                .name("invalid")
                .maxDistance(0)
                .build());
    }
}
