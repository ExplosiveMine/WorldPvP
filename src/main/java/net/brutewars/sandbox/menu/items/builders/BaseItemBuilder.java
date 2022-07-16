package net.brutewars.sandbox.menu.items.builders;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.menu.items.MenuItem;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.utils.StringUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked cast")
public abstract class BaseItemBuilder<Builder extends BaseItemBuilder<Builder>> {
    protected ItemStack item;

    protected ItemMeta meta;

    protected BiConsumer<InventoryClickEvent, BPlayer> action;

    protected BiFunction<ItemStack, BPlayer, ItemStack> function;

    public BaseItemBuilder(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
    }

    public Builder setAmount(int amount) {
        item.setAmount(amount);
        return (Builder) this;
    }

    public Builder setDisplayName(String displayName) {
        meta.displayName(Component.text(StringUtils.colour(displayName)));
        return (Builder) this;
    }

    public Builder setLore(String...lore) {
        meta.lore(Arrays.stream(lore).map(s -> Component.text(StringUtils.colour(s))).collect(Collectors.toList()));
        return (Builder) this;
    }

    public Builder setGlowing(boolean glowing) {
        if (glowing) {
            meta.addEnchant(Enchantment.ARROW_INFINITE, 0, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else
            meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);

        return (Builder) this;
    }

    public <T, Z> Builder setKey(BWorldPlugin plugin, String identifier, PersistentDataType<T, Z> dataType, Z data) {
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, identifier), dataType, data);
        return (Builder) this;
    }

    public <T, Z> Builder getKey(BWorldPlugin plugin, String identifier, PersistentDataType<T, Z> dataType) {
        meta.getPersistentDataContainer().get(new NamespacedKey(plugin, identifier), dataType);
        return (Builder) this;
    }

    public Builder setAction(BiConsumer<InventoryClickEvent, BPlayer> action) {
        this.action = action;
        return (Builder) this;
    }

    public Builder setFunction(BiFunction<ItemStack, BPlayer, ? extends BaseItemBuilder<?>> function) {
        this.function = (item, bPlayer) -> function.apply(item, bPlayer).toItem();
        return (Builder) this;
    }

    public ItemStack toItem() {
        item.setItemMeta(meta);
        return item;
    }

    private void setItem(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
    }

    public MenuItem toMenuItem() {
        return new MenuItem(toItem(), action, function);
    }

}