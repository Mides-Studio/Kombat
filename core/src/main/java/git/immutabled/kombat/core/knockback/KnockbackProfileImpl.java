package git.immutabled.kombat.core.knockback;

import git.immutabled.kombat.api.knockback.KnockbackProfile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter @Setter
public class KnockbackProfileImpl implements KnockbackProfile {

    private final String name;
    private double horizontalKnockback;
    private double verticalKnockback;
    private double friction;
    private double sprintMultiplier;
    private double maxDistance;
    private boolean airMovement;
    private boolean sprintKnockback;
}
