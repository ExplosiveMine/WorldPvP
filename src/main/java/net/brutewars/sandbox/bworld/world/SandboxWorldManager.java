package net.brutewars.sandbox.bworld.world;

import lombok.Getter;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.BWorld;
import net.brutewars.sandbox.utils.FileUtils;
import net.brutewars.sandbox.utils.Logging;
import net.brutewars.sandbox.bworld.world.dimensions.SandboxWorld;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public final class SandboxWorldManager {
    private final BWorldPlugin plugin;
    private final Set<String> deletedWorlds = new HashSet<>();

    public SandboxWorldManager(BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * @param bWorldUUID - the bWorld uuid
     * @param env - set to null to get the BWorld's worlds folder
     * @return the specified environment's world folder or the BWorld's worlds folder
     */
    private String getWorldPath(UUID bWorldUUID, @Nullable World.Environment env) {
        String path = plugin.getDataFolder() + File.separator + "worlds" + File.separator + bWorldUUID.toString();

        if (env != null)
            path = path + File.separator + env;

        return path;
    }

    /**
     * @return SandboxWorlds for the 3 main {@link World.Environment}, i.e. NORMAL, NETHER,
     * THE_END. This method is used to populate the worlds when creating a {@link BWorld} object
     */
    public Map<World.Environment, SandboxWorld> importSandboxWorlds(UUID uuid, ImportOptions importOptions) {
        return new HashMap<>() {{
            put(World.Environment.NORMAL, importSandboxWorld(uuid, importOptions, World.Environment.NORMAL));
            put(World.Environment.NETHER, importSandboxWorld(uuid, importOptions, World.Environment.NETHER));
            put(World.Environment.THE_END, importSandboxWorld(uuid, importOptions, World.Environment.THE_END));
        }};
    }

    /**
     * @return A {@link SandboxWorld} object by calling {@link WorldRoster#getSandboxWorld(World.Environment, ImportOptions)},
     */
    private SandboxWorld importSandboxWorld(UUID uuid, ImportOptions importOptions, World.Environment env) {
        importOptions.setName(getWorldPath(uuid, env)).setEnvironment(env);
        return plugin.getWorldRoster().getSandboxWorld(env, importOptions);
    }

    public void deleteWorldFiles(BWorld bWorld) {
        Logging.debug(plugin, "Deleting world files for " + bWorld.getAlias());
        unloadSandboxWorlds(bWorld, false);
        deletedWorlds.add(getWorldPath(bWorld.getUuid(), null));
    }

    public void unloadSandboxWorlds(BWorld bWorld, boolean save) {
        if (save)
            Logging.debug(plugin, "Saving world for " + bWorld.getAlias());

        Logging.debug(plugin, "Unloaded world for " + bWorld.getAlias());

        bWorld.getLoadedWorlds().forEach(sandboxWorld -> {
            World.Environment env = sandboxWorld.getWorld().getEnvironment();
            plugin.getServer().unloadWorld(getWorldPath(bWorld.getUuid(), env), save);
            bWorld.setWorldPhases(LoadingPhase.UNLOADED, env);
        });
    }

    public void eraseDeletedWorlds() {
        for (String deletedWorld : deletedWorlds)
            FileUtils.deleteDirectory(new File(deletedWorld));
    }

}