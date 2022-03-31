package net.brutewars.sandbox.world;

import lombok.Getter;
import net.brutewars.sandbox.BWorldPlugin;

import java.util.HashMap;
import java.util.Map;

public enum WorldSize {
    DEFAULT("world.default"),
    TYRANT("world.tyrant"),
    NOMAD("world.nomad"),
    BRUTE("world.brute");

    @Getter private final String permission;
    private final static Map<WorldSize, Integer> sizes = new HashMap<>();

    WorldSize(final String permission) {
        this.permission = permission;
    }

    public static void reload(final BWorldPlugin plugin) {
        for (WorldSize size : values())
            sizes.put(size, plugin.getConfig().getInt("world.size." + size.name().toLowerCase()));
    }

    public int getValue() {
        return sizes.get(this);
    }

}