package net.brutewars.sandbox.world;

import lombok.SneakyThrows;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.lastlocation.BLocation;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.utils.FileUtils;
import net.brutewars.sandbox.utils.Logging;
import net.brutewars.sandbox.utils.PersistentDataUtils;
import net.brutewars.sandbox.world.bonus.BonusChest;
import net.brutewars.sandbox.world.holograms.HologramManager;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;


public final class WorldFactory {
    private final BWorldPlugin plugin;

    public WorldFactory(BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    public World create(String worldName, WorldType worldType) {
        WorldCreator wc = new WorldCreator(worldName);
        if (worldType != null) {
            Logging.debug(plugin, "Generating new world: " + worldName);
            wc.type(worldType);
        } else {
            wc.keepSpawnLoaded(TriState.FALSE);
        }

        return plugin.getServer().createWorld(wc);
    }

    public void setWorldBorder(World world, WorldSize worldSize) {
        world.getWorldBorder().setSize(worldSize.getValue());
    }

    @SneakyThrows
    public void importWorld(File rosterFile, String worldName) {
        File worldFile = new File(worldName);

        if (!worldFile.exists()) {
            Logging.debug(plugin, "Importing world file " +worldName);
            FileUtils.copyDirectory(rosterFile, worldFile, s -> !s.equals("uid.dat"));
        }
    }

    public World loadWorld(String worldName) {
        return create(worldName, null);
    }

    public void setupWorld(World world) {
        Location spawnLoc = findSpawnLoc(world);

        // Generate beacon structure
        generateStructure(spawnLoc);

        spawnLoc.add(0, 1, 0);
        spawnLoc.setYaw(180F); // NORTH
        world.setSpawnLocation(spawnLoc);

        world.getWorldBorder().setCenter(spawnLoc);
    }

    private Location findSpawnLoc(World world) {
        Location finalLoc = world.getSpawnLocation();

        int minFlatness = world.getMaxHeight() - world.getMinHeight();
        boolean found = false;
        Location loc = finalLoc;
        while (!found) {
            // Some base conditions to improve efficiency, if conditions are not met, this does not count as an attempt.
            Material mat = loc.getBlock().getType();
            if (!mat.isSolid() || mat.toString().contains("LEAVES")) {
                loc = generateRandomLoc(world);
                continue;
            }

            // Analyse terrain to see how flat it is
            int highestY = loc.getBlockY();
            int lowestY = loc.getBlockY();
            for (int x = 0; x > -7; x--) {
                for (int z = 0; z > -18; z--) {
                    if (x == 0 && z == 0)
                        continue;

                    int y = world.getHighestBlockAt(loc.clone().add(x, 0, z)).getLocation().getBlockY();
                    if (y > highestY)
                        highestY = y;

                    if (y < lowestY)
                        lowestY = y;
                }
            }

            int flatness = highestY - lowestY;
            if (flatness < minFlatness) {
                minFlatness = flatness;
                finalLoc = loc;
            }

            if (flatness == 0 || flatness == 1)
                found = true;
            else
                loc = generateRandomLoc(world);
        }

        return finalLoc;
    }

    private Location generateRandomLoc(World world) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return world.getHighestBlockAt(random.nextInt(-2000, 2000), random.nextInt(-2000, 2000)).getLocation();
    }

    /**
     * @param spawnLoc the location to spawn the structure at, level with the floor
     */
    private void generateStructure(Location spawnLoc) {
        // generate beacon base
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++)
                spawnLoc.clone().add(i, 0, j).getBlock().setType(Material.IRON_BLOCK);

        // generate speed boosts
        generateSpeedBoost(spawnLoc,
                new Vector(1, 0, 0),
                new Vector(-1, 0, 0),
                new Vector(0, 0, 1),
                new Vector(0, 0, -1)
        );

        // Bonus chest
        Location loc = spawnLoc.clone().subtract(0, 1, 0);
        new BonusChest(plugin).spawn(loc);

        // Mid Block
        Block block = loc.subtract(0, 1, 0).getBlock();
        if (!block.getType().isSolid())
            block.setType(Material.DIRT);

        // Torches
        setTorch(block,
                BlockFace.NORTH,
                BlockFace.SOUTH,
                BlockFace.EAST,
                BlockFace.WEST
        );

        World world = spawnLoc.getWorld();
        // Spawn beacon
        spawnLoc.add(0.5, 1, 0.5).getBlock().setType(Material.BEACON);
        PersistentDataUtils.storeData(plugin,world, PersistentDataType.STRING, "beacon", new BLocation(spawnLoc).toString());

        // Spawn holograms
        HologramManager holoManager = plugin.getHologramManager();

        Location middle = spawnLoc.clone().subtract(0.5, 1.7, 0.5);

        holoManager.spawnHologram(middle.add(0.5, 0, 0), Lang.NORTH.get());
        holoManager.spawnHologram(middle.add(0, 0, 1), Lang.SOUTH.get());
        holoManager.spawnHologram(middle.add(0.5, 0, -0.5), Lang.EAST.get());
        holoManager.spawnHologram(middle.subtract(1, 0, 0), Lang.WEST.get());
    }

    private void setTorch(Block block, BlockFace...faces) {
        for (BlockFace face : faces) {
            Block torch = block.getRelative(face);
            torch.setType(Material.WALL_TORCH);

            if (torch.getBlockData() instanceof Directional data) {
                data.setFacing(face);
                torch.setBlockData(data);
            }
        }
    }

    private void generateSpeedBoost(Location spawnLoc, Vector...vectors) {
        StringBuilder locations = new StringBuilder();
        for (Vector vec : vectors) {
            Location loc = spawnLoc.clone().add(vec);
            // 3 blocks long speed boost
            for (int j = 0; j < 3; j++) {
                loc.add(vec);
                loc.getBlock().setType(Material.ORANGE_GLAZED_TERRACOTTA);
                locations.append(new BLocation(loc)).append(";");
            }
        }

        PersistentDataUtils.storeData(plugin, spawnLoc.getWorld(), PersistentDataType.STRING, "speed_boost", locations.toString());
    }

    public boolean isSpeedBoost(Block block) {
        if (!block.getType().equals(Material.ORANGE_GLAZED_TERRACOTTA))
            return false;

        World world = block.getWorld();
        String[] locs = PersistentDataUtils.getData(plugin, world, PersistentDataType.STRING, "speed_boost", "0:-100:0").split(";");
        for (String loc : locs) {
            if (block.equals(new BLocation(loc).toLoc(world).getBlock()))
                return true;
        }

        return false;
    }

    public boolean isBeacon(Block block) {
        if (!block.getType().equals(Material.BEACON))
            return false;

        World world = block.getWorld();
        Location loc = new BLocation(PersistentDataUtils.getData(plugin, world, PersistentDataType.STRING, "beacon", "0:-100:0")).toLoc(world);
        return block.equals(loc.getBlock());
    }

    public void removeSpeedBoost(World world) {
        PersistentDataUtils.remove(plugin, world, "speed_boost");
    }

}