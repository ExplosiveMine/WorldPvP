package net.brutewars.sandbox.bworld.world;

import lombok.Getter;
import lombok.Setter;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.world.dimensions.SandboxWorld;
import net.brutewars.sandbox.bworld.world.schematic.SchematicManager;
import net.brutewars.sandbox.bworld.world.schematic.SpawnSchematic;
import net.brutewars.sandbox.utils.FileUtils;
import net.brutewars.sandbox.utils.Logging;
import net.brutewars.sandbox.utils.Pair;
import net.brutewars.sandbox.bworld.world.dimensions.EndDimension;
import net.brutewars.sandbox.bworld.world.dimensions.NetherDimension;
import net.brutewars.sandbox.bworld.world.dimensions.OverworldDimension;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class WorldRoster {
    private final BWorldPlugin plugin;

    @Getter private final SchematicManager schematicManager;

    private final Map<Pair<WorldType, World.Environment>, List<SandboxWorld>> worlds = new HashMap<>();

    private int rosterSize;
    @Getter @Setter private long lastRoster;

    public WorldRoster(BWorldPlugin plugin) {
        this.plugin = plugin;
        this.schematicManager = new SchematicManager(plugin);
    }

    public void setupRoster() {
        File rosterFolder = new File(plugin.getDataFolder(), "world-roster");

        long currentTime = System.currentTimeMillis();
        if (currentTime >= lastRoster + plugin.getConfigSettings().getConfigParser().getWorldRenewTime().toMillis())
            FileUtils.deleteDirectory(rosterFolder);

        if (!rosterFolder.exists()) {
            rosterFolder.mkdirs();
            this.lastRoster = currentTime;
        }

        this.rosterSize = plugin.getConfigSettings().getConfigParser().getRosterSize();
        for (int i = 0; i < rosterSize; i++) {
            preGenWorld(i, WorldType.FLAT, World.Environment.NORMAL);
            preGenWorld(i, WorldType.NORMAL, World.Environment.NORMAL);
            preGenWorld(i, WorldType.AMPLIFIED, World.Environment.NORMAL);

            preGenWorld(i, WorldType.NORMAL, World.Environment.NETHER);
            preGenWorld(i, WorldType.NORMAL, World.Environment.THE_END);
        }

        Logging.debug(plugin, "World roster generated in: " + (System.currentTimeMillis() - currentTime)/1000 + "s");
    }

    private void preGenWorld(int i, WorldType worldType, World.Environment env) {
        String fileName = plugin.getDataFolder() + File.separator + "world-roster" + File.separator;
        if (env == World.Environment.NORMAL)
            fileName = fileName + worldType;
        else
            fileName = fileName + env;
        fileName = fileName + i;

        ImportOptions options = new ImportOptions()
                .setGenerateCustomSpawn(false)
                .setWorldType(worldType)
                .setEnvironment(env)
                .setName(fileName);

        SandboxWorld sandboxWorld = createSandboxWorld(env, options);
        if (sandboxWorld == null)
            return;

        Pair<WorldType, World.Environment> pair = Pair.of(worldType, env);
        List<SandboxWorld> worlds = this.worlds.getOrDefault(pair, new ArrayList<>());
        worlds.add(sandboxWorld);
        this.worlds.put(pair, worlds);

        File worldFile = new File(fileName);
        if (worldFile.exists())
            return;

        World world = sandboxWorld.create(true);
        sandboxWorld.findSpawnLocation();
        plugin.getServer().unloadWorld(world, true);
    }

    /**
     * @return a SandboxWorld which fits the worldType and environment with the specified worldName.
     * The world files are imported if necessary
     */
    public SandboxWorld getSandboxWorld(World.Environment environment, ImportOptions importOptions) {
        String worldName = importOptions.getName();
        File worldFile = new File(worldName);

        if (!worldFile.exists()) {
            Logging.debug(plugin, "Importing world file " + worldName);
            SandboxWorld templateWorld = worlds.get(importOptions.getPair()).get(ThreadLocalRandom.current().nextInt(rosterSize));
            FileUtils.copyDirectory(templateWorld.getWorldFile(), worldFile, s -> !s.equals("uid.dat"));
        }

        return createSandboxWorld(environment, importOptions);
    }

    private @Nullable SandboxWorld createSandboxWorld(World.Environment environment, ImportOptions importOptions) {
        SandboxWorld sandboxWorld = null;
        switch (environment) {
            case NORMAL -> {
                SpawnSchematic schematic = importOptions.isGenerateSpawn() ? schematicManager.getRandomSchematic() : null;
                sandboxWorld = new OverworldDimension(plugin, importOptions.getWorldType(), importOptions.getName(), schematic);
            }
            case NETHER -> sandboxWorld = new NetherDimension(plugin, importOptions.getName());
            case THE_END -> sandboxWorld = new EndDimension(plugin, importOptions.getName());
        }
        return sandboxWorld;
    }

}