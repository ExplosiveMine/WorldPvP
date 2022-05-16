package net.brutewars.sandbox.bworld.lastlocation;

import org.bukkit.Location;
import org.bukkit.World;

public final class LastLocation {
    private final double x, y, z;
    private final float yaw, pitch;

    public LastLocation(Location location) {
        x = location.getX();
        y = location.getY();
        z = location.getZ();
        yaw = location.getYaw();
        pitch = location.getPitch();
    }

    public LastLocation(String string) {
        String[] location = string.split(":");
        x = Double.parseDouble(location[0]);
        y = Double.parseDouble(location[1]);
        z = Double.parseDouble(location[2]);
        yaw = Float.parseFloat(location[3]);
        pitch = Float.parseFloat(location[4]);
    }

    public Location toLoc(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public String toString() {
        return x + ":" + y + ":" + z + ":" + yaw + ":" + pitch;
    }
}