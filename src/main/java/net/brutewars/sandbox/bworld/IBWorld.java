package net.brutewars.sandbox.bworld;

import net.brutewars.sandbox.bworld.lastlocation.LastLocation;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.world.LoadingPhase;
import net.brutewars.sandbox.world.WorldSize;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Set;
import java.util.UUID;

public interface IBWorld {
    UUID getUuid();

    BPlayer getOwner();

    WorldSize getWorldSize();

    LoadingPhase getLoadingPhase();

    int getResetting();

    int getUnloading();

    Set<BPlayer> getOnlineBPlayers();

    Set<BPlayer> getPlayers(boolean includeOwner);

    void addPlayer(BPlayer bPlayer, LastLocation lastLocation);

    void removePlayer(BPlayer bPlayer);

    boolean isInvited(BPlayer invitee);

    void invite(BPlayer inviter, BPlayer invitee);

    BPlayer getInviter(BPlayer invitee);

    void removeInvite(BPlayer invitee);

    void initialiseReset();

    void updateWorldSize();

    void initialiseUnloading();

    void cancelUnloading();

    String getWorldName();

    String getAlias();

    void teleportToWorld(BPlayer bPlayer);

    World getWorld();

    void updateLastLocation(BPlayer bPlayer, Location location);

    LastLocation getLastLocation(final BPlayer bPlayer);

    LastLocation getDefaultLocation();

    void setDefaultLocation(final LastLocation defaultLocation);
}
