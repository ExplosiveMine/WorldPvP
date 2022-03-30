package net.brutewars.worldpvp.hooks;

import net.brutewars.worldpvp.BWorldPlugin;
import net.brutewars.worldpvp.config.Lang;
import net.brutewars.worldpvp.player.BPlayer;
import net.brutewars.worldpvp.utils.Logging;
import net.brutewars.worldpvp.utils.StringUtils;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class Vault {
    private final BWorldPlugin plugin;

    private Permission perms;

    public Vault(final BWorldPlugin plugin) {
        this.plugin = plugin;

        if (!setupPermissions()) {
            Logging.severe(String.format("[%s] - Disabled due to no Vault dependency found!", plugin.getDescription().getName()));
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        Logging.info(Lang.SUCCESSFUL_HOOK.get("Vault"));
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public boolean hasPermission(final OfflinePlayer offlinePlayer, final String permission) {
        return perms.playerHas(plugin.getServer().getWorlds().get(0).getName(), offlinePlayer, permission);
    }

}