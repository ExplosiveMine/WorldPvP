package net.brutewars.sandbox.utils;

import net.brutewars.sandbox.BWorldPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataType;

public final class PersistentDataUtils {

    public static <T> void storeData(BWorldPlugin plugin, World world, PersistentDataType<T, T> type, String id, T value) {
        world.getPersistentDataContainer().set(new NamespacedKey(plugin, id), type, value);
    }

    public static <T> T getData(BWorldPlugin plugin, World world, PersistentDataType<T, T> type, String id, T defaultValue) {
        return world.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, id), type, defaultValue);
    }

    public static void remove(BWorldPlugin plugin, World world, String id) {
        world.getPersistentDataContainer().remove(new NamespacedKey(plugin, id));
    }
}
