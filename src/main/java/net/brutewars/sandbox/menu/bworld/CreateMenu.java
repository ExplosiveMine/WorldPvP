package net.brutewars.sandbox.menu.bworld;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.menu.MenuIdentifier;
import net.brutewars.sandbox.menu.bmenu.Menu;
import net.brutewars.sandbox.menu.items.ItemFactory;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.WorldType;
import org.bukkit.event.inventory.InventoryType;

public final class CreateMenu extends Menu {
    public CreateMenu(BWorldPlugin plugin) {
        super(plugin, MenuIdentifier.CREATE.getIdentifier(), InventoryType.HOPPER, Lang.CREATE_MENU.get(), null);
    }

    @Override
    public void init() {
        setItem(0, ItemFactory.createSkull("&4&lFlat", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDUyOGVkNDU4MDI0MDBmNDY1YjVjNGUzYTZiN2E5ZjJiNmE1YjNkNDc4YjZmZDg0OTI1Y2M1ZDk4ODM5MWM3ZCJ9fX0=",
                (event, bPlayer) -> {
                    close(bPlayer, false);
                    createWorld(bPlayer, WorldType.FLAT);
                }, "&cYour world will be completely flat!"));

        setItem(2, ItemFactory.createSkull("&6&lRegular", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Y0MDk0MmYzNjRmNmNiY2VmZmNmMTE1MTc5NjQxMDI4NmE0OGIxYWViYTc3MjQzZTIxODAyNmMwOWNkMSJ9fX0===",
                (event, bPlayer) -> {
                    close(bPlayer, false);
                    createWorld(bPlayer, WorldType.NORMAL);
                }, "&eYour world will be as usual!"));

        setItem(4, ItemFactory.createSkull("&5&lAmplified", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWMxMWQ2Yzc5YjhhMWYxODkwMmQ3ODNjZGRhNGJkZmI5ZDQ3MzM3YjczNzkxMDI4YTEyNmE2ZTZjZjEwMWRlZiJ9fX0==",
                (event, bPlayer) -> {
                    close(bPlayer, false);
                    createWorld(bPlayer, WorldType.AMPLIFIED);
                }, "&dA world from the past with amplified terrain!"));
    }

    private void createWorld(final BPlayer bPlayer, final WorldType type) {
        plugin.getBWorldManager().createBWorld(bPlayer, type);
        Lang.WORLD_CREATING.send(bPlayer);
        plugin.getMenuManager().open(MenuIdentifier.CREATING_ANIMATION, bPlayer);
    }

}