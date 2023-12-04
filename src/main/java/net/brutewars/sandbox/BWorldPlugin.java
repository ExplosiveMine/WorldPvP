package net.brutewars.sandbox;

import lombok.Getter;
import net.brutewars.sandbox.bworld.world.SandboxWorldManager;
import net.brutewars.sandbox.bworld.world.WorldRoster;
import net.brutewars.sandbox.commands.CommandHandler;
import net.brutewars.sandbox.commands.world.WorldCommandHandler;
import net.brutewars.sandbox.config.ConfigSettings;
import net.brutewars.sandbox.database.DataManager;
import net.brutewars.sandbox.dependencies.VaultDependency;
import net.brutewars.sandbox.dependencies.WorldEditDependency;
import net.brutewars.sandbox.listeners.*;
import net.brutewars.sandbox.menu.MenuManager;
import net.brutewars.sandbox.player.BPlayerManager;
import net.brutewars.sandbox.bworld.BWorldManager;
import net.brutewars.sandbox.thread.ClearLag;
import net.brutewars.sandbox.holograms.HologramManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class BWorldPlugin extends JavaPlugin {
    @Getter
    private final ConfigSettings configSettings = new ConfigSettings(this);

    @Getter
    private VaultDependency vault;
    @Getter
    private WorldEditDependency worldEdit;

    @Getter
    private final BPlayerManager bPlayerManager = new BPlayerManager(this);
    @Getter
    private final BWorldManager bWorldManager = new BWorldManager(this);

    @Getter
    private final SandboxWorldManager sandboxManager = new SandboxWorldManager(this);
    @Getter
    private final WorldRoster worldRoster = new WorldRoster(this);
    @Getter
    private final HologramManager hologramManager = new HologramManager(this);
    @Getter
    private final DataManager dataManager = new DataManager(this);
    @Getter
    private final MenuManager menuManager = new MenuManager(this);

    @Getter
    private final CommandHandler[] commands = new CommandHandler[]{
            new WorldCommandHandler(this)
    };

    private final EventListener[] listeners = {
            new PlayerEvents(this),
            new PortalEvents(this),
            new BlockEvents(this),
            new EntityEvents(this),
            new WorldEvents(this)
    };

    //todo new version
    // make it so that world generation sandboxworlds object type are different from pre-generated sandboxworld objects as an improvement <- not important
    // add per world inventories

    // todo test
    // bonus chest
    // worldsize implentation has been changed
    // all portals teleport correctly (which it will not for end portals)

    @Override
    public void onEnable() {
        configSettings.reload();

        // hooks must be loaded first
        vault = new VaultDependency(this);
        worldEdit = new WorldEditDependency(this);

        dataManager.load();

        worldRoster.setupRoster();
        menuManager.loadMenus();

        for (EventListener eventListener : listeners)
            getServer().getPluginManager().registerEvents(eventListener, this);

        new ClearLag(this).startThread();
        hologramManager.startThread();
    }

    @Override
    public void onDisable() {
        bWorldManager.updateLastLocations();
        hologramManager.removeDynamicHolograms();
        dataManager.save();
        sandboxManager.eraseDeletedWorlds();
    }

}