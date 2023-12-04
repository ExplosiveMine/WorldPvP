package net.brutewars.sandbox.dependencies;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.utils.Logging;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public final class WorldEditDependency extends Dependency {
    public WorldEditDependency(BWorldPlugin plugin) {
        super(plugin, "WorldEdit");
    }

    @Override
    public boolean setup() {
        return true;
    }

    public @Nullable Clipboard getClipboard(File file) {
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format == null)
            return null;

        Clipboard clipboard;
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return clipboard;
    }

    /**
     * @param editSession editsession for the world which can be obtained using {@link WorldEditDependency#getNewEditSession(World)}
     * @param clipboard clipboard to be pasted
     * @param loc location at which the schematic is to be pasted
     */
    public void pasteClipboard(Clipboard clipboard, Location loc) {
        try (EditSession editSession = getNewEditSession(loc.getWorld())) {
            Operations.complete(new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()))
                    .ignoreAirBlocks(true)
                    .build());
        } catch (WorldEditException e) {
            Logging.severe("Could not paste schematic at " + loc);
        }
    }

    public EditSession getNewEditSession(World world) {
        return WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world));
    }

}