package net.brutewars.sandbox.menu.items;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public final class ItemFactory {
    public static MenuItem createItem(Material material, int value, String displayName, BiConsumer<InventoryClickEvent, BPlayer> action, String...lore) {
        return new MenuItem(getItem(material, value, displayName, lore), action);
    }

    public static MenuItem createItem(Material material, String displayName, BiConsumer<InventoryClickEvent, BPlayer> action, String...lore) {
        return createItem(material, 0, displayName, action, lore);
    }

    public static MenuItem createMenuArrow(String displayName, BiConsumer<InventoryClickEvent, BPlayer> action) {
        return new MenuItem(getItem(Material.EXP_BOTTLE, 0, displayName), action);
    }

    public static MenuItem createSkull(String displayName, Player player, BiConsumer<InventoryClickEvent, BPlayer> action, String...lore) {
        ItemStack item = getItem(Material.SKULL_ITEM, 3, displayName, lore);

        SkullMeta skullMeta = ((SkullMeta) item.getItemMeta());
        skullMeta.setOwner(player.getName());
        item.setItemMeta(skullMeta);

        return new MenuItem(item, action);
    }

    public static MenuItem createSkull(String displayName, String url, BiConsumer<InventoryClickEvent, BPlayer> action, String...lore) {
        ItemStack skull = getItem(Material.SKULL_ITEM, 3, displayName, lore);

        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
        gameProfile.getProperties().put("textures", new Property("textures", url));

        try {
            Field field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, gameProfile);
        } catch (NoSuchFieldException | IllegalAccessException ignored) { }

        skull.setItemMeta(skullMeta);

        return new MenuItem(skull, action);
    }

    private static ItemStack getItem(Material material, int value, String displayName, String...lore) {
        ItemStack item = new ItemStack(material, 1, (byte) value);

        if (material.equals(Material.AIR)) return item;

        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(StringUtils.colour(displayName));
        if (lore.length > 0)
            itemMeta.setLore(Arrays.stream(lore).filter(s -> !s.isEmpty()).map(StringUtils::colour).collect(Collectors.toList()));

        item.setItemMeta(itemMeta);

        return item;
    }



}