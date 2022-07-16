package net.brutewars.sandbox.menu.items.builders;


import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;


public final class SkullBuilder extends BaseItemBuilder<SkullBuilder> {
    public SkullBuilder() {
        this(new ItemStack(Material.PLAYER_HEAD));
    }

    public SkullBuilder(ItemStack item) {
        super(item);
    }

    private SkullMeta getSkullMeta() {
        return (SkullMeta) meta;
    }

    public SkullBuilder setOwner(BPlayer bPlayer) {
        getSkullMeta().setOwningPlayer(bPlayer.toOfflinePlayer());
        return this;
    }

    public SkullBuilder setTexture(String url) {
        PlayerProfile playerProfile = Bukkit.createProfile(UUID.randomUUID());
        playerProfile.setProperty(new ProfileProperty("textures", url));
        getSkullMeta().setPlayerProfile(playerProfile);
        return this;
    }

}