package net.brutewars.sandbox.bworld.world.dimensions;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.World;
import org.bukkit.WorldType;

public final class NetherDimension extends SandboxWorld {

    public NetherDimension(BWorldPlugin plugin, String name) {
        super(plugin, World.Environment.NETHER, WorldType.NORMAL, name);
    }

    @Override
    public void teleportToWorld(BPlayer bPlayer) {
        //noop
    }

    @Override
    public boolean createSpawn() {
        //noop
        return true;
    }

    @Override
    public void findSpawnLocation() {
        //noop
    }

}