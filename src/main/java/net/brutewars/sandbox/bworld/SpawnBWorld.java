package net.brutewars.sandbox.bworld;

import lombok.Getter;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.lastlocation.BLocation;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.world.LoadingPhase;
import org.bukkit.GameMode;
import org.bukkit.World;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class SpawnBWorld implements IBWorld {
    private final BWorldPlugin plugin;

    @Getter private final UUID uuid;
    @Getter private final BPlayer owner;

    @Getter private final int resetting = -1;
    @Getter private final int unloading = -1;

    private GameMode defaultGameMode;
    private BLocation defaultLocation;

    public SpawnBWorld(BWorldPlugin plugin) {
        this.plugin = plugin;
        this.uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        this.owner = new BPlayer(plugin, uuid, null);
    }

    @Override
    public LoadingPhase getWorldPhase(World.Environment env) {
        return LoadingPhase.LOADED;
    }

    @Override
    public Set<BPlayer> getPlayers(boolean includeOwner) {
        return Collections.emptySet();
    }

    @Override
    public void addPlayer(BPlayer bPlayer, BLocation bLocation) {
        //noop
    }

    @Override
    public void removePlayer(BPlayer bPlayer) {
        //noop
    }

    @Override
    public boolean isInvited(BPlayer invitee) {
        return false;
    }

    @Override
    public void invite(BPlayer inviter, BPlayer invitee) {
        //noop
    }

    @Override
    public BPlayer getInviter(BPlayer invitee) {
        return null;
    }

    @Override
    public void removeInvite(BPlayer invitee) {
        //noop
    }

    @Override
    public String getWorldPath() {
        return plugin.getConfigSettings().getConfigParser().getWorldName();
    }

    @Override
    public String getAlias() {
        return uuid.toString();
    }

    /**
     * @implNote  teleportation is done synchronously so that players are
     * immediately teleported when a world is being unloaded. If done async,
     * the world would not be unloaded properly
     */
    @Override
    public void teleportToWorld(BPlayer bPlayer) {
        bPlayer.runIfOnline(player -> player.teleport(getDefaultLocation().toLoc(getWorld())));
    }

    @Override
    public BLocation getDefaultLocation() {
        if (defaultLocation == null) {
            String loc = plugin.getConfigSettings().getConfigParser().getSpawnLocation();
            if (loc.isEmpty())
                defaultLocation = new BLocation(getWorld().getSpawnLocation());
            else
                defaultLocation = new BLocation(plugin.getConfigSettings().getConfigParser().getSpawnLocation());
        }

        return defaultLocation;
    }

    @Override
    public BLocation getLastLocation(BPlayer bPlayer) {
        return getDefaultLocation();
    }

    @Override
    public void setDefaultLocation(BLocation defaultLocation) {
        //noop
    }

    @Override
    public World getWorld() {
        World world = plugin.getServer().getWorld(getWorldPath());
        return world == null ? plugin.getServer().getWorlds().get(0) : world;
    }

    @Override
    public GameMode getDefaultGameMode() {
        if (defaultGameMode == null)
            setDefaultGameMode(GameMode.valueOf(plugin.getConfigSettings().getConfigParser().getSpawnDefaultGamemode()));

        return defaultGameMode;
    }

    @Override
    public void setDefaultGameMode(GameMode gamemode) {
        this.defaultGameMode = gamemode;
        updatePlayerGameModes();
    }

    @Override
    public Set<BPlayer> getActivePlayers() {
        return getWorld().getPlayers().stream()
                .map(player -> plugin.getBPlayerManager().get(player))
                .collect(Collectors.toSet());
    }

}