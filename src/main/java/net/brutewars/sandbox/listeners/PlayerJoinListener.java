package net.brutewars.sandbox.listeners;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.thread.Executor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final BWorldPlugin plugin;

    public PlayerJoinListener(final BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Executor.sync(plugin, unused -> plugin.getBWorldManager().getSpawn().teleportToWorld(plugin.getBPlayerManager().getBPlayer(event.getPlayer())));
    }

}