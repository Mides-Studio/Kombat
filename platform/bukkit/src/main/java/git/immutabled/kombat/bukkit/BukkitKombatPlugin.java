package git.immutabled.kombat.bukkit;

import git.immutabled.kombat.api.platform.PlatformLoaded;
import git.immutabled.kombat.paper.AbstractPaperKombatPlugin;

public final class BukkitKombatPlugin extends AbstractPaperKombatPlugin {

    @Override
    protected PlatformLoaded platformType() {
        String serverName = getServer().getName().toLowerCase(java.util.Locale.ROOT);
        return serverName.contains("paper") ? PlatformLoaded.PAPER : PlatformLoaded.BUKKIT;
    }
}
