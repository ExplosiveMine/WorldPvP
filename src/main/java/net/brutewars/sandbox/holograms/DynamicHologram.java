package net.brutewars.sandbox.holograms;

import lombok.Getter;
import net.brutewars.sandbox.BWorldPlugin;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

public abstract class DynamicHologram extends Hologram {
    private boolean spawned = false;
    @Getter private final long chunkKey;

    public DynamicHologram(Location loc, String text) {
        super(loc, text);
        //copied from Chunk#getChunkKey()
        this.chunkKey = (long) ((int) Math.floor(loc.getX()) >> 4) & 0xffffffffL |
                ((long) ((int) Math.floor(loc.getZ()) >> 4) & 0xffffffffL) << 32;
    }

    @Override
    public ArmorStand spawn(BWorldPlugin plugin) {
        if (spawned)
            return (ArmorStand) getEntity();

        spawned = true;
        return super.spawn(plugin);
    }

    /**
     * @return true if the entity has been either de-spawned by this method or
     * had been previously de-spawned
     */
    @Override
    public boolean deSpawn() {
        if (!spawned)
            return true;

        boolean successful = super.deSpawn();
        if (successful)
            spawned = false;

        return successful;
    }

    public void update() {
        if (!spawned)
            return;

        tick();
    }

    public abstract void tick();

}