package net.brutewars.sandbox.bworld.world.dimensions;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.world.location.BLocation;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.utils.Logging;
import net.brutewars.sandbox.utils.PersistentDataUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.persistence.PersistentDataType;

public final class EndDimension extends SandboxWorld {
    public EndDimension(BWorldPlugin plugin, String name) {
        super(plugin, World.Environment.THE_END, WorldType.NORMAL, name);
    }

    @Override
    public boolean createSpawn() {
        Location spawnLoc = getWorld().getSpawnLocation();
        //todo remove
        Logging.debug(getPlugin(), spawnLoc.toString());
        findSpawnLocation();
        Logging.debug(getPlugin(), getWorld().getSpawnLocation().toString());

        // obsidian platform
//        for (int x = -2; x <= 2; x++) {
//            for (int z = -2; z <= 2; z++) {
//                Location loc = spawnLoc.clone().add(x, -1, z);
//                Logging.debug(getPlugin(), " placing obby");
//                loc.getBlock().setType(Material.OBSIDIAN);
//                for (int y = 1; y <= 3; y++)
//                    loc.clone().add(0, y, 0).getBlock().setType(Material.AIR);
//            }
//        }

        Location portalCentre = spawnLoc.clone().add(2, 0, 0);
        setWaterPortal(portalCentre.clone(), 0);
        setWaterPortal(portalCentre.clone().add(0, 1, 0), 1);

        for (int z = -1; z <= 1; z++) {
            for (int y = 0; y < 3; y++) {
                // don't fill in the water area
                if (y < 2 && z == 0)
                    continue;

                portalCentre.clone().add(0, y, z).getBlock().setType(Material.OBSIDIAN);
            }
        }

        // hologram
        getPlugin().getHologramManager().spawnHologram(spawnLoc.clone().add(2, 1, 0), "end", Lang.TELEPORT_OVERWORLD_HOLO);
        return true;
    }

    @Override
    public void findSpawnLocation() {
        World world = getWorld();
        world.setSpawnLocation(new Location(world, 100.5, 50, 0.5, 90, 0));
    }

    private void setWaterPortal(Location loc, int i) {
        Block block = loc.getBlock();
        block.setType(Material.WATER);
        Levelled blockData = ((Levelled) block.getBlockData());
        blockData.setLevel(0);
        block.setBlockData(blockData);

        PersistentDataUtils.storeData(getPlugin(),
                loc.getWorld(),
                PersistentDataType.STRING, "water_portal" + i,
                new BLocation(loc).toString()
        );
    }

    public void onBlockFromTo(BlockFromToEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.WATER)
            return;

        for (int i = 0; i < 2; i++) {
            String bLoc = PersistentDataUtils.getData(
                    getPlugin(),
                    event.getBlock().getWorld(),
                    PersistentDataType.STRING,
                    "water_portal" + i,
                    ""
            );

            if (bLoc.isEmpty())
                continue;

            if (block.equals(new BLocation(bLoc).toLoc(block.getWorld()).getBlock()))
                event.setCancelled(true);
        }
    }

}