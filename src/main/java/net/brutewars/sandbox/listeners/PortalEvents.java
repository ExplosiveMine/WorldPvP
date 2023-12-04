package net.brutewars.sandbox.listeners;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.BWorld;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;

public final class PortalEvents extends EventListener {
    public PortalEvents(BWorldPlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPortal(PlayerPortalEvent event) {
        BWorld bWorld = plugin.getBWorldManager().getBWorld(event.getFrom().getWorld());
        if (bWorld == null)
            return;

        World.Environment toEnv = event.getTo().getWorld().getEnvironment();
        if (toEnv == World.Environment.NETHER) {
            event.getTo().setWorld(bWorld.getWorld(World.Environment.NETHER));
            return;
        }

        World.Environment fromEnv = event.getFrom().getWorld().getEnvironment();
        if (fromEnv == World.Environment.NETHER
                && (toEnv == World.Environment.NORMAL || toEnv == World.Environment.THE_END)) {
            bWorld.teleportToWorld(plugin.getBPlayerManager().get(event.getPlayer()), toEnv);
            event.setCancelled(true);
        }

        if (event.getCause() == PlayerPortalEvent.TeleportCause.END_PORTAL
                && fromEnv == World.Environment.NORMAL
                && toEnv == World.Environment.THE_END)
            event.getTo().setWorld(bWorld.getWorld(toEnv));
    }



    @EventHandler(ignoreCancelled = true)
    public void onEntityPortal(EntityPortalEvent event) {
        //todo might cause bugs
        BWorld bWorld = plugin.getBWorldManager().getBWorld(event.getFrom().getWorld());
        if (bWorld == null)
            return;

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityPortalEnter(EntityPortalEnterEvent event) {
        World world = event.getLocation().getWorld();
        BWorld bWorld = plugin.getBWorldManager().getBWorld(world);

        if (bWorld == null)
            return;

        Entity ent = event.getEntity();
        if (!(ent instanceof Player))
            return;

        Material mat = event.getLocation().getBlock().getType();
        // for nether portals we need to wait until it's ready to teleport
        if (mat == Material.NETHER_PORTAL)
            return;

        Location loc;
        World.Environment env = World.Environment.NORMAL;
        if (world.getEnvironment() == env || world.getEnvironment() == World.Environment.NETHER) {
            if (mat == Material.END_PORTAL)
                return;

            loc = bWorld.getWorld(env).getSpawnLocation();
        } else {
            loc = bWorld.getLastLocationTracker().getDefaultLocation().toLoc(bWorld.getWorld());
        }

        ent.teleportAsync(loc);
    }



}