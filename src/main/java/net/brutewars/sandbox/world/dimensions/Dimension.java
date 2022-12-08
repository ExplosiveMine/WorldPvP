package net.brutewars.sandbox.world.dimensions;

import net.brutewars.sandbox.BWorldPlugin;
import org.bukkit.Location;

public abstract class Dimension {
    protected final BWorldPlugin plugin;

    public Dimension(BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract Location generate(Location spawnLoc);

}