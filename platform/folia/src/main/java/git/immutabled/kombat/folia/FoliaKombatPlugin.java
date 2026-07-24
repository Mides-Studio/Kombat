package git.immutabled.kombat.folia;

import git.immutabled.kombat.api.platform.PlatformLoaded;
import git.immutabled.kombat.paper.AbstractPaperKombatPlugin;

public final class FoliaKombatPlugin extends AbstractPaperKombatPlugin {

    @Override
    protected PlatformLoaded platformType() {
        return PlatformLoaded.FOLIA;
    }
}
