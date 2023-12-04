package net.brutewars.sandbox.bworld.world.size;

import net.brutewars.sandbox.BWorldPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class WorldSizes {
    private final static Map<String, BorderSize> worldSizes = new HashMap<>();

    public static void reload(BWorldPlugin plugin) {
        worldSizes.clear();
        plugin.getConfigSettings().getWorldSizeParser().getWorldSizes(true).forEach(WorldSizes::put);
    }

    public static BorderSize getDefaultSize() {
        return worldSizes.getOrDefault("default", new BorderSize("default", 1000));
    }

    public static Collection<BorderSize> getValues() {
        return worldSizes.values();
    }

    private static void put(String identifier, int size) {
        worldSizes.put(identifier, new BorderSize(identifier, size));
    }

}