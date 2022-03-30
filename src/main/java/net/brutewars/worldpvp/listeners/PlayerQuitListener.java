package net.brutewars.worldpvp.listeners;

import net.brutewars.worldpvp.BWorldPlugin;
import net.brutewars.worldpvp.player.BPlayer;
import net.brutewars.worldpvp.thread.Executor;
import net.brutewars.worldpvp.world.BWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public final class PlayerQuitListener implements Listener {
    private final BWorldPlugin plugin;

    public PlayerQuitListener(final BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final BPlayer bPlayer = plugin.getBPlayerManager().getBPlayer(player);

        if (bPlayer == null)
            return;

        final BWorld bWorld = bPlayer.getBWorld();

        if (bPlayer.getBWorld() == null)
            return;

        Executor.sync(plugin, new BukkitRunnable() {
            @Override
            public void run() {
                if (bWorld.getOnlineBPlayers().size() == 0)
                    bWorld.initialiseUnloading();
            }
        });

    }

}