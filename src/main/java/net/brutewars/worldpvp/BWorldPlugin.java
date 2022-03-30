package net.brutewars.worldpvp;

import lombok.Getter;
import net.brutewars.worldpvp.commands.chat.ChatCommandHandler;
import net.brutewars.worldpvp.commands.CommandHandler;
import net.brutewars.worldpvp.commands.world.WorldCommandHandler;
import net.brutewars.worldpvp.config.Lang;
import net.brutewars.worldpvp.database.DataManager;
import net.brutewars.worldpvp.hooks.Vault;
import net.brutewars.worldpvp.listeners.PlayerChatListener;
import net.brutewars.worldpvp.listeners.PlayerQuitListener;
import net.brutewars.worldpvp.player.BPlayerManager;
import net.brutewars.worldpvp.world.BWorldManager;
import net.brutewars.worldpvp.world.WorldSize;
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