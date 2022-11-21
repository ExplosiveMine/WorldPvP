package net.brutewars.sandbox.bworld;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.lastlocation.BLocation;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.thread.Executor;
import net.brutewars.sandbox.utils.Logging;
import net.brutewars.sandbox.world.LoadingPhase;
import net.brutewars.sandbox.world.WorldSize;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class BWorld implements IBWorld {
    private final BWorldPlugin plugin;

    @Getter private final UUID uuid;

    @Getter private BPlayer owner;
    private final Set<BPlayer> players = new HashSet<>();

    @Getter private final String worldName;
    @Getter private WorldSize worldSize = WorldSize.DEFAULT;
    @Getter private Difficulty difficulty = Difficulty.NORMAL;
    @Getter @Setter private boolean cheating = false;

    @Getter private BLocation defaultLocation;
    private final Map<BPlayer, BLocation> lastLocations = new HashMap<>();

    @Getter @Setter private int resetting = -1;
    @Getter @Setter private int unloading = -1;
    @Getter @Setter private LoadingPhase loadingPhase = LoadingPhase.UNLOADED;

    private final Map<BPlayer, BPlayer> invitedPlayers = new HashMap<>();


    public BWorld(BWorldPlugin plugin, UUID uuid) {
        this.plugin = plugin;
        this.uuid = uuid;
        worldName = plugin.getDataFolder() + File.separator + "worlds" + File.separator + uuid.toString();
        owner = null;
        defaultLocation = null;
    }

    public void setOwner(BPlayer owner) {
        // add the owner to the BWorld
        this.owner = owner;
        owner.setBWorld(this);
        lastLocations.put(owner, defaultLocation);

        updateWorldSize();
    }

    @Override
    public Set<BPlayer> getOnlineBPlayers() {
        return getPlayers(true).stream().filter(BPlayer::isOnline).collect(Collectors.toSet());
    }

    @Override
    public Set<BPlayer> getPlayers(boolean includeOwner) {
        Set<BPlayer> _players = new HashSet<>(players);
        if (includeOwner)
            _players.add(owner);
        return _players;
    }

    @Override
    public void addPlayer(BPlayer bPlayer, BLocation bLocation) {
        Preconditions.checkNotNull(bPlayer, "bPlayer parameter cannot be null");

        bPlayer.addBWorld(this);

        if (bLocation == null)
            bLocation = defaultLocation;

        lastLocations.put(bPlayer, bLocation);
        players.add(bPlayer);
    }

    @Override
    public void removePlayer(BPlayer bPlayer) {
        Preconditions.checkNotNull(bPlayer, "bPlayer parameter cannot be null");

        bPlayer.removeBWorld(this);

        bPlayer.runIfOnline(player -> {
            if (player.getWorld().getName().equals(getWorldName()))
                plugin.getBWorldManager().getSpawn().teleportToWorld(bPlayer);
        });

        players.remove(bPlayer);
        lastLocations.remove(bPlayer);
    }

    @Override
    public boolean isInvited(BPlayer invitee) {
        return invitedPlayers.containsKey(invitee);
    }

    @Override
    public void invite(BPlayer inviter, BPlayer invitee) {
        invitedPlayers.put(invitee, inviter);
        Executor.sync(plugin, unused -> invitedPlayers.remove(invitee), plugin.getConfigSettings().getConfigParser().getInvitingTime());
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

        setResetting(Executor.sync(plugin, unused -> {
            Logging.debug(plugin, "Reset cancelled for: " + getAlias());
            setResetting(-1);
        }, plugin.getConfigSettings().getConfigParser().getResettingTime()));
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

        Logging.debug(plugin, "Updated WorldSize from " +  worldSize.getValue() + " to " + maxSize.getValue() + " for: " + getAlias());

        if (loadingPhase.equals(LoadingPhase.LOADED)) {
            Lang.WORLD_BORDER_UPDATE.send(this, (maxSize.getValue() > worldSize.getValue() ? "increased" : "decreased"), worldSize.getValue(), maxSize.getValue());
            plugin.getBWorldManager().getWorldManager().getWorldFactory().setWorldBorder(getWorld(), maxSize);
        }

        this.worldSize = maxSize;
    }

    @Override
    public void initialiseUnloading() {
        Logging.debug(plugin, "Initialised unloading for: " + getAlias());

        setUnloading(Executor.sync(plugin, unused -> {
            if (getOnlineBPlayers().size() != 0) return;
            Logging.debug(plugin, "Unloading world: " + getAlias());
            plugin.getBWorldManager().getWorldManager().unload(BWorld.this, true);
        }, plugin.getConfigSettings().getConfigParser().getUnloadingTime()));
    }

    @Override
    public void cancelUnloading() {
        Logging.debug(plugin, "Unloading cancelled for: " + getAlias());
        plugin.getServer().getScheduler().cancelTask(getUnloading());
    }

    @Override
    public String getAlias() {
        return getOwner().getName();
    }

    @Override
    public void teleportToWorld(BPlayer bPlayer) {
        teleportToWorld(bPlayer, Boolean::booleanValue);
    }

    @Override
    public void teleportToWorld(BPlayer bPlayer, Consumer<Boolean> consumer) {
        BLocation lastLoc = lastLocations.put(bPlayer, defaultLocation);
        if (lastLoc == null)
            return;

        bPlayer.runIfOnline(player -> player.teleportAsync(lastLoc.toLoc(getWorld()), PlayerTeleportEvent.TeleportCause.PLUGIN)
                .whenComplete((bool, throwable) -> consumer.accept(bool)));
    }

    @Override
    public void updateLastLocation(BPlayer bPlayer, Location location) {
        lastLocations.put(bPlayer, new BLocation(location));
    }

    @Override
    public BLocation getLastLocation(BPlayer bPlayer) {
        lastLocations.computeIfAbsent(bPlayer, k -> defaultLocation);
        return lastLocations.get(bPlayer);
    }

    @Override
    public void setDefaultLocation(BLocation defaultLocation) {
        this.defaultLocation = defaultLocation;
        lastLocations.replaceAll((bPlayer, lastLocation) -> lastLocation == null ? defaultLocation : lastLocation);
    }

    @Override
    public World getWorld() {
        return plugin.getBWorldManager().getWorldManager().getWorld(this);
    }

    public void setDifficulty(Difficulty difficulty, boolean updateWorld) {
        this.difficulty = difficulty;
        if (updateWorld)
            getWorld().setDifficulty(difficulty);
    }

}