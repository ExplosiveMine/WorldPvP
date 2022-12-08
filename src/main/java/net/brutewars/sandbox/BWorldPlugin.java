package net.brutewars.sandbox;

import lombok.Getter;
import net.brutewars.sandbox.commands.CommandHandler;
import net.brutewars.sandbox.commands.world.WorldCommandHandler;
import net.brutewars.sandbox.config.ConfigSettings;
import net.brutewars.sandbox.database.DataManager;
import net.brutewars.sandbox.dependencies.VaultDependency;
import net.brutewars.sandbox.listeners.*;
import net.brutewars.sandbox.menu.HUDManager;
import net.brutewars.sandbox.menu.MenuManager;
import net.brutewars.sandbox.player.BPlayerManager;
import net.brutewars.sandbox.bworld.BWorldManager;
import net.brutewars.sandbox.thread.ClearLag;
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
    @Getter private final HUDManager hudManager = new HUDManager(this);

    @Getter private final CommandHandler[] commands = new CommandHandler[] {
            new WorldCommandHandler(this)
    };

    private final EventListener[] listeners = {
            new PlayerEvents(this),
            new PortalEvents(this),
            new BlockEvents(this),
            new EntityEvents(this),
            new WorldEvents(this)
    };

    // todo new version
    // new structure with schematic
    // on creation of world; option to not spawn structures

    @Override
    public void onEnable() {
        configSettings.init();

        // hooks must be loaded first
        vault = new VaultDependency(this);

        dataManager.load();

        menuManager.loadMenus();

        for (EventListener eventListener : listeners)
            getServer().getPluginManager().registerEvents(eventListener, this);

        bWorldManager.getWorldManager().setupWorldRoster();

        new ClearLag(this).init();

        // start ticking dynamic holograms
        hologramManager.init();

        hudManager.setup();
    }

    @Override
    public void onDisable() {
        bWorldManager.updateLastLocations();
        hologramManager.removeDynamicHolograms();
        getServer().getOnlinePlayers().forEach(hudManager::onPlayerQuit);
        dataManager.save();
        bWorldManager.getWorldManager().eraseDeletedWorlds();
    }

}