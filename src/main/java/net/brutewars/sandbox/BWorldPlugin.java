package net.brutewars.sandbox;

import lombok.Getter;
import net.brutewars.sandbox.commands.CommandHandler;
import net.brutewars.sandbox.commands.world.WorldCommandHandler;
import net.brutewars.sandbox.config.ConfigSettings;
import net.brutewars.sandbox.database.DataManager;
import net.brutewars.sandbox.hooks.Vault;
import net.brutewars.sandbox.listeners.PlayerListeners;
import net.brutewars.sandbox.menu.MenuManager;
import net.brutewars.sandbox.player.BPlayerManager;
import net.brutewars.sandbox.bworld.BWorldManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class BWorldPlugin extends JavaPlugin {
    @Getter private final ConfigSettings configSettings = new ConfigSettings(this);

    @Getter private Vault vault;

    @Getter private final BPlayerManager bPlayerManager = new BPlayerManager(this);
    @Getter private final BWorldManager bWorldManager = new BWorldManager(this);
    @Getter private final DataManager dataManager = new DataManager(this);

    @Getter private final CommandHandler[] commands = new CommandHandler[] { new WorldCommandHandler(this) };

    @Getter private final MenuManager menuManager = new MenuManager(this);

    @Override
    public void onEnable() {
        // must be instantiated on enable
        vault = new Vault(this);

        dataManager.load();

        Listener[] listeners = new Listener[] {
                new PlayerListeners(this)};
        Arrays.stream(listeners).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
    }

    @Override
    public void onDisable() {
        bWorldManager.updateLastLocations();

        dataManager.save();
    }

}