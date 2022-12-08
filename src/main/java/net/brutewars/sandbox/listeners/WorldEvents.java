package net.brutewars.sandbox.listeners;

import net.brutewars.sandbox.BWorldPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public final class WorldEvents extends EventListener {
    public WorldEvents(BWorldPlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onChunkLoad(ChunkLoadEvent event) {
        plugin.getHologramManager().onChunkLoad(event.getChunk());
    }

    @EventHandler(ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        plugin.getHologramManager().onChunkUnload(event.getChunk());
    }

    @EventHandler(ignoreCancelled = true)
    public void onWorldUnload(WorldUnloadEvent event) {
        plugin.getHologramManager().onWorldUnload(event.getWorld());
    }

}