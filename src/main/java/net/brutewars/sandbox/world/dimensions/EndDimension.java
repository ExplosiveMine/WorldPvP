package net.brutewars.sandbox.world.dimensions;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.lastlocation.BLocation;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.utils.PersistentDataUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.persistence.PersistentDataType;

public final class EndDimension extends Dimension {
    public EndDimension(BWorldPlugin plugin) {
        super(plugin);
    }

    @Override
    public Location generate(Location spawnLoc) {
        spawnLoc = spawnLoc.set(100.5, 50, 0.5);

        // obsidian platform
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                Location loc = spawnLoc.clone().add(x, -1, z);
                loc.getBlock().setType(Material.OBSIDIAN);
                for (int y = 1; y <= 3; y++)
                    loc.clone().add(0, y, 0).getBlock().setType(Material.AIR);
            }
        }

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
        plugin.getHologramManager().spawnHologram(spawnLoc.clone().add(2, 1, 0), "end", Lang.TELEPORT_OVERWORLD_HOLO);

        spawnLoc.setYaw(90.0F);
        return spawnLoc;
    }

    private void setWaterPortal(Location loc, int i) {
        Block block = loc.getBlock();
        block.setType(Material.WATER);
        Levelled blockData = ((Levelled) block.getBlockData());
        blockData.setLevel(0);
        block.setBlockData(blockData);

        PersistentDataUtils.storeData(plugin,
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
                    plugin,
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