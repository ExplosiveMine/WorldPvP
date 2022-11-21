package net.brutewars.sandbox.bworld;

import net.brutewars.sandbox.bworld.lastlocation.BLocation;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.world.LoadingPhase;
import net.brutewars.sandbox.world.WorldSize;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface IBWorld {
    UUID getUuid();

    BPlayer getOwner();

    WorldSize getWorldSize();

    LoadingPhase getLoadingPhase();

    int getResetting();

    int getUnloading();

    Set<BPlayer> getOnlineBPlayers();

    Set<BPlayer> getPlayers(boolean includeOwner);

    void addPlayer(BPlayer bPlayer, BLocation bLocation);

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

    void teleportToWorld(BPlayer bPlayer, Consumer<Boolean> biConsumer);

    void updateLastLocation(BPlayer bPlayer, Location location);

    BLocation getLastLocation(BPlayer bPlayer);

    BLocation getDefaultLocation();

    void setDefaultLocation(BLocation defaultLocation);

    World getWorld();

}