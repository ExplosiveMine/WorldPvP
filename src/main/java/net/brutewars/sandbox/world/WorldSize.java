package net.brutewars.sandbox.world;

import lombok.Getter;
import net.brutewars.sandbox.BWorldPlugin;

import java.util.HashMap;
import java.util.Map;

public enum WorldSize {
    DEFAULT(),
    TYRANT(),
    NOMAD(),
    BRUTE();

    @Getter private final String permission;
    private final static Map<WorldSize, Integer> sizes = new HashMap<>();

    WorldSize() {
        this.permission = "world." + name().toLowerCase();
    }

    public static void reload(final BWorldPlugin plugin) {
        for (WorldSize size : values())
            sizes.put(size, plugin.getConfig().getInt("world.size." + size.name().toLowerCase()));
    }

    public int getValue() {
        return sizes.get(this);
    }

}