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
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public final class BWorld implements IBWorld {
    private final BWorldPlugin plugin;

    @Getter private final UUID uuid;
    @Getter private BPlayer owner;

    @Getter private final String worldPath;

    private final Set<BPlayer> players = new HashSet<>();
    private final Map<BPlayer, BPlayer> invitedPlayers = new HashMap<>();

    //World Settings
    @Getter private WorldSize worldSize = WorldSize.DEFAULT;
    @Getter private Difficulty difficulty = Difficulty.NORMAL;
    @Getter private GameMode defaultGameMode = GameMode.SURVIVAL;
    @Getter private boolean animals = true;
    @Getter @Setter private boolean aggressiveMonsters = true;
    @Getter @Setter private boolean membersCanBuild = true;
    @Getter private boolean keepInventory = false;

    // Last location
    @Getter private BLocation defaultLocation;
    private final Map<BPlayer, BLocation> lastLocations = new HashMap<>();

    // Loading & Unloading
    @Getter @Setter private int resetting = -1;
    @Getter @Setter private int unloading = -1;
    private final Map<World.Environment, LoadingPhase> worldPhases = new HashMap<>() {{
        put(World.Environment.NORMAL, LoadingPhase.UNLOADED);
        put(World.Environment.NETHER, LoadingPhase.UNLOADED);
        put(World.Environment.THE_END, LoadingPhase.UNLOADED);
    }};

    public BWorld(BWorldPlugin plugin, UUID uuid) {
        this.plugin = plugin;
        this.uuid = uuid;
        worldPath = plugin.getDataFolder() + File.separator + "worlds" + File.separator + uuid.toString() + File.separator;
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
    public String getAlias() {
        return getOwner().getName();
    }

    /*
    ----------------------------------------------------------
                            PLAYERS
    ----------------------------------------------------------
     */

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
            if (player.getWorld().getName().equals(getWorldPath()))
                plugin.getBWorldManager().getSpawn().teleportToWorld(bPlayer);
        });

        players.remove(bPlayer);
        lastLocations.remove(bPlayer);
    }

    public Set<BPlayer> getActivePlayers() {
        Set<BPlayer> activePlayers = new HashSet<>();
        for (World loadedWorld : getLoadedWorlds()) {
            activePlayers.addAll(loadedWorld.getPlayers().stream()
                    .map(player -> plugin.getBPlayerManager().get(player))
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

    /*
    ----------------------------------------------------------
                     WORLD LOADING & UNLOADING
    ----------------------------------------------------------
     */

    public LoadingPhase getWorldPhase(World.Environment env) {
        return worldPhases.get(env);
    }

    public void setWorldPhases(LoadingPhase loadingPhase, World.Environment env) {
        worldPhases.put(env, loadingPhase);
    }

    public void initialiseReset() {
        Logging.debug(plugin, "Initialised reset for: " + getAlias());

        setResetting(Executor.sync(plugin, unused -> {
            Logging.debug(plugin, "Reset cancelled for: " + getAlias());
            setResetting(-1);
        }, plugin.getConfigSettings().getConfigParser().getResettingTime()));
    }

    public void updateWorldSize() {
        WorldSize maxSize = WorldSize.DEFAULT;

        for (WorldSize worldSize : WorldSize.values()) {
            if (getOwner().hasPermission(worldSize.getPermission()) && worldSize.getValue() > maxSize.getValue())
                maxSize = worldSize;
        }

        if (worldSize == maxSize)
            return;

        Logging.debug(plugin, "Updated world size from " +  worldSize.getValue() + " to " + maxSize.getValue() + " for: " + getWorldSize());

        for (World world : getLoadedWorlds()) {
            Lang.WORLD_BORDER_UPDATE.send(this, (maxSize.getValue() > worldSize.getValue() ? "increased" : "decreased"), worldSize.getValue(), maxSize.getValue());
            plugin.getBWorldManager().getWorldManager().getWorldFactory().setWorldBorder(world, maxSize);
        }

        this.worldSize = maxSize;
    }

    public void initialiseUnloading() {
        Logging.debug(plugin, "Initialised unloading for: " + getAlias());

        setUnloading(Executor.sync(plugin, unused -> {
            if (getOnlineBPlayers().size() != 0)
                return;

            Logging.debug(plugin, "Unloading world: " + getAlias());
            plugin.getBWorldManager().getWorldManager().unload(this, true);
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

    @Override
    public void teleportToWorld(BPlayer bPlayer) {
        teleportToWorld(bPlayer, World.Environment.NORMAL);
    }

    public void teleportToWorld(BPlayer bPlayer, World.Environment env) {
        Location toTeleport = null;
        if (env == World.Environment.NORMAL) {
            BLocation lastLoc = lastLocations.put(bPlayer, defaultLocation);
            if (lastLoc == null)
                return;

            toTeleport = lastLoc.toLoc(getWorld());
            // in creative if they player is being teleported to a nether portal they will get teleported
            // to the nether
            if (bPlayer.getIfOnline(Player::getGameMode) == GameMode.CREATIVE)
                toTeleport = defaultLocation.toLoc(getWorld());
        } else if (env == World.Environment.THE_END) {
            toTeleport = getWorld(World.Environment.THE_END).getSpawnLocation();
        }

        if (toTeleport != null) {
            Location finalToTeleport = toTeleport;
            bPlayer.runIfOnline(player -> player.teleportAsync(finalToTeleport, PlayerTeleportEvent.TeleportCause.PLUGIN));
        }
    }

    public void updateLastLocation(BPlayer bPlayer, Location location) {
        updateLastLocation(bPlayer, new BLocation(location));
    }

    public void updateLastLocation(BPlayer bPlayer, BLocation bLocation) {
        lastLocations.put(bPlayer, bLocation);
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
        return getWorld(World.Environment.NORMAL);
    }

    public World getWorld(World.Environment env) {
        return plugin.getBWorldManager().getWorldManager().getWorld(this, env);
    }

    public List<World> getLoadedWorlds() {
        return getEnvironments(LoadingPhase.LOADED).stream()
                .map(this::getWorld)
                .collect(Collectors.toList());
    }

    public List<World.Environment> getEnvironments(LoadingPhase loadingPhase) {
        return worldPhases.keySet().stream()
                .filter(env -> getWorldPhase(env) == loadingPhase)
                .collect(Collectors.toList());
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
                updateLastLocation(bPlayer, getDefaultLocation());
            else if (worldTo.getEnvironment() == World.Environment.NETHER)
                updateLastLocation(bPlayer, from);
        } else
            updateLastLocation(bPlayer, from);
    }

    /*
    ----------------------------------------------------------
                        WORLD SETTINGS
    ----------------------------------------------------------
     */

    /**
     * @param bPlayer player who tries to build/break something
     * @return whether to cancel the event
     */
    public boolean canPlayerBuild(BPlayer bPlayer) {
        return equals(bPlayer.getBWorld())  // player is NOT the owner
                || bPlayer.isInBWorld(this, false) && isMembersCanBuild(); // NOT(player is member & members can build)
    }

    @Override
    public void setDefaultGameMode(GameMode gamemode) {
        this.defaultGameMode = gamemode;
        updatePlayerGameModes();
    }

    public void setDifficulty(Difficulty difficulty, boolean updateWorld) {
        this.difficulty = difficulty;
        if (updateWorld) {
            for (World loadedWorld : getLoadedWorlds())
                loadedWorld.setDifficulty(difficulty);
        }
    }

    public void setKeepInventory(boolean keepInventory) {
        this.keepInventory = keepInventory;
        for (World loadedWorld : getLoadedWorlds())
            loadedWorld.setGameRule(GameRule.KEEP_INVENTORY, keepInventory);
    }

    public void setAnimals(boolean animals) {
        this.animals = animals;

        if (!animals) {
            getLoadedWorlds().forEach(world -> world.getEntities().forEach(entity -> {
                if (!shouldEntityExist(entity.getType()) && entity.customName() == null)
                    entity.remove();
            }));
        }

    }

    /**
     * @return whether a creature should be spawned or removed based on current
     * settings
     */
    public boolean shouldEntityExist(EntityType entityType) {
        return animals || !isAnimal(entityType);
    }

    private boolean isAnimal(EntityType entityType) {
        Class<? extends Entity> entityClass = entityType.getEntityClass();
        return entityClass != null &&
                (Animals.class.isAssignableFrom(entityType.getEntityClass() )
                || WaterMob.class.isAssignableFrom(entityType.getEntityClass())
                || Ambient.class.isAssignableFrom(entityType.getEntityClass()));
    }

}