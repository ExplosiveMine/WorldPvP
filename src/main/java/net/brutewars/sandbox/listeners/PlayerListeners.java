package net.brutewars.sandbox.listeners;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.BWorld;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.thread.Executor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public final class PlayerListeners implements Listener {
    private final BWorldPlugin plugin;

    public PlayerListeners(BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Executor.sync(plugin, unused -> plugin.getBWorldManager().getSpawn().teleportToWorld(plugin.getBPlayerManager().getBPlayer(event.getPlayer())));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        BPlayer bPlayer = plugin.getBPlayerManager().getBPlayer(player);

        if (bPlayer == null)
            return;

        BWorld bWorld = bPlayer.getBWorld();
        if (bPlayer.getBWorld() == null)
            return;

        Executor.sync(plugin, unused -> {
            if (bWorld.getOnlineBPlayers().size() == 0)
                bWorld.initialiseUnloading();
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getFrom().getWorld().equals(event.getTo().getWorld()))
            return;

        BPlayer bPlayer = plugin.getBPlayerManager().getBPlayer(event.getPlayer());
        BWorld bWorld = plugin.getBWorldManager().getBWorld(event.getFrom().getWorld().getName());

        if (!bPlayer.isInBWorld(bWorld, true))
            return;

        bWorld.updateLastLocation(bPlayer, event.getFrom());
    }

}