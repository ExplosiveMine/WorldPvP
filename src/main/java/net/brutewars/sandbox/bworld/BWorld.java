package net.brutewars.sandbox.bworld;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.world.location.BLocation;
import net.brutewars.sandbox.bworld.world.location.LastLocationTracker;
import net.brutewars.sandbox.bworld.settings.WorldSettings;
import net.brutewars.sandbox.bworld.settings.WorldSettingsContainer;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.thread.Executor;
import net.brutewars.sandbox.utils.Logging;
import net.brutewars.sandbox.bworld.world.LoadingPhase;
import net.brutewars.sandbox.bworld.world.dimensions.SandboxWorld;
import net.brutewars.sandbox.bworld.world.size.BorderSize;
import net.brutewars.sandbox.bworld.world.size.WorldSizes;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.util.*;
import java.util.stream.Collectors;

public final class BWorld implements WorldSettingsContainer {
    private final BWorldPlugin plugin;
    @Getter private final UUID uuid;
    @Getter private BPlayer owner;
    private final Set<BPlayer> players = new HashSet<>();
    private final Map<BPlayer, BPlayer> invitedPlayers = new HashMap<>();

    @Getter private final WorldSettings settings = new WorldSettings();

    // Loading & Unloading
    @Getter @Setter private int resetting = -1;
    @Getter @Setter private int unloading = -1;

    private final Map<World.Environment, SandboxWorld> worlds;

