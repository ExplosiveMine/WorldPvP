package net.brutewars.sandbox.listeners;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.BWorld;
import net.brutewars.sandbox.bworld.world.dimensions.SpawnWorld;
import net.brutewars.sandbox.bworld.world.location.BLocation;
import net.brutewars.sandbox.bworld.settings.WorldSettingsContainer;
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
        Executor.sync(plugin, unused -> {
            // Teleport the player to the world spawn if they are in the spawn world
            SpawnWorld spawn = plugin.getBWorldManager().getSpawn();
            if (spawn.getWorld().equals(event.getPlayer().getLocation().getWorld()))
                spawn.teleportToWorld(plugin.getBPlayerManager().get(event.getPlayer()));
        });
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
            Location lastLoc = event.getPlayer().getLocation();
            World world = lastLoc.getWorld();

            if (bWorld.equals(plugin.getBWorldManager().getBWorld(world))
                    && world.getEnvironment() == World.Environment.NORMAL) {
                bWorld.getLastLocationTracker().updateLastLocation(bPlayer, lastLoc);
            }

            if (bWorld.getOnlineMembers().size() == 0)
                bWorld.initialiseUnloading();
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        //todo
        // maybe move this to end dimension? if it fits
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

        //todo probably need to move this to BWorld#onPlayerTeleport
        // also make it so when a player is teleported in the same world it doesnt affect gamemode
        WorldSettingsContainer settingsContainer = plugin.getBWorldManager().getSettingsContainer(event.getTo().getWorld());
        if (settingsContainer != null)
            bPlayer.setGameMode(settingsContainer.getSettings().getDefaultGameMode());

        BWorld bWorld = plugin.getBWorldManager().getBWorld(event.getFrom().getWorld());
        if (bPlayer.isInBWorld(bWorld, true))
            bWorld.onPlayerTeleport(bPlayer, event.getFrom(), event.getTo());
    }

    /**
     * when a player dies, we save their last location
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
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

        bWorld.getLastLocationTracker().updateLastLocation(bPlayer, loc);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Location deathLoc = event.getPlayer().getLocation();
        BWorld bWorld = plugin.getBWorldManager().getBWorld(deathLoc.getWorld());
        if (bWorld == null)
            return;

        Location bedLocation = event.getPlayer().getBedSpawnLocation();
        if (bedLocation == null || !bedLocation.getWorld().equals(bWorld.getWorld()))
            event.setRespawnLocation(bWorld.getLastLocationTracker().getDefaultLocation().toLoc(bWorld.getWorld()));
    }

}