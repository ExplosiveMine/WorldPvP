package net.brutewars.sandbox.bworld.world.dimensions;

import lombok.Getter;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.world.location.BLocation;
import net.brutewars.sandbox.bworld.world.location.LastLocationTracker;
import net.brutewars.sandbox.bworld.world.schematic.SpawnSchematic;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.config.parser.SchematicSettingsParser;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.utils.Logging;
import net.brutewars.sandbox.utils.PersistentDataUtils;
import net.brutewars.sandbox.holograms.RainbowHologram;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Orientable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class OverworldDimension extends SandboxWorld implements LastLocationTracker {
    private final Map<BPlayer, BLocation> lastLocations = new HashMap<>();
    @Getter private BLocation defaultLocation;

    private final SpawnSchematic spawnSchematic;

    public OverworldDimension(BWorldPlugin plugin, WorldType worldType, String name, SpawnSchematic spawnSchematic) {
        super(plugin, World.Environment.NORMAL, worldType, name);
        this.spawnSchematic = spawnSchematic;
    }

    @Override
    public void teleportToWorld(BPlayer bPlayer) {
        BLocation lastLoc = lastLocations.put(bPlayer, defaultLocation);
        if (lastLoc == null)
            return;

        Location toTeleport = lastLoc.toLoc(getWorld());
        // in creative if the player is being teleported to a nether portal they will get teleported
        // to the nether
        if (bPlayer.getIfOnline(Player::getGameMode) == GameMode.CREATIVE)
            toTeleport = defaultLocation.toLoc(getWorld());

        Location finalToTeleport = toTeleport;
        bPlayer.runIfOnline(player -> player.teleportAsync(finalToTeleport, PlayerTeleportEvent.TeleportCause.PLUGIN));
    }

    @Override
    public void onWorldLoad(World world) {
        super.onWorldLoad(world);

        Location spawnLoc = world.getSpawnLocation();
        setDefaultLocation(new BLocation(spawnLoc.clone().add(0.5, 0, 0.5)));

        Location loc = spawnLoc.clone().add(0, 0, -2);
        String data = PersistentDataUtils.getData(getPlugin(), world, PersistentDataType.STRING, "hologram_welcome", "");
        if (!data.isEmpty())
            getPlugin().getHologramManager().addDynamicHologram(new RainbowHologram(loc, Lang.WELCOME_HOLOGRAM_1.get()), "welcome");
    }

    @Override
    public void updateLastLocation(BPlayer bPlayer, Location location) {
        updateLastLocation(bPlayer, new BLocation(location));
    }

    @Override
    public void updateLastLocation(BPlayer bPlayer, BLocation bLocation) {
        lastLocations.put(bPlayer, bLocation);
    }

    @Override
    public BLocation getLastLocation(BPlayer bPlayer) {
        lastLocations.computeIfAbsent(bPlayer, k -> defaultLocation);
        return lastLocations.get(bPlayer);
    }

    @Override
    public void startTracking(BPlayer bPlayer) {
        lastLocations.put(bPlayer, defaultLocation);
    }

    @Override
    public void stopTracking(BPlayer bPlayer) {
        lastLocations.remove(bPlayer);
    }

    private void setDefaultLocation(BLocation defaultLocation) {
        this.defaultLocation = defaultLocation;
        lastLocations.replaceAll((bPlayer, lastLocation) -> lastLocation == null ? defaultLocation : lastLocation);
    }

    /**
     * @return whether a schematic for the spawn was generated so that it can be marked as complete
     */
    @Override
    public boolean createSpawn() {
        Location spawnLoc = getWorld().getSpawnLocation();
        spawnLoc.setYaw(180F); // NORTH

        // Generate beacon structure
        if (spawnSchematic != null)
            generateStructure(spawnLoc);

        return spawnSchematic != null;
    }

    // 37 is the optimum quadrant length as determined through a series of tests
    private final int QUADRANT_LENGTH = 37;
    private final int MIDDLE = QUADRANT_LENGTH + 1;

    @Override
    public void findSpawnLocation() {
        final int SIZE = 2 * QUADRANT_LENGTH + 1, SPAWN_BOUNDARY = 2000;
        World world = getWorld();

        ThreadLocalRandom random = ThreadLocalRandom.current();
        int maxFlatBlocks = 0, numAttempts = 0;
        Location bestLocation = world.getSpawnLocation();

        //spawn is lengthX blocks and lengthZ blocks so the starting block cannot be at corners
        SchematicSettingsParser parser = getPlugin().getConfigSettings().getSchematicParser();
        int lengthX = parser.getMaxLengthX(), lengthZ = parser.getMaxLengthZ();

        while (numAttempts < getPlugin().getConfigSettings().getConfigParser().getSpawnLocationAttempts()) {
            Location[][] mapTerrainLocations = new Location[SIZE][SIZE];
            Location initialLoc = world.getHighestBlockAt(random.nextInt(-SPAWN_BOUNDARY, SPAWN_BOUNDARY), random.nextInt(-2000, 2000)).getLocation();
            mapTerrainLocations[MIDDLE][MIDDLE] = initialLoc;

            generateHeightMap(initialLoc, mapTerrainLocations);

            for (int i = lengthX; i < SIZE - lengthX; i++) {
                for (int j = lengthZ; j < SIZE - lengthZ; j++) {
                    // i & x = row
                    // j & z = column
                    boolean perfect = true;
                    int numFlatBlocks = 0;
                    Location origin = mapTerrainLocations[i][j];

                    for (int x = 0; x < lengthX; x++) {
                        for (int z = 0; z < lengthZ; z++) {
                            Location loc = mapTerrainLocations[i + x][j + z];

                            if (loc == null || origin.getBlockY() != loc.getBlockY() || !loc.getBlock().getType().isSolid())
                                perfect = false;
                            else
                                numFlatBlocks++;
                        }
                    }

                    if (perfect) {
                        Logging.debug(getPlugin(), "Found perfect spawn location in " + (numAttempts + 1) + " attempts!");
                        mapTerrainLocations[i][j].add(0, 1,0);
                        setSpawnLocation(mapTerrainLocations[i][j]);
                        return;
                    }

                    if (numFlatBlocks > maxFlatBlocks) {
                        maxFlatBlocks = numFlatBlocks;
                        bestLocation = mapTerrainLocations[i][j];
                    }
                }
            }

            numAttempts++;
        }

        Logging.debug(getPlugin(), "Spawn location used is " + maxFlatBlocks  * 100 / (lengthX * lengthZ)  + "% perfect");
        bestLocation.add(0, 1, 0);
        setSpawnLocation(bestLocation);
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

            // base case
            if (Math.abs(_x) > QUADRANT_LENGTH || Math.abs(_z) > QUADRANT_LENGTH ||
                    mapTerrainLocations[_x + QUADRANT_LENGTH][_z + QUADRANT_LENGTH] != null)
                continue;

            // general case
            generateHeightMap(loc, mapTerrainLocations);
        }
    }

    /**
     * @param spawnLoc the location to spawn the structure at, level with the floor
     * @return the location to spawn the player which may have been modified
     */
    private void generateStructure(@NotNull Location spawnLoc) {
        spawnSchematic.pasteSchematic(spawnLoc);

        // structure hard coded generation
//        HologramManager holoManager = getPlugin().getHologramManager();
//        Vector[] vectors;
//        // BEACON BASE
//        {
//            for (int x = -1; x <= 1; x++) {
//                for (int z = -1; z <= 1; z++) {
//                    Location loc = spawnLoc.clone().add(x, 0, z);
//                    loc.getBlock().setType(Material.IRON_BLOCK);
//
//                    if (Math.abs(x) == Math.abs(z)) {
//                        loc.add(0, 1, 0).getBlock().setType(Material.CHISELED_STONE_BRICKS);
//                    } else {
//                        setStair(spawnLoc, loc.add(0, 1, 0), Material.COBBLESTONE_STAIRS, Stairs.Shape.STRAIGHT, false);
//
//                        loc.add(x, 0, z);
//                        for (int i = -1; i <= 1; i++) {
//                            if (x == 0)
//                                setStair(spawnLoc.clone().add(i, 0, 0), loc.clone().add(i, 0, 0), Material.STONE_BRICK_STAIRS, Stairs.Shape.STRAIGHT, true);
//                            else
//                                setStair(spawnLoc.clone().add(0, 0, i), loc.clone().add(0, 0, i), Material.STONE_BRICK_STAIRS, Stairs.Shape.STRAIGHT, true);
//                        }
//                    }
//                }
//            }
//
//            setStair(origin.clone().add(-1, 1, -1), origin.clone().add(-1, 1, 0), Material.STONE_BRICK_STAIRS, Stairs.Shape.OUTER_LEFT, true);
//            setStair(origin.clone().add(-5, 1, -1), origin.clone().add(-5, 1, 0), Material.STONE_BRICK_STAIRS, Stairs.Shape.OUTER_RIGHT, true);
//            setStair(origin.clone().add(-1, 1, -3), origin.clone().add(-1, 1, -4), Material.STONE_BRICK_STAIRS, Stairs.Shape.OUTER_RIGHT, true);
//            setStair(origin.clone().add(-5, 1, -3), origin.clone().add(-5, 1, -4), Material.STONE_BRICK_STAIRS, Stairs.Shape.OUTER_LEFT, true);
//        }
//
//        // BEACON
//        {
//            spawnLoc.add(0.5, 1, 0.5).getBlock().setType(Material.BEACON);
//            Beacon beacon = ((Beacon) spawnLoc.getBlock().getState());
//            beacon.setPrimaryEffect(PotionEffectType.SPEED);
//            beacon.update();
//        }
//
//        // HOLOGRAMS
//        {
//            World world = spawnLoc.getWorld();
//            Location middle = spawnLoc.clone().subtract(0.5, 1.3, 0.5);
//            spawnHolo(middle, Lang.NORTH, 0.5, 0, 0);
//            spawnHolo(middle, Lang.SOUTH, 0, 0, 1);
//            spawnHolo(middle, Lang.EAST, 0.5, 0, -0.5);
//            spawnHolo(middle, Lang.WEST, -1, 0, 0);
//
//            // store rainbow hologram key
//            PersistentDataUtils.storeData(getPlugin(), world, PersistentDataType.STRING, "hologram_welcome", "rainbow_hologram");
//            spawnHolo(middle, Lang.WELCOME_HOLOGRAM_3, 0.5, 1.7, -2);
//            spawnHolo(middle, Lang.WELCOME_HOLOGRAM_2, 0, 0.3, 0);
//
//            PersistentDataUtils.storeData(getPlugin(), world, PersistentDataType.STRING, "beacon", new BLocation(spawnLoc).toString());
//        }
//
//        // PATHWAY
//        {
//            for (int x = 2; x <= 4; x++)
//                for (int z = 5; z <= 17; z++)
//                    setBlockWithStoneUnderneath(origin.clone().subtract(x, 0, z), Material.GRAVEL);
//
//            vectors = new Vector[] {
//                    new Vector(-1, 0, -8),
//                    new Vector(-1, 0, -13),
//                    new Vector(-5, 0, -8),
//                    new Vector(-5, 0, -13),
//            };
//
//            for (Vector vector : vectors) {
//                Location loc = origin.clone().add(vector);
//                for (int z = 0; z < 4; z++) {
//                    setBlockWithStoneUnderneath(loc.clone(), Material.GRAVEL);
//                    loc.subtract(0, 0, 1);
//                }
//            }
//        }
//
//        // BONUS CHEST
//        {
//            Location loc = origin.clone().add(-3, 1, -15);
//            new BonusChest(getPlugin()).spawn(loc);
//
//            holoManager.spawnHologram(loc.clone().add(0.5, -0.8, -0.3), "bonus", Lang.BONUS_CHEST_HOLOGRAM);
//
//            PersistentDataUtils.storeData(getPlugin(), origin.getWorld(), PersistentDataType.STRING, "bonus", new BLocation(loc).toString());
//
//            loc.clone().add(1, 0, 0).getBlock().setType(Material.TORCH);
//            loc.clone().add(-1, 0, 0).getBlock().setType(Material.TORCH);
//        }
//
//        // STONE PILLARS
//        {
//            vectors = new Vector[] {
//                    new Vector(0, 1, -8),
//                    new Vector(0, 1, -11),
//                    new Vector(-6, 1, -8),
//                    new Vector(-6, 1, -11),
//            };
//
//            for (Vector vector : vectors) {
//                Location loc = origin.clone().add(vector);
//                for (int y = -2; y <= 1; y++)
//                    loc.clone().add(0, y, 0).getBlock().setType(Material.STONE);
//            }
//
//            vectors = new Vector[] {
//                    new Vector(0, 1, -7),
//                    new Vector(-6, 1, -7),
//                    new Vector(0, 1, -12),
//                    new Vector(-6, 1, -12)
//            };
//
//            for (int i = 0; i < vectors.length; i++) {
//                Location loc = origin.clone().add(vectors[i]);
//                Block block = loc.getBlock();
//                block.setType(Material.COBBLESTONE_WALL);
//
//                Wall wall = ((Wall) block.getBlockData());
//                if (i < 2)
//                    wall.setHeight(BlockFace.NORTH, Wall.Height.LOW);
//                else
//                    wall.setHeight(BlockFace.SOUTH, Wall.Height.LOW);
//                block.setBlockData(wall);
//
//                loc.add(0, 1, 0).getBlock().setType(Material.TORCH);
//            }
//
//            vectors = new Vector[] {
//                    new Vector(1, 0, -9),
//                    new Vector(1, 0, -10)
//            };
//
//            for (Vector vector : vectors) {
//                for (int y = -1; y < 2; y++) {
//                    origin.clone().add(vector).add(0, y, 0).getBlock().setType(Material.STONE);
//                    origin.clone().add(vector).add(-8, y, 0).getBlock().setType(Material.STONE);
//                }
//            }
//        }
//
//        // PORTALS
//        {
//            setBlockWithStoneUnderneath(origin.clone().add(0, 0, -9), Material.OBSIDIAN);
//            setBlockWithStoneUnderneath(origin.clone().add(0, 0, -10), Material.OBSIDIAN);
//
//            for (int i = 0; i < 2; i++) {
//                Location loc = origin.clone().add(0, 1, -9 - i);
//                setNetherPortal(loc);
//                PersistentDataUtils.storeData(getPlugin(), origin.getWorld(), PersistentDataType.STRING, "nether_portal" + i, new BLocation(loc).toString());
//            }
//
//            origin.clone().add(-6, 0, -9).getBlock().setType(Material.END_PORTAL_FRAME);
//            origin.clone().add(-6, 0, -10).getBlock().setType(Material.END_PORTAL_FRAME);
//
//            setBlockWithStoneUnderneath(origin.clone().add(-6, 0, -10), Material.END_PORTAL);
//            setBlockWithStoneUnderneath(origin.clone().add(-6, 0, -9), Material.END_PORTAL);
//
//            Location portalHolo = origin.clone().add(0.5, 0, -9);
//            holoManager.spawnHologram(portalHolo, Lang.NETHER_DIMENSION_HOLOGRAM);
//            holoManager.spawnHologram(portalHolo.add(-6, 0, 0), Lang.END_DIMENSION_HOLOGRAM);
//        };
    }

