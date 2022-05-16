package net.brutewars.sandbox.bworld;

import net.brutewars.sandbox.bworld.lastlocation.LastLocation;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.world.LoadingPhase;
import net.brutewars.sandbox.world.WorldSize;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

    void updateLastLocation(BPlayer bPlayer, Location location);

    LastLocation getLastLocation(BPlayer bPlayer);

    LastLocation getDefaultLocation();

    void setDefaultLocation(LastLocation defaultLocation);

    CompletableFuture<World> getWorld();

}