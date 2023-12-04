package net.brutewars.sandbox.listeners;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.BWorld;
import net.brutewars.sandbox.bworld.BWorldManager;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.bworld.world.dimensions.EndDimension;
import net.brutewars.sandbox.bworld.world.dimensions.OverworldDimension;
import net.brutewars.sandbox.holograms.HologramManager;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public final class BlockEvents extends EventListener {
    private final BWorldManager bWorldManager;
    public BlockEvents(BWorldPlugin plugin) {
        super(plugin);
        this.bWorldManager = plugin.getBWorldManager();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Block block = event.getBlock();
        BPlayer bPlayer = plugin.getBPlayerManager().get(event.getPlayer());
        BWorld bWorld = bWorldManager.getBWorld(block.getWorld());
        if (bWorld == null)
            return;

        if (!bWorld.canPlayerBuild(bPlayer) && !event.getPlayer().isOp()) {
            Lang.CANNOT_BUILD.send(bPlayer);
            event.setCancelled(true);
            return;
        }

        World world = block.getWorld();
        HologramManager holoManager = plugin.getHologramManager();
        OverworldDimension overWorldDimension = (OverworldDimension) bWorld.getSandboxWorld(World.Environment.NORMAL);
        if (overWorldDimension.isSpecialBlock(block, Material.BEACON, "beacon")) {
            holoManager.removeIncrementalHolograms(world);
            holoManager.removeHologram(world, "welcome");
        }

        if (overWorldDimension.isSpecialBlock(block, Material.CHEST, "bonus"))
            holoManager.removeHologram(world, "bonus");
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        BPlayer bPlayer = plugin.getBPlayerManager().get(player);
        BWorld bWorld = bWorldManager.getBWorld(block.getWorld());
        if (bWorld == null || player.isOp() || bWorld.canPlayerBuild(bPlayer))
            return;

        Lang.CANNOT_BUILD.send(bPlayer);
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        World world = event.getBlock().getWorld();
        BWorld bWorld = bWorldManager.getBWorld(world);
        if (bWorld == null)
            return;

        if (world.getEnvironment() != World.Environment.NORMAL)
            return;

        ((OverworldDimension) bWorld.getSandboxWorld(World.Environment.NORMAL)).onBlockPhysics(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        World world = event.getBlock().getWorld();
        BWorld bWorld = bWorldManager.getBWorld(world);
        if (bWorld == null)
            return;

        if (world.getEnvironment() != World.Environment.THE_END)
            return;

        ((EndDimension) bWorld.getSandboxWorld(World.Environment.THE_END)).onBlockFromTo(event);
    }
}