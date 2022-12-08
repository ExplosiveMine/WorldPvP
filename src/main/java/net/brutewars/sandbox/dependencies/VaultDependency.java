package net.brutewars.sandbox.dependencies;

import net.brutewars.sandbox.BWorldPlugin;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class VaultDependency extends Dependency {
    private Permission permissions;

    public VaultDependency(BWorldPlugin plugin) {
        super(plugin, "Vault");
    }

    @Override
    public boolean setup() {
        if (plugin.getServer().getPluginManager().getPlugin(name) == null)
            return false;

        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null)
            return false;

        permissions = rsp.getProvider();
        return true;
    }

    public boolean hasPermission(OfflinePlayer offlinePlayer, String permission) {
        return permissions.playerHas(null, offlinePlayer, permission);
    }

}