package net.brutewars.sandbox.world.dimensions;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.lastlocation.BLocation;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.utils.Logging;
import net.brutewars.sandbox.utils.PersistentDataUtils;
import net.brutewars.sandbox.world.bonus.BonusChest;
import net.brutewars.sandbox.world.holograms.HologramManager;
import org.bukkit.*;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Wall;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public final class OverworldDimension extends Dimension {

    public OverworldDimension(BWorldPlugin plugin) {
        super(plugin);
    }

    @Override
    public @NotNull Location generate(Location spawnLoc) {
        spawnLoc = findSpawnLocation(spawnLoc.getWorld());

        // Generate beacon structure
        generateStructure(spawnLoc.add(3, 0 , 15));

        spawnLoc.add(0, 1, 0);
        spawnLoc.setYaw(180F); // NORTH

        return spawnLoc;
    }

    // 37 is the optimum quadrant length which has been determined by running tests
    private final int QUADRANT_LENGTH = 37;
    private final int MIDDLE = QUADRANT_LENGTH + 1;
    private Location findSpawnLocation(@NotNull World world) {
        final int SIZE = 2 * QUADRANT_LENGTH + 1;

        ThreadLocalRandom random = ThreadLocalRandom.current();
        int maxFlatBlocks = 0, numAttempts = 0;
        Location bestLocation = world.getSpawnLocation();

        while (numAttempts < plugin.getConfigSettings().getConfigParser().getSpawnLocationAttempts()) {
            Location[][] mapTerrainLocations = new Location[SIZE][SIZE];
            Location initialLoc = world.getHighestBlockAt(random.nextInt(-2000, 2000), random.nextInt(-2000, 2000)).getLocation();
            mapTerrainLocations[MIDDLE][MIDDLE] = initialLoc;

            generateHeightMap(initialLoc, mapTerrainLocations);

            // 18 blocks tall and 7 blocks wide so the starting block cannot be at the opposite corner
            for (int i = 0; i < SIZE - 7; i++) {
                for (int j = 0; j < SIZE - 18; j++) {
                    // i & x = row
                    // j & z = column
                    boolean perfect = true;
                    int numFlatBlocks = 0;
                    Location origin = mapTerrainLocations[i][j];

                    for (int x = 0; x < 7; x++) {
                        for (int z = 0; z < 18; z++) {
                            Location loc = mapTerrainLocations[i + x][j + z];
                            if (loc == null || origin.getBlockY() != loc.getBlockY() || !loc.getBlock().getType().isSolid()) {
                                perfect = false;
                                break;
                            } else numFlatBlocks++;
                        }

                        if (!perfect)
                            break;
                    }

                    if (perfect) {
                        Logging.debug(plugin, "Found perfect spawn location after " + (numAttempts + 1) + " attempts!");
                        return mapTerrainLocations[i][j];
                    }

                    if (numFlatBlocks > maxFlatBlocks) {
                        maxFlatBlocks = numFlatBlocks;
                        bestLocation = mapTerrainLocations[i][j];
                    }
                }
            }

            numAttempts++;
        }

        Logging.debug(plugin, "Spawn location used is " + maxFlatBlocks * 0.794 + "% perfect");
        return bestLocation;
    }

    private final BlockFace[] cardinalDirections = new BlockFace[] {
            BlockFace.NORTH,
            BlockFace.SOUTH,
            BlockFace.EAST,
            BlockFace.WEST
    };
    private void generateHeightMap(Location location, Location[][] mapTerrainLocations) {
        Location origin = mapTerrainLocations[MIDDLE][MIDDLE];
        int x = location.getBlockX() - origin.getBlockX();
        int z = location.getBlockZ() - origin.getBlockZ();
        mapTerrainLocations[x + QUADRANT_LENGTH][z + QUADRANT_LENGTH] = location;

        Block block = location.getBlock();
        for (BlockFace blockFace : cardinalDirections) {
            Location loc = block.getRelative(blockFace).getLocation();
            loc = origin.getWorld().getHighestBlockAt(loc).getLocation();
            int _x = loc.getBlockX() - origin.getBlockX();
            int _z = loc.getBlockZ() - origin.getBlockZ();

            // base cases
            if (Math.abs(_x) > QUADRANT_LENGTH || Math.abs(_z) > QUADRANT_LENGTH ||
                    mapTerrainLocations[_x + QUADRANT_LENGTH][_z + QUADRANT_LENGTH] != null)
                continue;

            // recursion
            generateHeightMap(loc, mapTerrainLocations);
        }
    }

    /**
     * @param spawnLoc the location to spawn the structure at, level with the floor
     */
    private void generateStructure(@NotNull Location spawnLoc) {
        Location origin = spawnLoc.clone().add(3, 0, 2);
        HologramManager holoManager = plugin.getHologramManager();
        Vector[] vectors;

        // BEACON BASE
        {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Location loc = spawnLoc.clone().add(x, 0, z);
                    loc.getBlock().setType(Material.IRON_BLOCK);

                    if (Math.abs(x) == Math.abs(z)) {
                        loc.add(0, 1, 0).getBlock().setType(Material.CHISELED_STONE_BRICKS);
                    } else {
                        setStair(spawnLoc, loc.add(0, 1, 0), Material.COBBLESTONE_STAIRS, Stairs.Shape.STRAIGHT, false);

                        loc.add(x, 0, z);
                        for (int i = -1; i <= 1; i++) {
                            if (x == 0)
                                setStair(spawnLoc.clone().add(i, 0, 0), loc.clone().add(i, 0, 0), Material.STONE_BRICK_STAIRS, Stairs.Shape.STRAIGHT, true);
                            else
                                setStair(spawnLoc.clone().add(0, 0, i), loc.clone().add(0, 0, i), Material.STONE_BRICK_STAIRS, Stairs.Shape.STRAIGHT, true);
                        }
                    }
                }
            }

            setStair(origin.clone().add(-1, 1, -1), origin.clone().add(-1, 1, 0), Material.STONE_BRICK_STAIRS, Stairs.Shape.OUTER_LEFT, true);
            setStair(origin.clone().add(-5, 1, -1), origin.clone().add(-5, 1, 0), Material.STONE_BRICK_STAIRS, Stairs.Shape.OUTER_RIGHT, true);
            setStair(origin.clone().add(-1, 1, -3), origin.clone().add(-1, 1, -4), Material.STONE_BRICK_STAIRS, Stairs.Shape.OUTER_RIGHT, true);
            setStair(origin.clone().add(-5, 1, -3), origin.clone().add(-5, 1, -4), Material.STONE_BRICK_STAIRS, Stairs.Shape.OUTER_LEFT, true);
        }

        // BEACON
        {
            spawnLoc.add(0.5, 1, 0.5).getBlock().setType(Material.BEACON);
            Beacon beacon = ((Beacon) spawnLoc.getBlock().getState());
            beacon.setPrimaryEffect(PotionEffectType.SPEED);
            beacon.update();
        }

        // HOLOGRAMS
        {
            World world = spawnLoc.getWorld();
            Location middle = spawnLoc.clone().subtract(0.5, 1.3, 0.5);
            spawnHolo(middle, Lang.NORTH, 0.5, 0, 0);
            spawnHolo(middle, Lang.SOUTH, 0, 0, 1);
            spawnHolo(middle, Lang.EAST, 0.5, 0, -0.5);
            spawnHolo(middle, Lang.WEST, -1, 0, 0);

            // store rainbow hologram key
            PersistentDataUtils.storeData(plugin, world, PersistentDataType.STRING, "hologram_welcome", "rainbow_hologram");
            spawnHolo(middle, Lang.WELCOME_HOLOGRAM_3, 0.5, 1.7, -2);
            spawnHolo(middle, Lang.WELCOME_HOLOGRAM_2, 0, 0.3, 0);

            PersistentDataUtils.storeData(plugin, world, PersistentDataType.STRING, "beacon", new BLocation(spawnLoc).toString());
        }

        // PATHWAY
        {
            for (int x = 2; x <= 4; x++)
                for (int z = 5; z <= 17; z++)
                    setBlockWithStoneUnderneath(origin.clone().subtract(x, 0, z), Material.GRAVEL);

            vectors = new Vector[] {
                    new Vector(-1, 0, -8),
                    new Vector(-1, 0, -13),
                    new Vector(-5, 0, -8),
                    new Vector(-5, 0, -13),
            };

            for (Vector vector : vectors) {
                Location loc = origin.clone().add(vector);
                for (int z = 0; z < 4; z++) {
                    setBlockWithStoneUnderneath(loc.clone(), Material.GRAVEL);
                    loc.subtract(0, 0, 1);
                }
            }
        }

        // BONUS CHEST
        {
            Location loc = origin.clone().add(-3, 1, -15);
            new BonusChest(plugin).spawn(loc);

            holoManager.spawnHologram(loc.clone().add(0.5, -0.8, -0.3), "bonus", Lang.BONUS_CHEST_HOLOGRAM);

            PersistentDataUtils.storeData(plugin, origin.getWorld(), PersistentDataType.STRING, "bonus", new BLocation(loc).toString());

            loc.clone().add(1, 0, 0).getBlock().setType(Material.TORCH);
            loc.clone().add(-1, 0, 0).getBlock().setType(Material.TORCH);
        }

        // STONE PILLARS
        {
            vectors = new Vector[] {
                    new Vector(0, 1, -8),
                    new Vector(0, 1, -11),
                    new Vector(-6, 1, -8),
                    new Vector(-6, 1, -11),
            };

            for (Vector vector : vectors) {
                Location loc = origin.clone().add(vector);
                for (int y = -2; y <= 1; y++)
                    loc.clone().add(0, y, 0).getBlock().setType(Material.STONE);
            }

            vectors = new Vector[] {
                    new Vector(0, 1, -7),
                    new Vector(-6, 1, -7),
                    new Vector(0, 1, -12),
                    new Vector(-6, 1, -12)
            };

            for (int i = 0; i < vectors.length; i++) {
                Location loc = origin.clone().add(vectors[i]);
                Block block = loc.getBlock();
                block.setType(Material.COBBLESTONE_WALL);

                Wall wall = ((Wall) block.getBlockData());
                if (i < 2)
                    wall.setHeight(BlockFace.NORTH, Wall.Height.LOW);
                else
                    wall.setHeight(BlockFace.SOUTH, Wall.Height.LOW);
                block.setBlockData(wall);

                loc.add(0, 1, 0).getBlock().setType(Material.TORCH);
            }

            vectors = new Vector[] {
                    new Vector(1, 0, -9),
                    new Vector(1, 0, -10)
            };

            for (Vector vector : vectors) {
                for (int y = -1; y < 2; y++) {
                    origin.clone().add(vector).add(0, y, 0).getBlock().setType(Material.STONE);
                    origin.clone().add(vector).add(-8, y, 0).getBlock().setType(Material.STONE);
                }
            }
        }

        // PORTALS
        {
            setBlockWithStoneUnderneath(origin.clone().add(0, 0, -9), Material.OBSIDIAN);
            setBlockWithStoneUnderneath(origin.clone().add(0, 0, -10), Material.OBSIDIAN);

            for (int i = 0; i < 2; i++) {
                Location loc = origin.clone().add(0, 1, -9 - i);
                setNetherPortal(loc);
                PersistentDataUtils.storeData(plugin, origin.getWorld(), PersistentDataType.STRING, "nether_portal" + i, new BLocation(loc).toString());
            }

            origin.clone().add(-6, 0, -9).getBlock().setType(Material.END_PORTAL_FRAME);
            origin.clone().add(-6, 0, -10).getBlock().setType(Material.END_PORTAL_FRAME);

            setBlockWithStoneUnderneath(origin.clone().add(-6, 0, -10), Material.END_PORTAL);
            setBlockWithStoneUnderneath(origin.clone().add(-6, 0, -9), Material.END_PORTAL);

            Location portalHolo = origin.clone().add(0.5, 0, -9);
            holoManager.spawnHologram(portalHolo, Lang.NETHER_DIMENSION_HOLOGRAM);
            holoManager.spawnHologram(portalHolo.add(-6, 0, 0), Lang.END_DIMENSION_HOLOGRAM);
        };
    }

    private void spawnHolo(Location loc, Lang text, double x, double y, double z) {
        plugin.getHologramManager().spawnHologram(loc.add(x, y, z), text);
    }

    public void setNetherPortal(@NotNull Location loc) {
        Block block = loc.getBlock();
        block.setType(Material.NETHER_PORTAL);
        if (!(block.getBlockData() instanceof Orientable orientable))
            return;

        orientable.setAxis(Axis.Z);
        block.setBlockData(orientable);
    }

    private void setBlockWithStoneUnderneath(@NotNull Location loc, Material material) {
        loc.getBlock().setType(material);
        loc.clone().subtract(0, 1, 0).getBlock().setType(Material.STONE);
    }

    private void setStair(@NotNull Location origin, @NotNull Location loc, Material material, Stairs.Shape shape, boolean setBlockUnderneath) {
        // in order to get the correct cardinal direction
        Location _origin = origin.clone();
        _origin.setY(loc.getY());

        Block block = loc.getBlock();
        if (setBlockUnderneath)
            setBlockWithStoneUnderneath(loc, material);
        else
            block.setType(material);

        if (block.getBlockData() instanceof Stairs stair) {
            stair.setShape(shape);
            stair.setFacing(getCardinalDirection(_origin, loc));
            block.setBlockData(stair);
        }
    }

    /**
     * @return cardinal directions. It returns {@link BlockFace#UP} if a cardinal direction
     * has not been found for some reason.
     */
    private BlockFace getCardinalDirection(@NotNull Location origin, Location location) {
        Vector vec = origin.clone().subtract(location).toVector().normalize();

        for (BlockFace blockFace : BlockFace.values()) {
            if (vec.equals(blockFace.getDirection()))
                return blockFace;
        }

        return BlockFace.UP;
    }

    public boolean isSpecialBlock(Block block, Material material, String key) {
        if (block.getType() != material)
            return false;

        World world = block.getWorld();
        String bLoc = PersistentDataUtils.getData(plugin, world, PersistentDataType.STRING, key, "");
        if (bLoc.isEmpty())
            return false;

        return block.equals(new BLocation(bLoc).toLoc(world).getBlock());
    }

    public void onBlockPhysics(BlockPhysicsEvent event) {
        // Nether portal block changed to air
        if (event.getChangedType() != Material.AIR)
            return;

        Location loc = event.getBlock().getLocation();
        World world = loc.getWorld();

        boolean found = false;
        Location[] portalLocations = new Location[2];
        for (int i = 0; i < 2; i++) {
            String bLoc = PersistentDataUtils.getData(plugin, world, PersistentDataType.STRING, "nether_portal" + i, "");
            if (bLoc.isEmpty())
                continue;

            Location bLocation = new BLocation(bLoc).toLoc(world);

            // If obsidian under has been removed, we don't do anything
            if (bLocation.clone().subtract(0, 1, 0).getBlock().getType() != Material.OBSIDIAN)
                return;

            portalLocations[i] = bLocation;
            if (loc.equals(bLocation))
                found = true;
        }

        if (found) {
            event.setCancelled(true);
            OverworldDimension generator = new OverworldDimension(plugin);
            for (Location portalLocation : portalLocations)
                generator.setNetherPortal(portalLocation);
        }
    }

}