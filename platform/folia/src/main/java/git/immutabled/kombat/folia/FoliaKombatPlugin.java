package git.immutabled.kombat.folia;

import git.immutabled.kombat.api.KombatAPI;
import git.immutabled.kombat.api.loader.Platform;
import git.immutabled.kombat.api.loader.PlatformLoaded;
import git.immutabled.kombat.core.KombatAPIImpl;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter @Setter
public class FoliaKombatPlugin extends JavaPlugin {

    @Getter static FoliaKombatPlugin INSTANCE;
    private KombatAPI kombatAPI;

    @Override
    public void onEnable() {
        INSTANCE = this;

        this.kombatAPI = new KombatAPIImpl(
                new Platform(
                        this.getServer().getVersion(),
                        PlatformLoaded.FOLIA
                )
        );

        //TODO: Register all listeners
    }
}
