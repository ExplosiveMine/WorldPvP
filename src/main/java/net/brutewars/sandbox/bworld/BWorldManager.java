package net.brutewars.sandbox.bworld;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.world.dimensions.SpawnWorld;
import net.brutewars.sandbox.bworld.settings.WorldSettingsContainer;
import net.brutewars.sandbox.bworld.world.ImportOptions;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.utils.Logging;
import net.brutewars.sandbox.utils.StringUtils;
import net.brutewars.sandbox.bworld.world.LoadingPhase;
import net.brutewars.sandbox.bworld.world.dimensions.SandboxWorld;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public final class BWorldManager {
    private final BWorldPlugin plugin;

    private SpawnWorld spawn;
    private final Map<UUID, BWorld> bWorlds = new HashMap<>();

    private final Map<BPlayer, WorldType> pendingRequests = new HashMap<>();

    public BWorldManager(BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    public SpawnWorld getSpawn() {
        if (spawn == null)
            spawn = new SpawnWorld(plugin, plugin.getConfigSettings().getConfigParser().getWorldName());

        return spawn;
    }

    public void createRequest(BPlayer bPlayer, WorldType worldType) {
        pendingRequests.put(bPlayer, worldType);
    }

    public void resolveRequest(BPlayer bPlayer, boolean generateSpawn) {
        createBWorld(bPlayer, pendingRequests.remove(bPlayer), generateSpawn);
    }

    private void createBWorld(BPlayer owner, WorldType worldType, boolean generateSpawn) {
        BWorld bWorld = loadBWorld(generateNextUuid(), owner, worldType, generateSpawn);
        bWorld.loadSandboxWorld(World.Environment.NORMAL);
    }

    public BWorld loadBWorld(UUID uuid, BPlayer owner, WorldType worldType, boolean generateSpawn) {
        ImportOptions options = new ImportOptions()
                .setWorldType(worldType)
                .setGenerateCustomSpawn(generateSpawn);

        BWorld bWorld = new BWorld(plugin, uuid, plugin.getSandboxManager().importSandboxWorlds(uuid, options));
        bWorld.setOwner(owner);

        bWorlds.put(uuid, bWorld);
        return bWorld;
    }

    public void removeBWorld(BWorld bWorld) {
        if (bWorld.getResetting() != -1) {
            Logging.debug(plugin, "Resetting: " + bWorld.getAlias());
            plugin.getServer().getScheduler().cancelTask(bWorld.getResetting());
        }

        // teleport visitors in that world to spawn if its loaded
        for (SandboxWorld sandboxWorld : bWorld.getLoadedWorlds()) {
            sandboxWorld.getPlayers().stream()
                    .filter(bPlayer -> !bPlayer.isInBWorld(bWorld, false))
                    .forEach(bPlayer -> {
                        Lang.ON_RESET_VISITOR.send(bPlayer);
                        getSpawn().teleportToWorld(bPlayer);
                    });
        }

        bWorld.getMembers(false).forEach(bWorld::removeMember);
        bWorld.getOwner().setBWorld(null);

        plugin.getSandboxManager().deleteWorldFiles(bWorld);
        bWorlds.remove(bWorld.getUuid());
    }

    public BWorld getBWorld(UUID uuid) {
        return bWorlds.get(uuid);
    }

    public BWorld getBWorld(World world) {
        String[] str = world.getName().split(File.separator);
        if (str.length < 4 || !StringUtils.isUUID(str[3]))
            return null;

        return getBWorld(UUID.fromString(str[3]));
    }

    public WorldSettingsContainer getSettingsContainer(World world) {
        if (spawn.getName().equals(world.getName()))
            return spawn;

        return getBWorld(world);
    }

    public Collection<BWorld> getBWorlds() {
        return Collections.unmodifiableCollection(bWorlds.values());
    }

    private UUID generateNextUuid() {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (getBWorld(uuid) != null);

        return uuid;
    }

    public void updateLastLocations() {
        for (BWorld bWorld : plugin.getBWorldManager().getBWorlds()) {
            if (bWorld.getLoadingPhase(World.Environment.NORMAL) != LoadingPhase.LOADED)
                continue;

            bWorld.getWorld().getPlayers().stream()
                    .map(player -> plugin.getBPlayerManager().get(player))
                    .filter(bPlayer -> bPlayer.isInBWorld(bWorld, true))
                    .forEach(bPlayer -> bWorld.getLastLocationTracker().updateLastLocation(bPlayer, (Location) bPlayer.getIfOnline(Player::getLocation)));
        }
    }

}