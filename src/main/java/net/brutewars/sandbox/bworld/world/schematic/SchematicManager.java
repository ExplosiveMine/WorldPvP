package net.brutewars.sandbox.bworld.world.schematic;

import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import net.brutewars.sandbox.BWorldPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class SchematicManager {
    private final BWorldPlugin plugin;

    private final List<SpawnSchematic> spawnSchematics = new ArrayList<>();

    private boolean setup = false;

    public SchematicManager(BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadSchematics() {
        File[] files = new File(plugin.getDataFolder(), "schematic").listFiles();

        if (files == null)
            return;

        for (File file : files) {
            ClipboardFormat format = ClipboardFormats.findByFile(file);
            if (format == null)
                continue;

            SpawnSchematic spawnSchematic = new SpawnSchematic(plugin, file);
            spawnSchematics.add(spawnSchematic);
        }
    }

    public @Nullable SpawnSchematic getRandomSchematic() {
        if (!setup) {
            loadSchematics();
            setup = true;
        }

        if (spawnSchematics.size() == 0)
            return null;

        return spawnSchematics.get(ThreadLocalRandom.current().nextInt(0, spawnSchematics.size()));
    }

}