package net.brutewars.sandbox.menu.bmenu;

import lombok.Getter;
import lombok.Setter;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.menu.items.ItemFactory;
import net.brutewars.sandbox.player.BPlayer;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.function.BiFunction;

public abstract class AnvilInputMenu extends Menu {
    // what to do when the player clicks on the result slot which contains an item
    @Getter @Setter protected BiFunction<BPlayer, String, String> completeAction;

    public AnvilInputMenu(BWorldPlugin plugin, String identifier, String title, String parentMenuId) {
        super(plugin, identifier, title, InventoryType.ANVIL, parentMenuId);

        setItem(2, ItemFactory.createItem(Material.AIR, "", (event, bPlayer) -> {
            AnvilInputMenu anvilMenu = (AnvilInputMenu) event.getInventory().getHolder();
            ItemStack outputSlot = event.getCurrentItem();

            String response = anvilMenu.getCompleteAction().apply(bPlayer, outputSlot.hasItemMeta() ? outputSlot.getItemMeta().getDisplayName() : "");

            // successful
            if (response.isEmpty()) {
                bPlayer.playSound(Sound.ANVIL_USE, 0.5f, 0.5f);
                return;
            }

            // unsuccessful, a message is displayed in the text slot
            ItemMeta meta = outputSlot.getItemMeta();
            meta.setDisplayName(response);
            outputSlot.setItemMeta(meta);
            event.getInventory().setItem(0, outputSlot);
            bPlayer.runIfOnline(Player::updateInventory);
        }));
    }

    @Override
    public void open(BPlayer bPlayer) {
        EntityPlayer player = bPlayer.getIfOnline(p -> ((CraftPlayer) p).getHandle());

        if (player == null)
            return;

        AnvilContainer anvil = new AnvilContainer(player, this, title);
        int containerId = player.nextContainerCounter();

        player.playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerId, "minecraft:anvil", new ChatMessage(Blocks.ANVIL.a() + ".name")));
        anvil.windowId = containerId;
        anvil.addSlotListener(player);
        player.activeContainer = anvil;

        build(bPlayer);
    }

    @Override
    protected Inventory createInv(BPlayer bPlayer) {
        return bPlayer.getIfOnline(player -> player.getOpenInventory().getTopInventory());
    }

    public static final class AnvilContainer extends ContainerAnvil {
        public AnvilContainer(EntityHuman entityhuman, InventoryHolder inventoryHolder, String title) {
            super(entityhuman.inventory, entityhuman.getWorld(), new BlockPosition(0, 0 ,0), entityhuman);

            /*
            Set the InventoryHolder and title for the anvil inventory using reflection as Bukkit#createInventory
            does not work for anvil inventories since they were never implemented`in 1.8.8
             */
            try {
                Field subContainer = getClass().getSuperclass().getDeclaredField("h");
                subContainer.setAccessible(true);

                InventorySubcontainer h = ((InventorySubcontainer) subContainer.get(this));
                h.a(title);

                Field bukkitOwner = h.getClass().getSuperclass().getDeclaredField("bukkitOwner");
                bukkitOwner.setAccessible(true);
                bukkitOwner.set(h, inventoryHolder);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean a(EntityHuman entityHuman) {
            return true;
        }

        @Override
        public void b(EntityHuman entityhuman) {
        }

    }

}