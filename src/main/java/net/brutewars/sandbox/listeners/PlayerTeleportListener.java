package net.brutewars.sandbox.listeners;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.BWorld;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleportListener implements Listener {
    private final BWorldPlugin plugin;

    public PlayerTeleportListener(final BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getFrom().getWorld().equals(event.getTo().getWorld()))
            return;

        final BPlayer bPlayer = plugin.getBPlayerManager().getBPlayer(event.getPlayer());
        final BWorld bWorld = plugin.getBWorldManager().getBWorld(event.getFrom().getWorld().getName());

        if (!bPlayer.isInBWorld(bWorld, true))
            return;

        bWorld.updateLastLocation(bPlayer, event.getFrom());
    }

}