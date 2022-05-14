package net.brutewars.sandbox;

import lombok.Getter;
import net.brutewars.sandbox.commands.CommandHandler;
import net.brutewars.sandbox.commands.world.WorldCommandHandler;
import net.brutewars.sandbox.config.ConfigSettings;
import net.brutewars.sandbox.database.DataManager;
import net.brutewars.sandbox.hooks.Vault;
import net.brutewars.sandbox.listeners.PlayerJoinListener;
import net.brutewars.sandbox.listeners.PlayerQuitListener;
import net.brutewars.sandbox.listeners.PlayerTeleportListener;
import net.brutewars.sandbox.menu.MenuManager;
import net.brutewars.sandbox.player.BPlayerManager;
import net.brutewars.sandbox.bworld.BWorldManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class BWorldPlugin extends JavaPlugin {
    @Getter private ConfigSettings configSettings;

    @Getter private Vault vault;

    @Getter private BPlayerManager bPlayerManager;
    @Getter private BWorldManager bWorldManager;
    @Getter private DataManager dataManager;

    @Getter private CommandHandler[] commands;

    @Getter private MenuManager menuManager;

    @Override
    public void onEnable() {
        configSettings = new ConfigSettings(this);

        vault = new Vault(this);

        bPlayerManager = new BPlayerManager(this);
        bWorldManager = new BWorldManager(this);
        dataManager = new DataManager(this);

        commands = new CommandHandler[] {
                new WorldCommandHandler(this)};

        menuManager = new MenuManager(this);

        final Listener[] listeners = new Listener[] {
                new PlayerQuitListener(this),
                new PlayerTeleportListener(this),
                new PlayerJoinListener(this)};
        Arrays.stream(listeners).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
    }

    @Override
    public void onDisable() {
        bWorldManager.updateLastLocations();

        dataManager.save();
    }

}