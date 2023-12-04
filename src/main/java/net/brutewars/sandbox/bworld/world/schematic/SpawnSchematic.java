package net.brutewars.sandbox.bworld.world.schematic;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.world.bonus.BonusChest;
import net.brutewars.sandbox.config.parser.SchematicSettingsParser;
import net.brutewars.sandbox.dependencies.WorldEditDependency;
import net.brutewars.sandbox.thread.Executor;
import org.bukkit.Location;

import java.io.File;
import java.util.Map;

public final class SpawnSchematic {
    private final BWorldPlugin plugin;

    private final File schematicFile;
    public SpawnSchematic(BWorldPlugin plugin, File schematicFile) {
        this.plugin = plugin;
        this.schematicFile = schematicFile;
    }

    /**
     * @param location where the schematic should be pasted
     */
    public void pasteSchematic(Location location) {
        WorldEditDependency we = plugin.getWorldEdit();

        Clipboard clipboard = we.getClipboard(schematicFile);
        if (clipboard == null)
            return;

        BlockVector3 spawnPoint = clipboard.getOrigin();
        BlockVector3 min = clipboard.getMinimumPoint();

        BlockVector3 newOrigin = BlockVector3.at(min.getBlockX(), clipboard.getOrigin().getY(), min.getBlockZ());
        clipboard.setOrigin(newOrigin);

        replaceMaterials(clipboard, location);

        we.pasteClipboard(clipboard, location);

        BlockVector3 diff = spawnPoint.subtract(newOrigin);
        location.add(diff.getX(), diff.getY(), diff.getZ());
        location.getWorld().setSpawnLocation(location);
    }

    public void replaceMaterials(Clipboard clipboard, Location location) {
        SchematicSettingsParser parser = plugin.getConfigSettings().getSchematicParser();
        Map<String, String> blockMap = parser.getBlockMap();
        String bonus = parser.getBonusChest();

        for (BlockVector3 blockVector3 : clipboard.getRegion()) {
            String blockType = clipboard.getBlock(blockVector3).getBlockType().toString();

            if (blockMap.containsKey(blockType)) {
                BlockType replacement = BlockTypes.get(blockMap.get(blockType));
                if (replacement != null) {
                    try {
                        clipboard.setBlock(blockVector3, replacement.getDefaultState());
                    } catch (WorldEditException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (blockType.equals(bonus)) {
                BlockVector3 v = blockVector3.subtract(clipboard.getOrigin());
                Location l = location.clone();
                Executor.sync(plugin, bukkitRunnable -> new BonusChest(plugin).spawn(l.add(v.getX(), v.getY(), v.getZ())));
            }
        }
    }

}