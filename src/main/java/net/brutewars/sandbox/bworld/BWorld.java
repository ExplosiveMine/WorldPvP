package net.brutewars.sandbox.bworld;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.lastlocation.LastLocation;
import net.brutewars.sandbox.config.Lang;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.thread.Executor;
import net.brutewars.sandbox.utils.Logging;
import net.brutewars.sandbox.world.LoadingPhase;
import net.brutewars.sandbox.world.WorldSize;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;
import java.util.stream.Collectors;

public final class BWorld implements IBWorld {
    private final BWorldPlugin plugin;

    @Getter private final UUID uuid;

    @Getter private BPlayer owner;

    private final Set<BPlayer> players = new HashSet<>();
    private final Map<BPlayer, BPlayer> invitedPlayers = new HashMap<>();

    @Getter private LastLocation defaultLocation;
    private final Map<BPlayer, LastLocation> lastLocations = new HashMap<>();

    @Getter private WorldSize worldSize;

    @Getter @Setter private int unloading = -1;
    @Getter @Setter private LoadingPhase loadingPhase = LoadingPhase.UNLOADED;

    @Getter @Setter private int resetting = -1;

    public BWorld(final BWorldPlugin plugin, UUID uuid) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.owner = null;
        this.defaultLocation = null;
        this.worldSize = WorldSize.DEFAULT;
    }

    public void init(final BPlayer owner) {
        // add the owner to the BWorld
        this.owner = owner;
        owner.setBWorld(this);
        lastLocations.put(owner, null);

        updateWorldSize();
    }

    @Override
    public Set<BPlayer> getOnlineBPlayers() {
        return getPlayers(true).stream().filter(BPlayer::isOnline).collect(Collectors.toSet());
    }

    @Override
    public Set<BPlayer> getPlayers(boolean includeOwner) {
        final Set<BPlayer> _players = new HashSet<>(players);
        if (includeOwner)
            _players.add(owner);
        return _players;
    }

    @Override
    public void addPlayer(BPlayer bPlayer, LastLocation lastLocation) {
        Preconditions.checkNotNull(bPlayer, "bPlayer parameter cannot be null");

        bPlayer.addBWorld(this);

        if (lastLocation == null)
            lastLocation = defaultLocation;

        lastLocations.put(bPlayer, lastLocation);
        players.add(bPlayer);
    }

    @Override
    public void removePlayer(BPlayer bPlayer) {
        Preconditions.checkNotNull(bPlayer, "bPlayer parameter cannot be null");

        bPlayer.removeBWorld(this);
        bPlayer.runIfOnline(player -> {
            //todo this doesnt seem to work
            if (player.getWorld().getName().equals(getWorldName()))
                plugin.getBWorldManager().getSpawn().teleportToWorld(bPlayer);
        });

        players.remove(bPlayer);
    }

    @Override
    public boolean isInvited(BPlayer invitee) {
        return invitedPlayers.containsKey(invitee);
    }

    @Override
    public void invite(BPlayer inviter, BPlayer invitee) {
        invitedPlayers.put(invitee, inviter);
        Executor.syncTimer(plugin, unused -> invitedPlayers.remove(invitee), plugin.getBWorldManager().INVITING_TIME);
    }

    @Override
    public BPlayer getInviter(BPlayer invitee) {
        return invitedPlayers.get(invitee);
    }

    @Override
    public void removeInvite(BPlayer invitee) {
        invitedPlayers.remove(invitee);
    }

    @Override
    public void initialiseReset() {
        Logging.debug(plugin, "Initialised reset for: " + getAlias());

        setResetting(Executor.syncTimer(plugin, unused -> {
            Logging.debug(plugin, "Reset cancelled for: " + getAlias());
            setResetting(-1);
        }, plugin.getBWorldManager().RESETTING_TIME));
    }

    @Override
    public void updateWorldSize() {
        WorldSize maxSize = WorldSize.DEFAULT;

        for (WorldSize worldSize : WorldSize.values()) {
            if (getOwner().hasPermission(worldSize.getPermission()) && worldSize.getValue() > maxSize.getValue())
                maxSize = worldSize;
        }

        if (worldSize.equals(maxSize))
            return;

        Logging.debug(plugin, "Updated WorldSize for: " + getAlias());

        if (loadingPhase.equals(LoadingPhase.LOADED)) {
            Lang.WORLD_BORDER_UPDATE.send(this, (maxSize.getValue() > worldSize.getValue() ? "increased" : "decreased"),worldSize.getValue(), maxSize.getValue());
            plugin.getBWorldManager().getWorldFactory().setWorldBorder(this, maxSize);
        }

        this.worldSize = maxSize;
    }

    @Override
    public void initialiseUnloading() {
        Logging.debug(plugin, "Initialised unloading for: " + getAlias());

        setUnloading(Executor.syncTimer(plugin, unused -> {
            if (getOnlineBPlayers().size() != 0) return;
            Logging.debug(plugin, "Unloading world: " + getAlias());
            plugin.getBWorldManager().getWorldFactory().unload(BWorld.this, true);
        }, plugin.getBWorldManager().UNLOADING_TIME));
    }

    @Override
    public void cancelUnloading() {
        Logging.debug(plugin, "Unloading cancelled for: " + getAlias());
        plugin.getServer().getScheduler().cancelTask(getUnloading());
    }

    @Override
    public String getWorldName() {
        return uuid.toString();
    }

    @Override
    public String getAlias() {
        return getOwner().getName();
    }

    @Override
    public void teleportToWorld(final BPlayer bPlayer) {
        final LastLocation lastLoc = lastLocations.put(bPlayer, defaultLocation);
        if (lastLoc != null)
            bPlayer.teleport(lastLoc.toLoc(getWorld()));
    }

    @Override
    public World getWorld() {
        return plugin.getBWorldManager().getWorldFactory().getWorld(this);
    }

    @Override
    public void updateLastLocation(final BPlayer bPlayer, final Location location) {
        lastLocations.put(bPlayer, new LastLocation(location));
    }

    @Override
    public LastLocation getLastLocation(final BPlayer bPlayer) {
        lastLocations.computeIfAbsent(bPlayer, k -> defaultLocation);
        return lastLocations.get(bPlayer);
    }

    @Override
    public void setDefaultLocation(final LastLocation defaultLocation) {
        this.defaultLocation = defaultLocation;
        lastLocations.replaceAll((bPlayer, lastLocation) -> lastLocation == null ? defaultLocation : lastLocation);
    }

}