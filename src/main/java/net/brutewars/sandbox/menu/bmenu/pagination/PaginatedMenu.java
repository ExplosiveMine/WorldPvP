package net.brutewars.sandbox.menu.bmenu.pagination;

import com.google.common.base.Preconditions;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.menu.items.BaseMenuItem;
import net.brutewars.sandbox.menu.bmenu.Menu;
import net.brutewars.sandbox.player.BPlayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class PaginatedMenu extends Menu {
    private List<MenuPage> pages;

    public PaginatedMenu(BWorldPlugin plugin, String identifier, String title, int size, String parentMenuId) {
        super(plugin, identifier, title, size, parentMenuId);
    }

    /**
     * Convenient method to populate the pages
     *
     * @param iterator supplies the items
     * @param pageSize the size for which the items are added. This is not the inventory size
     *
     */
    public void populate(final Iterator<? extends BaseMenuItem> iterator, final int pageSize, final int next, final int previous) {
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

    public void open(final BPlayer bPlayer, final int page) {
        if (reloadOnOpen())
            init();
        getPage(page).open(bPlayer);
    }

    @Override
    public void setItem(int slot, BaseMenuItem menuItem) {
        setItem(slot, menuItem, 0);
    }

    public void setItem(int slot, BaseMenuItem item, int page) {
        getPage(page).setItem(slot, item);
    }

    public MenuPage addPage(final int previous, final int next) {
        final int id = pages.size();
        final MenuPage page = new MenuPage(plugin, identifier, id, title, size);
        pages.add(page);

        page.setPreviousArrow(previous, (event, bPlayer) -> {
            if (id == 0) return;

            close(bPlayer, false);
            open(bPlayer, id - 1);
        });

        page.setNextArrow(next, (event, bPlayer) -> {
            if (id == pages.size() - 1) return;

            close(bPlayer, false);
            open(bPlayer, id + 1);
        });

        return page;
    }

    private MenuPage getPage(int page) {
        Preconditions.checkArgument(page >= 0 && page < pages.size(), "PAGE argument cannot take value " + page + ". PAGES array has size: " + pages.size());
        return pages.get(page);
    }

    public abstract boolean reloadOnOpen();

}