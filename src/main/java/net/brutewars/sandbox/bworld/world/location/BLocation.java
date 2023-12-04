package net.brutewars.sandbox.bworld.world.location;

import org.bukkit.Location;
import org.bukkit.World;

public final class BLocation {
    private final double x, y, z;
    private final float yaw, pitch;

    public BLocation(Location location) {
        x = location.getX();
        y = location.getY();
        z = location.getZ();
        yaw = location.getYaw();
        pitch = location.getPitch();
    }

    public BLocation(String string) {
        String[] location = string.split(":");
        x = Double.parseDouble(location[0]);
        y = Double.parseDouble(location[1]);
        z = Double.parseDouble(location[2]);
        if (location.length == 5) {
            yaw = Float.parseFloat(location[3]);
            pitch = Float.parseFloat(location[4]);
        } else {
            yaw = 0;
            pitch = 0;
        }

    }

    public Location toLoc(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public String toString() {
        return x + ":" + y + ":" + z + ":" + yaw + ":" + pitch;
    }
}