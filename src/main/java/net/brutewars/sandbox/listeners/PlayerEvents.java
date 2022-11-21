package net.brutewars.sandbox.listeners;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.BWorld;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.thread.Executor;
import net.brutewars.sandbox.world.WorldFactory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class PlayerEvents extends EventListener {

    public PlayerEvents(BWorldPlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Executor.sync(plugin, unused -> plugin.getBWorldManager().getSpawn().teleportToWorld(plugin.getBPlayerManager().get(event.getPlayer())));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        BPlayer bPlayer = plugin.getBPlayerManager().get(player);

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
    public void onPlayerMove(PlayerMoveEvent event) {
        // add potion effect
        if (plugin.getBWorldManager().getWorldManager().getWorldFactory().isSpeedBoost(event.getTo().clone().subtract(0, 1, 0).getBlock()))
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 7 * 20, 2));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getFrom().getWorld().equals(event.getTo().getWorld()))
            return;

        BPlayer bPlayer = plugin.getBPlayerManager().get(event.getPlayer());
        BWorld bWorld = plugin.getBWorldManager().getBWorld(event.getFrom().getWorld().getName());

        if (!bPlayer.isInBWorld(bWorld, true))
            return;

        bWorld.updateLastLocation(bPlayer, event.getFrom());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        WorldFactory worldFactory = plugin.getBWorldManager().getWorldManager().getWorldFactory();
        Block block = event.getBlock();
        if (worldFactory.isSpeedBoost(block))
            worldFactory.removeSpeedBoost(block.getWorld());

        if (worldFactory.isBeacon(block))
            plugin.getHologramManager().removeHolograms(block.getWorld());
    }

}