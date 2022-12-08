package net.brutewars.sandbox.world.dimensions;

import net.brutewars.sandbox.BWorldPlugin;
import org.bukkit.Location;

public final class NetherDimension extends Dimension {
    public NetherDimension(BWorldPlugin plugin) {
        super(plugin);
    }

    @Override
    public Location generate(Location spawnLoc) {
        return spawnLoc;
    }

}