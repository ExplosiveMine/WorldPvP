package net.brutewars.sandbox.menu.menus.pagination;

import com.google.common.base.Preconditions;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.menu.MenuIdentifier;
import net.brutewars.sandbox.menu.items.builders.BaseItemBuilder;
import net.brutewars.sandbox.menu.menus.Menu;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.*;

public abstract class PaginatedMenu extends Menu {
    private List<MenuPage> pages;

    private final Map<BPlayer, Integer> playerMap = new HashMap<>();

    public PaginatedMenu(BWorldPlugin plugin, MenuIdentifier identifier, String title, int size) {
        super(plugin, identifier, title, size);
        this.pages = new ArrayList<>();
    }

    /**
     * Convenient method to populate the pages
     *
     * @param iterator supplies the items
     * @param pageSize the size for which the items are added. This is not the inventory size
     */
    public void populate(Iterator<? extends BaseItemBuilder<?>> iterator, int pageSize, int next, int previous) {
        this.pages = new ArrayList<>();
        MenuPage page = addPage(next, previous);
        int slot = 0;
        while (iterator.hasNext()) {
            if (slot == pageSize) {
                page = addPage(next, previous);
                slot = 0;
            }

            page.setItem(slot, iterator.next());
            slot++;
        }
    }

    @Override
    public void open(BPlayer bPlayer) {
        open(bPlayer, 0);
    }

    public void open(BPlayer bPlayer, int page) {
        if (bPlayer.isSleeping())
            return;

        if (reloadOnOpen() || !setup) {
            setup = true;
            placeItems();
        }

        getPage(page).open(bPlayer);
        playerMap.put(bPlayer, page);
    }

    @Override
    public void setItem(int slot, BaseItemBuilder<?> baseItemBuilder) {
        setItem(slot, baseItemBuilder, 0);
    }

    public void setItem(int slot, BaseItemBuilder<?> baseItemBuilder, int page) {
        getPage(page).setItem(slot, baseItemBuilder);
    }

    public MenuPage addPage(int previous, int next) {
        int id = pages.size();
        MenuPage page = new MenuPage(plugin, identifier, title, size);
        pages.add(page);

        page.setNextArrow(next, (event, bPlayer) -> {
            if (id == pages.size() - 1) return;

            close(bPlayer, false);
            open(bPlayer, id + 1);
        });

        page.setPreviousArrow(previous, (event, bPlayer) -> {
            if (id == 0) return;

            close(bPlayer, false);
            open(bPlayer, id - 1);
        });

        return page;
    }

    private MenuPage getPage(int page) {
        Preconditions.checkArgument(page >= 0 && page < pages.size(), "PAGE argument cannot take value " + page + ". PAGES array has size: " + pages.size());
        return pages.get(page);
    }

    public boolean reloadOnOpen() {
        return false;
    }

    @Override
    public void clickItemAt(InventoryClickEvent event) {
        getPage(playerMap.get(plugin.getBPlayerManager().get(event.getWhoClicked().getUniqueId()))).clickItemAt(event);
    }

    @Override
    public void onClose(InventoryCloseEvent event, BPlayer bPlayer) {
        playerMap.remove(bPlayer);
        super.onClose(event, bPlayer);
    }
}