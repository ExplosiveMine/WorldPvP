package net.brutewars.sandbox.menu.impl;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.BWorld;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.menu.MenuIdentifier;
import net.brutewars.sandbox.menu.items.builders.ItemBuilder;
import net.brutewars.sandbox.menu.items.builders.SkullBuilder;
import net.brutewars.sandbox.menu.menus.Menu;
import net.brutewars.sandbox.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.UUID;

public class JoinWorldMenu extends Menu {
    public JoinWorldMenu(BWorldPlugin plugin) {
        super(plugin, MenuIdentifier.JOINWORLD, Lang.JOIN_WORLD_MENU.get(), 36);
    }

    @Override
    public void placeItems() {
        for (int i = 0; i < 36; i++) {
            int index = i;
            setItem(i, new ItemBuilder(Material.AIR)
                    .onClick((event, bPlayer) -> {
                        String id = new ItemBuilder(event.getCurrentItem()).getKey(plugin, "bWorld_uuid", PersistentDataType.STRING, "");
                        if (!StringUtils.isUUID(id))
                            return;

                        plugin.getBWorldManager().getBWorld(UUID.fromString(id)).teleportToWorld(bPlayer);
                    })
                    .setFunction((itemStack, bPlayer) -> {
                        List<UUID> additionalBWorlds = bPlayer.getAdditionalBWorlds();
                        if (additionalBWorlds.size() <= index)
                            return new ItemBuilder(itemStack);

                        BWorld bWorld = plugin.getBWorldManager().getBWorld(additionalBWorlds.get(index));
                        return new SkullBuilder()
                                .setDisplayName(bWorld.getOwner().getName())
                                .setKey(plugin, "bWorld_uuid", PersistentDataType.STRING, bWorld.getUuid().toString())
                                .setLore("&7Click to teleport!")
                                .setOwner(bWorld.getOwner());
                    }));
        }
    }

}