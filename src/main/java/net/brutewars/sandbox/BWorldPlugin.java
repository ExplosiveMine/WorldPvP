package net.brutewars.sandbox;

import lombok.Getter;
import net.brutewars.sandbox.commands.CommandHandler;
import net.brutewars.sandbox.commands.world.WorldCommandHandler;
import net.brutewars.sandbox.config.ConfigSettings;
import net.brutewars.sandbox.database.DataManager;
import net.brutewars.sandbox.dependencies.VaultDependency;
import net.brutewars.sandbox.listeners.EventListener;
import net.brutewars.sandbox.listeners.PlayerEvents;
import net.brutewars.sandbox.menu.MenuManager;
import net.brutewars.sandbox.player.BPlayerManager;
import net.brutewars.sandbox.bworld.BWorldManager;
import net.brutewars.sandbox.world.holograms.HologramManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class BWorldPlugin extends JavaPlugin {
    @Getter private final ConfigSettings configSettings = new ConfigSettings(this);

    @Getter private VaultDependency vault;

    @Getter private final BPlayerManager bPlayerManager = new BPlayerManager(this);
    @Getter private final BWorldManager bWorldManager = new BWorldManager(this);

    @Getter private final HologramManager hologramManager = new HologramManager(this);
    @Getter private final DataManager dataManager = new DataManager(this);
    @Getter private final MenuManager menuManager = new MenuManager(this);

    @Getter private final CommandHandler[] commands = new CommandHandler[] {
            new WorldCommandHandler(this)
    };

    private final EventListener[] listeners = {
            new PlayerEvents(this)
    };

    //todo
    // personal nether and end
    // beacon gives speed 3 until broken
    // do new structure
    // do portals
    // do portal blocks

    @Override
    public void onEnable() {
        configSettings.init();

        // hooks must be loaded here
        vault = new VaultDependency(this);

        dataManager.load();

        for (EventListener eventListener : listeners)
            getServer().getPluginManager().registerEvents(eventListener, this);

        bWorldManager.getWorldManager().setupWorldRoster();
    }

    @Override
    public void onDisable() {
        bWorldManager.updateLastLocations();
        dataManager.save();
        bWorldManager.getWorldManager().eraseDeletedWorlds();
    }

}