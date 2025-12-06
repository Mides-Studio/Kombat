package git.immutabled.kombat.folia;

import git.immutabled.kombat.api.KombatAPI;
import git.immutabled.kombat.api.platform.Platform;
import git.immutabled.kombat.api.platform.PlatformLoaded;
import git.immutabled.kombat.core.KombatAPIImpl;
import git.immutabled.kombat.folia.commands.FoliaKnockbackCommand;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@Getter @Setter
public class FoliaKombatPlugin extends JavaPlugin {

    @Getter static FoliaKombatPlugin INSTANCE;
    private KombatAPI kombatAPI;

    @Override
    public void onEnable() {
        FoliaKombatPlugin.INSTANCE = this;

        this.kombatAPI = new KombatAPIImpl(
                new Platform(
                        this.getServer().getVersion(),
                        PlatformLoaded.FOLIA
                )
        );

        Objects.requireNonNull(this.getCommand("kombat"), "Kombat command not recognized").setExecutor(new FoliaKnockbackCommand());
        Objects.requireNonNull(this.getCommand("knockback"), "Knockback command not recognized").setExecutor(new FoliaKnockbackCommand());

    }
}