//    private void spawnHolo(Location loc, Lang text, double x, double y, double z) {
//        getPlugin().getHologramManager().spawnHologram(loc.add(x, y, z), text);
//    }

    public void setNetherPortal(@NotNull Location loc) {
        Block block = loc.getBlock();
        block.setType(Material.NETHER_PORTAL);
        if (!(block.getBlockData() instanceof Orientable orientable))
            return;

        orientable.setAxis(Axis.Z);
        block.setBlockData(orientable);
    }

//    private void setBlockWithStoneUnderneath(@NotNull Location loc, Material material) {
//        loc.getBlock().setType(material);
//        loc.clone().subtract(0, 1, 0).getBlock().setType(Material.STONE);
//    }

//    private void setStair(@NotNull Location origin, @NotNull Location loc, Material material, Stairs.Shape shape, boolean setBlockUnderneath) {
//        // in order to get the correct cardinal direction
//        Location _origin = origin.clone();
//        _origin.setY(loc.getY());
//
//        Block block = loc.getBlock();
//        if (setBlockUnderneath)
//            setBlockWithStoneUnderneath(loc, material);
//        else
//            block.setType(material);
//
//        if (block.getBlockData() instanceof Stairs stair) {
//            stair.setShape(shape);
//            stair.setFacing(getCardinalDirection(_origin, loc));
//            block.setBlockData(stair);
//        }
//    }

    /**
     * @return cardinal directions. It returns {@link BlockFace#UP} if a cardinal direction
     * has not been found for some reason.
     */
//    private BlockFace getCardinalDirection(@NotNull Location origin, Location location) {
//        Vector vec = origin.clone().subtract(location).toVector().normalize();
//
//        for (BlockFace blockFace : BlockFace.values()) {
//            if (vec.equals(blockFace.getDirection()))
//                return blockFace;
//        }
//
//        return BlockFace.UP;
//    }

    public boolean isSpecialBlock(Block block, Material material, String key) {
        if (block.getType() != material)
            return false;

        World world = block.getWorld();
        String bLoc = PersistentDataUtils.getData(getPlugin(), world, PersistentDataType.STRING, key, "");
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
            String bLoc = PersistentDataUtils.getData(getPlugin(), world, PersistentDataType.STRING, "nether_portal" + i, "");
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
            for (Location portalLocation : portalLocations)
                setNetherPortal(portalLocation);
        }
    }

}