    public BWorld(BWorldPlugin plugin, UUID uuid, Map<World.Environment, SandboxWorld> worlds) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.worlds = worlds;
    }

    public void setOwner(BPlayer owner) {
        this.owner = owner;
        owner.setBWorld(this);
        getLastLocationTracker().startTracking(owner);

        updateWorldSize();
    }

    public String getAlias() {
        return getOwner().getName();
    }

    /*
    ----------------------------------------------------------
                            PLAYERS
    ----------------------------------------------------------
     */

    public Set<BPlayer> getOnlineMembers() {
        return getMembers(true).stream().filter(BPlayer::isOnline).collect(Collectors.toSet());
    }

    public Set<BPlayer> getMembers(boolean includeOwner) {
        Set<BPlayer> _players = new HashSet<>(players);
        if (includeOwner)
            _players.add(owner);
        return _players;
    }

    public void addMember(BPlayer bPlayer, BLocation bLocation) {
        Preconditions.checkNotNull(bPlayer, "bPlayer parameter cannot be null");

        bPlayer.addBWorld(this);

        LastLocationTracker tracker = getLastLocationTracker();
        tracker.startTracking(bPlayer);
        if (bLocation != null)
            tracker.updateLastLocation(bPlayer, bLocation);

        players.add(bPlayer);
    }

    public void removeMember(BPlayer bPlayer) {
        Preconditions.checkNotNull(bPlayer, "bPlayer parameter cannot be null");

        bPlayer.removeBWorld(this);

        bPlayer.runIfOnline(player -> {
            World playerWorld = player.getWorld();
            if (playerWorld.equals(getWorld(playerWorld.getEnvironment())))
                plugin.getBWorldManager().getSpawn().teleportToWorld(bPlayer);
        });

        players.remove(bPlayer);
        getLastLocationTracker().stopTracking(bPlayer);
    }

    public Set<BPlayer> getActivePlayers() {
        Set<BPlayer> activePlayers = new HashSet<>();
        for (SandboxWorld sandboxWorld : getLoadedWorlds()) {
            activePlayers.addAll(sandboxWorld.getPlayers().stream()
                    .filter(bPlayer -> bPlayer.isInBWorld(BWorld.this, true))
                    .collect(Collectors.toSet()));
        }
        return activePlayers;
    }

    /*
    ----------------------------------------------------------
                             INVITES
    ----------------------------------------------------------
     */

    public boolean isInvited(BPlayer invitee) {
        return invitedPlayers.containsKey(invitee);
    }

    public void invite(BPlayer inviter, BPlayer invitee) {
        invitedPlayers.put(invitee, inviter);
        Executor.sync(plugin, unused -> invitedPlayers.remove(invitee), plugin.getConfigSettings().getConfigParser().getInvitingTime());
    }

    public BPlayer getInviter(BPlayer invitee) {
        return invitedPlayers.get(invitee);
    }

    public void removeInvite(BPlayer invitee) {
        invitedPlayers.remove(invitee);
    }

    /*
    ----------------------------------------------------------
                           DIMENSIONS
    ----------------------------------------------------------
     */

    public LoadingPhase getLoadingPhase(World.Environment env) {
        return worlds.get(env).getLoadingPhase();
    }

    public void setWorldPhases(LoadingPhase loadingPhase, World.Environment env) {
        worlds.get(env).setLoadingPhase(loadingPhase);
    }

    public World getWorld() {
        return getWorld(World.Environment.NORMAL);
    }

    public World getWorld(World.Environment env) {
        return getSandboxWorld(env).getWorld();
    }

    public void loadSandboxWorld(World.Environment env) {
        SandboxWorld sandboxWorld = getSandboxWorld(env);
        Logging.debug(plugin, "Loading world for " + getAlias() + ": " + sandboxWorld.getName());
        sandboxWorld.loadWorld();
        applySandboxSettings();
        Lang.WORLD_LOADED.send(this);
    }

    public SandboxWorld getSandboxWorld(World.Environment env) {
        return worlds.get(env);
    }

    public List<SandboxWorld> getLoadedWorlds() {
        return getEnvironments(LoadingPhase.LOADED).stream()
                .map(worlds::get)
                .collect(Collectors.toList());
    }

    public List<World.Environment> getEnvironments(LoadingPhase loadingPhase) {
        return worlds.entrySet().stream()
                .filter(entry -> entry.getValue().getLoadingPhase() == loadingPhase)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void initialiseReset() {
        Logging.debug(plugin, "Initialised reset for: " + getAlias());

        setResetting(Executor.sync(plugin, unused -> {
            Logging.debug(plugin, "Reset cancelled for: " + getAlias());
            setResetting(-1);
        }, plugin.getConfigSettings().getConfigParser().getResettingTime()));
    }

    public void updateWorldSize() {
        BorderSize maxSize = WorldSizes.getDefaultSize();

        for (BorderSize worldSize : WorldSizes.getValues()) {
            if (getOwner().hasPermission(worldSize.getPermission()) && worldSize.getSize() > maxSize.getSize())
                maxSize = worldSize;
        }

        BorderSize worldSize = settings.getBorderSize();
        if (worldSize == maxSize)
            return;

        Logging.debug(plugin, "Updated world size from " +  worldSize.getSize() + " to " + maxSize.getSize() + " for: " + getAlias());

        for (SandboxWorld sandboxWorld : getLoadedWorlds()) {
            Lang.WORLD_BORDER_UPDATE.send(this, (maxSize.getSize() > worldSize.getSize() ? "increased" : "decreased"), worldSize.getSize(), maxSize.getSize());
            sandboxWorld.setWorldBorder(maxSize);
        }

        settings.setBorderSize(maxSize);
    }

    public void initialiseUnloading() {
        Logging.debug(plugin, "Initialised unloading for: " + getAlias());

        setUnloading(Executor.sync(plugin, unused -> {
            if (getOnlineMembers().size() != 0)
                return;

            Logging.debug(plugin, "Unloading world: " + getAlias());
            plugin.getSandboxManager().unloadSandboxWorlds(this, true);
        }, plugin.getConfigSettings().getConfigParser().getUnloadingTime()));
    }

    public void cancelUnloading() {
        Logging.debug(plugin, "Unloading cancelled for: " + getAlias());
        plugin.getServer().getScheduler().cancelTask(getUnloading());
    }

    /*
    ----------------------------------------------------------
                WORLD TELEPORTATION & LASTLOCATION
    ----------------------------------------------------------
     */

    public void teleportToWorld(BPlayer bPlayer) {
        teleportToWorld(bPlayer, World.Environment.NORMAL);
    }

    public void teleportToWorld(BPlayer bPlayer, World.Environment env) {
        getSandboxWorld(env).teleportToWorld(bPlayer);
    }

    public LastLocationTracker getLastLocationTracker() {
        return (LastLocationTracker) getSandboxWorld(World.Environment.NORMAL);
    }

    public void onPlayerTeleport(BPlayer bPlayer, Location from, Location to) {
        // When the player goes from nether/end to the overworld we don't save the last location
        World worldTo = to.getWorld();
        if (from.getWorld().equals(worldTo))
            return;

        BWorld _bWorld = plugin.getBWorldManager().getBWorld(worldTo);
        if (equals(_bWorld)) {
            // if they are touching an end portal block, teleport them to another location
            if (from.getWorld().getEnvironment() == World.Environment.THE_END)
                getLastLocationTracker().updateLastLocation(bPlayer, getLastLocationTracker().getDefaultLocation());
            else if (worldTo.getEnvironment() == World.Environment.NETHER)
                getLastLocationTracker().updateLastLocation(bPlayer, from);
        } else
            getLastLocationTracker().updateLastLocation(bPlayer, from);
    }

    /*
    ----------------------------------------------------------
                           WORLD SETTINGS
    ----------------------------------------------------------
     */

    public void applySandboxSettings() {
        updatePlayerGameModes();

        for (SandboxWorld sandboxWorld : getLoadedWorlds()) {
            sandboxWorld.setWorldBorder(settings.getBorderSize());
            World loadedWorld = sandboxWorld.getWorld();
            loadedWorld.setDifficulty(settings.getDifficulty());
            loadedWorld.setGameRule(GameRule.KEEP_INVENTORY, settings.isKeepInventory());

            if (!settings.isAnimals()) {
                loadedWorld.getEntities().forEach(entity -> {
                    if (!shouldEntityExist(entity.getType()) && entity.customName() == null)
                        entity.remove();
                });
            }
        }
    }

    public void updatePlayerGameModes() {
        for (BPlayer bPlayer : getActivePlayers())
            bPlayer.setGameMode(settings.getDefaultGameMode());
    }

    /**
     * @param bPlayer player who tries to build/break something
     * @return whether to cancel the event
     */
    public boolean canPlayerBuild(BPlayer bPlayer) {
        return bPlayer.isInBWorld(this, true) || settings.isPlayersCanBuild();
    }

    /**
     * @return whether a creature should be spawned or removed based on current
     * settings
     */
    public boolean shouldEntityExist(EntityType entityType) {
        return settings.isAnimals() || !isAnimal(entityType);
    }

    private boolean isAnimal(EntityType entityType) {
        Class<? extends Entity> entityClass = entityType.getEntityClass();
        return entityClass != null &&
                (Animals.class.isAssignableFrom(entityType.getEntityClass() )
                || WaterMob.class.isAssignableFrom(entityType.getEntityClass())
                || Ambient.class.isAssignableFrom(entityType.getEntityClass()));
    }

}