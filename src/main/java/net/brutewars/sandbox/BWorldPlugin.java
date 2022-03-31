package net.brutewars.sandbox;

import lombok.Getter;
import net.brutewars.sandbox.commands.chat.ChatCommandHandler;
import net.brutewars.sandbox.commands.CommandHandler;
import net.brutewars.sandbox.commands.world.WorldCommandHandler;
import net.brutewars.sandbox.config.Lang;
import net.brutewars.sandbox.database.DataManager;
import net.brutewars.sandbox.hooks.Vault;
import net.brutewars.sandbox.listeners.PlayerChatListener;
import net.brutewars.sandbox.listeners.PlayerQuitListener;
import net.brutewars.sandbox.player.BPlayerManager;
import net.brutewars.sandbox.bworld.BWorldManager;
import net.brutewars.sandbox.world.WorldSize;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class BWorldPlugin extends JavaPlugin {
    @Getter private static BWorldPlugin plugin;

    @Getter private DataManager dataManager;
    @Getter private BPlayerManager bPlayerManager;
    @Getter private BWorldManager bWorldManager;

    @Getter private CommandHandler[] commands;

    //HOOKS
    @Getter private Vault vault;

    @Override
    public void onEnable() {
        plugin = this;

        // Config
        Lang.reload(this);
        WorldSize.reload(this);
        saveDefaultConfig();

        //Hooks
        this.vault = new Vault(this);

        //Managers
        bPlayerManager = new BPlayerManager(this);
        bWorldManager = new BWorldManager(this);
        dataManager = new DataManager(this, bPlayerManager, bWorldManager);
        dataManager.init();

        commands = new CommandHandler[] {
                new WorldCommandHandler(this),
                new ChatCommandHandler(this)};

        Listener[] listeners = new Listener[] {
                new PlayerChatListener(this),
                new PlayerQuitListener(this)};
        Arrays.stream(listeners).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

    }

    @Override
    public void onDisable() {
        dataManager.save();
    }

}