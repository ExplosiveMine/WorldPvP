package net.brutewars.sandbox.bworld.world.location;

import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.Location;

public interface LastLocationTracker {
    void updateLastLocation(BPlayer bPlayer, BLocation bLocation);
    void updateLastLocation(BPlayer bPlayer, Location Location);

    BLocation getLastLocation(BPlayer bPlayer);

    BLocation getDefaultLocation();

    void startTracking(BPlayer bPlayer);

    void stopTracking(BPlayer bPlayer);
}
