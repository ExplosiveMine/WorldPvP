package net.brutewars.sandbox.menu;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.menu.items.builders.BaseItemBuilder;
import net.brutewars.sandbox.menu.items.builders.ItemBuilder;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.utils.Logging;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public final class HUDManager {
    private final BWorldPlugin plugin;

    private final Map<Integer, ItemBuilder> hudItems = new HashMap<>();
    private ItemBuilder blazeRodToggled = null;
    private ItemBuilder blazeRodNotToggled = null;

    public HUDManager(BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    public void setup() {
        if (blazeRodToggled != null)
            return;

        //NOTE: we haven't used setFunction anywhere, hence we can use itemBuilder#toItem directly
        add(3, new ItemBuilder(Material.CHEST)
                .setDisplayName("&eWorld Settings")
                .setLore("&7Manage your world!")
                .setKey(plugin, "hud_item", PersistentDataType.STRING, "world_settings")
                .onClick((event, _bPlayer) -> event.setCancelled(true))
                .onInteract((event, _bPlayer) -> {
                    event.setCancelled(true);

                    String cmd;
                    if (_bPlayer.getBWorld() == null)
                        cmd = "world";
                    else
                        cmd = "world settings";

                    _bPlayer.runIfOnline(player -> player.performCommand(cmd));
                }));

        add(4, new ItemBuilder(Material.EMERALD)
                .setDisplayName("&dSupport Server!")
                .setLore("&7Visit the shop")
                .setKey(plugin, "hud_item", PersistentDataType.STRING, "shop")
                .onClick((event, _bPlayer) -> event.setCancelled(true))
                .onInteract((event, _bPlayer) -> {
                    event.setCancelled(true);
                    _bPlayer.runIfOnline(_player -> _player.performCommand("buy"));
                }));

        add(5, new ItemBuilder(Material.ZOMBIE_SPAWN_EGG)
                .setDisplayName("&aJoin A World")
                .setLore("&7Go to your own or one of", "&7your friends' world!")
                .setKey(plugin, "hud_item", PersistentDataType.STRING, "worlds")
                .onClick((event, _bPlayer) -> event.setCancelled(true))
                .onInteract((event, _bPlayer) -> {
                    event.setCancelled(true);
                    plugin.getMenuManager().open(MenuIdentifier.JOINWORLD, _bPlayer);
                }));

        blazeRodToggled = new ItemBuilder(Material.BLAZE_ROD)
                .setDisplayName("&bToggle Items")
                .setLore("&7Click to hide items")
                .setGlowing(true)
                .setKey(plugin, "hud_item", PersistentDataType.STRING, "toggled=true")
                .onClick((event, bPlayer) -> onClickBlazeRod(event))
                .onInteract((event, _bPlayer) -> {
                    PlayerInventory inv = event.getPlayer().getInventory();
                    inv.setItem(inv.getHeldItemSlot(), blazeRodNotToggled.toMenuItem().getItem(_bPlayer));
                    removeItems(_bPlayer, false);
                });

        blazeRodNotToggled = new ItemBuilder(Material.BLAZE_ROD)
                .setDisplayName("&bToggle Items")
                .setLore("&7Click to show items")
                .setKey(plugin, "hud_item", PersistentDataType.STRING, "toggled=false")
                .onClick((event, bPlayer) -> onClickBlazeRod(event))
                .onInteract((event, _bPlayer) -> {
                    // if inventory is full, we display a message to player
                    if (!giveItems(_bPlayer)) {
                        _bPlayer.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 0.5f);
                        _bPlayer.runIfOnline(player -> player.showTitle(Title.title(
                                Component.text(Lang.INVENTORY_FULL_TITLE.get()),
                                Component.text(Lang.INVENTORY_FULL_SUBTITLE.get())
                        )));
                    }
                });
    }

    private void onClickBlazeRod(InventoryClickEvent event) {
        ClickType type = event.getClick();
        if (type == ClickType.DROP || type == ClickType.CONTROL_DROP)
            return;

        Logging.debug(plugin, type.toString());

        if (event.getInventory().getType() != InventoryType.CRAFTING)
            event.setCancelled(true);
    }

    private void add(int defaultSlot, ItemBuilder itemBuilder) {
        hudItems.put(defaultSlot, itemBuilder);
    }

    /**
     * @param bPlayer the player
     * @return whether items were given successfully or not because the player was offline
     * or the inventory was full
     */
    public boolean giveItems(BPlayer bPlayer) {
        Inventory inv = bPlayer.getIfOnline(Player::getInventory);
        // player is offline
        if (inv == null)
            return false;

        if (inv.contains(blazeRodNotToggled.toItem())) {
            // Player toggled HUD items

            int slot = inv.first(blazeRodNotToggled.toItem());
            bPlayer.setHudSlot(slot);

            // check whether player has enough inv space
            if (!hasMinimumFreeSlots(inv, 3))
                return false;

            giveItem(inv, slot, blazeRodToggled);
            bPlayer.setHudToggled(true);

            List<ItemStack> replacedItems = new ArrayList<>();
            for (Map.Entry<Integer, ItemBuilder> entry : hudItems.entrySet())
                replacedItems.add(giveItem(inv, entry.getKey(), entry.getValue()));

            replacedItems.stream().filter(Objects::nonNull).forEach(inv::addItem);
        } else {
            // giving player hud items on joining server/respawning

            if (!hasMinimumFreeSlots(inv, 1))
                return false;

            boolean toggled = bPlayer.isHudToggled();
            if (!hasMinimumFreeSlots(inv, 4))
                toggled = false;

            List<ItemStack> replacedItems = new ArrayList<>();
            if (toggled) {
                replacedItems.add(giveItem(inv, bPlayer.getHudSlot(), blazeRodToggled));

                for (Map.Entry<Integer, ItemBuilder> entry : hudItems.entrySet())
                    replacedItems.add(giveItem(inv, entry.getKey(), entry.getValue()));
            } else {
                replacedItems.add(giveItem(inv, bPlayer.getHudSlot(), blazeRodNotToggled));
            }

            replacedItems.stream().filter(Objects::nonNull).forEach(inv::addItem);
        }
        return true;
    }

    private ItemStack giveItem(Inventory inv, int slot, ItemBuilder builder) {
        ItemStack toReplace = inv.getItem(slot);
        inv.setItem(slot, builder.toItem());
        return toReplace;
    }

    private boolean hasMinimumFreeSlots(Inventory inv, int minimumFreeSlots) {
        int numFreeSlots = 0;
        for (ItemStack item : inv.getStorageContents()) {
            if (item != null && item.getType() != Material.AIR)
                continue;

            numFreeSlots++;
            if (numFreeSlots >= minimumFreeSlots)
                return true;
        }
        return false;
    }

    public void removeItems(BPlayer bPlayer, boolean removeBlazeRod) {
        PlayerInventory inv = bPlayer.getIfOnline(Player::getInventory);
        if (inv == null)
            return;

        List<ItemStack> hudItems = getHUDItems(false);
        ItemStack[] contents = inv.getContents().clone();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null)
                continue;

            if (hudItems.contains(item))
                inv.remove(item);

            if (removeBlazeRod && (item.equals(blazeRodToggled.toItem()) || item.equals(blazeRodNotToggled.toItem()))) {
                bPlayer.setHudSlot(i);
                inv.remove(item);
            }
        }

        //toggling off
        if (!removeBlazeRod)
            bPlayer.setHudToggled(false);
    }


    private boolean hasHUDItems(BPlayer bPlayer) {
        Inventory inv = bPlayer.getIfOnline(Player::getInventory);
        if (inv == null)
            return false;

        for (ItemStack item : inv.getContents()) {
            if (item == null)
                continue;

            String key = new ItemBuilder(item).getKey(plugin, "hud_item", PersistentDataType.STRING, "");
            if (key.isEmpty())
                continue;

            return true;
        }

        return false;
    }

    private List<ItemStack> getHUDItems(boolean includeBlazeRod) {
        List<ItemStack> items = hudItems.values().stream().map(BaseItemBuilder::toItem).collect(Collectors.toList());
        if (includeBlazeRod) {
            items.add(blazeRodToggled.toItem());
            items.add(blazeRodNotToggled.toItem());
        }

        return items;
    }

    private @Nullable ItemBuilder getItem(int slot, ItemStack item) {
        if (hudItems.containsKey(slot) && hudItems.get(slot).toItem().equals(item))
            return hudItems.get(slot);
        else if (blazeRodToggled.toItem().equals(item))
            return blazeRodToggled;
        else if (blazeRodNotToggled.toItem().equals(item))
            return blazeRodNotToggled;

        return null;
    }

    // EVENTS
    public void onPlayerClick(InventoryClickEvent event) {
        BPlayer bPlayer = plugin.getBPlayerManager().get(event.getWhoClicked().getUniqueId());

        int slot;
        ItemStack currentItem;
        if (event.getClick() == ClickType.NUMBER_KEY) {
            slot = event.getHotbarButton();
            currentItem = event.getView().getBottomInventory().getItem(slot);

            // prevent placing the cursor on the hud item and pressing a hotbar button
            // to move it
            ItemBuilder builder = getItem(slot, currentItem);
            if (builder == null) {
                slot = event.getSlot();
                currentItem = event.getView().getBottomInventory().getItem(slot);
            }
        } else if (event.getAction().toString().contains("PLACE")) {
            // PLACE_ALL / PLACE_SOME / PLACE_ONE

            slot = event.getSlot();
            currentItem = event.getCursor();
        } else {
            slot = event.getSlot();
            currentItem = event.getCurrentItem();
        }

        ItemBuilder builder = getItem(slot, currentItem);
        if (builder != null && builder.toMenuItem().getAction() != null) {
            // prevent item from being moved to crafting grid of player inventory
            int rawSlot = event.getRawSlot();
            if (event.getInventory().getType() == InventoryType.CRAFTING && rawSlot > 0 && rawSlot <= 4) {
                event.setCancelled(true);
                return;
            }

            builder.toMenuItem().getAction().accept(event, bPlayer);
        }
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        // Interact event is fired for each hand hence cancel one hand
        // event is also fired for dropping items as a left click so cancel those for HUD items
        if (event.getHand() != EquipmentSlot.HAND || !event.getAction().isRightClick())
            return;

        int heldSlot = event.getPlayer().getInventory().getHeldItemSlot();
        ItemBuilder item = getItem(heldSlot, event.getItem());
        if (item == null)
            return;

        BPlayer bPlayer = plugin.getBPlayerManager().get(event.getPlayer());
        item.toInteractableItem().getAction().accept(event, bPlayer);
    }

    public void onPlayerDrop(PlayerDropItemEvent event) {
        int heldSlot = event.getPlayer().getInventory().getHeldItemSlot();
        ItemBuilder item = getItem(heldSlot, event.getItemDrop().getItemStack());
        if (item != null) {
            event.getPlayer().sendActionBar(Component.text(Lang.CANNOT_DROP_ITEM.get()));
            event.setCancelled(true);
        }
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        BPlayer bPlayer = plugin.getBPlayerManager().get(event.getPlayer());
        giveItems(bPlayer);
    }

    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().removeAll(getHUDItems(true));
    }

    public void onPlayerRespawn(PlayerRespawnEvent event) {
        BPlayer bPlayer = plugin.getBPlayerManager().get(event.getPlayer());
        if (!hasHUDItems(bPlayer))
            giveItems(bPlayer);
    }

    public void onPlayerQuit(Player player) {
        BPlayer bPlayer = plugin.getBPlayerManager().get(player);
        removeItems(bPlayer, true);
    }

}