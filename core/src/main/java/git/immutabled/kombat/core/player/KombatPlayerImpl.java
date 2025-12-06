package git.immutabled.kombat.core.player;

import git.immutabled.kombat.api.knockback.KnockbackProfile;
import git.immutabled.kombat.api.player.KombatPlayer;
import git.immutabled.kombat.api.player.statistics.PlayerStatistics;
import git.immutabled.kombat.core.player.statistics.PlayerStatisticsImpl;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@RequiredArgsConstructor
@Getter @Setter
//TODO: add javadocs for class and fields
public class KombatPlayerImpl implements KombatPlayer {
    private final UUID uniqueId;
    private final String name;
    private boolean online;
    private PlayerStatistics statistics = new PlayerStatisticsImpl();
    private @Nullable KnockbackProfile knockbackProfile = null;
    private int comboCount = 0;
    private boolean kombatEnabled = true;
}
