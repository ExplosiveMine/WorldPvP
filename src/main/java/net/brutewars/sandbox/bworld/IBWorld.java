package net.brutewars.sandbox.bworld;

import net.brutewars.sandbox.bworld.lastlocation.BLocation;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.world.LoadingPhase;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Set;
import java.util.UUID;

public interface IBWorld {
    UUID getUuid();

    BPlayer getOwner();

    LoadingPhase getWorldPhase(World.Environment env);

    int getResetting();

    int getUnloading();

    Set<BPlayer> getPlayers(boolean includeOwner);

    void addPlayer(BPlayer bPlayer, BLocation bLocation);

    void removePlayer(BPlayer bPlayer);

    boolean isInvited(BPlayer invitee);

    void invite(BPlayer inviter, BPlayer invitee);

    BPlayer getInviter(BPlayer invitee);

    void removeInvite(BPlayer invitee);

    String getWorldPath();

    String getAlias();

    void teleportToWorld(BPlayer bPlayer);

    BLocation getLastLocation(BPlayer bPlayer);

    BLocation getDefaultLocation();

    void setDefaultLocation(BLocation defaultLocation);

    World getWorld();

    GameMode getDefaultGameMode();

    void setDefaultGameMode(GameMode gamemode);

    /**
     * @return Set of BPlayers who are in the BWorld's loaded worlds
     */
    Set<BPlayer> getActivePlayers();

    default void updatePlayerGameModes() {
        for (BPlayer bPlayer : getActivePlayers())
            bPlayer.setGameMode(getDefaultGameMode());
    }

}