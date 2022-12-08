package net.brutewars.sandbox.listeners;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.BWorld;
import net.brutewars.sandbox.bworld.IBWorld;
import net.brutewars.sandbox.bworld.SpawnBWorld;
import net.brutewars.sandbox.bworld.lastlocation.BLocation;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.thread.Executor;
import net.brutewars.sandbox.utils.PersistentDataUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.persistence.PersistentDataType;

public final class PlayerEvents extends EventListener {

    public PlayerEvents(BWorldPlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getHudManager().onPlayerJoin(event);

        Executor.sync(plugin, unused -> {
            // Teleport the player to the world spawn if they are in the spawn world
            SpawnBWorld spawn = plugin.getBWorldManager().getSpawn();
            if (spawn.getWorld().equals(event.getPlayer().getLocation().getWorld()))
                spawn.teleportToWorld(plugin.getBPlayerManager().get(event.getPlayer()));
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getHudManager().onPlayerQuit(event.getPlayer());

        Player player = event.getPlayer();
        BPlayer bPlayer = plugin.getBPlayerManager().get(player);

        if (bPlayer == null)
            return;

        BWorld bWorld = bPlayer.getBWorld();
        if (bPlayer.getBWorld() == null)
            return;

        Executor.sync(plugin, unused -> {
            Location lastLoc = event.getPlayer().getLocation();
            World world = lastLoc.getWorld();

            if (bWorld.equals(plugin.getBWorldManager().getBWorld(world))
                    && world.getEnvironment() == World.Environment.NORMAL) {
                bWorld.updateLastLocation(bPlayer, lastLoc);
            }

            if (bWorld.getOnlineBPlayers().size() == 0)
                bWorld.initialiseUnloading();
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        //todo
        // move this to EndDimension.java
        BWorld bWorld = plugin.getBWorldManager().getBWorld(event.getTo().getWorld());
        if (bWorld == null)
            return;

        Location to = event.getTo();
        World world = to.getWorld();
        String bLoc = PersistentDataUtils.getData(plugin, world, PersistentDataType.STRING, "water_portal0", "");
        if (bLoc.isEmpty())
            return;

        Location portalLoc = new BLocation(bLoc).toLoc(world);
        if (to.getBlockX() == portalLoc.getBlockX()
                && to.getBlockY() == portalLoc.getBlockY()
                && to.getBlockZ() == portalLoc.getBlockZ()) {
            bWorld.teleportToWorld(plugin.getBPlayerManager().get(event.getPlayer()));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        BPlayer bPlayer = plugin.getBPlayerManager().get(event.getPlayer());

        // Update player's gamemode
        IBWorld iBWorld = plugin.getBWorldManager().getIBWorld(event.getTo().getWorld());
        if (iBWorld != null)
            bPlayer.setGameMode(iBWorld.getDefaultGameMode());

        // Update last location
        BWorld bWorld = plugin.getBWorldManager().getBWorld(event.getFrom().getWorld());
        if (bPlayer.isInBWorld(bWorld, true))
            bWorld.onPlayerTeleport(bPlayer, event.getFrom(), event.getTo());
    }

    /**
     * when a player dies, we save their last location
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        plugin.getHudManager().onPlayerDeath(event);

        Location loc = event.getPlayer().getLocation();
        World world = loc.getWorld();
        BWorld bWorld = plugin.getBWorldManager().getBWorld(world);
        if (bWorld == null)
            return;

        if (world.getEnvironment() != World.Environment.NORMAL)
            return;

        BPlayer bPlayer = plugin.getBPlayerManager().get(event.getPlayer());
        if (!bPlayer.isInBWorld(bWorld, true))
            return;

        bWorld.updateLastLocation(bPlayer, loc);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        plugin.getHudManager().onPlayerRespawn(event);

        Location deathLoc = event.getPlayer().getLocation();
        BWorld bWorld = plugin.getBWorldManager().getBWorld(deathLoc.getWorld());
        if (bWorld == null)
            return;

        Location bedLocation = event.getPlayer().getBedSpawnLocation();
        if (bedLocation == null || !bedLocation.getWorld().equals(bWorld.getWorld()))
            event.setRespawnLocation(bWorld.getDefaultLocation().toLoc(bWorld.getWorld()));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        plugin.getHudManager().onPlayerInteract(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        plugin.getHudManager().onPlayerDrop(event);
    }

}