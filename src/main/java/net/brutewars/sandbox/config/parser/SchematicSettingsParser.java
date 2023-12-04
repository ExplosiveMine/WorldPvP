package net.brutewars.sandbox.config.parser;

import lombok.Getter;
import net.brutewars.sandbox.BWorldPlugin;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class SchematicSettingsParser extends SectionParser {
    @Getter private int maxLengthX, maxLengthZ;

    @Getter private String bonusChest = "CHEST";
    @Getter private final Map<String, String> blockMap = new HashMap<>();

    public SchematicSettingsParser(BWorldPlugin plugin) {
        super(plugin, "schematic" + File.separator + "schematic-settings.yml");
    }

    @Override
    public void parse() {
        maxLengthX = getSection().getInt("schematic length x", 5);
        maxLengthZ = getSection().getInt("schematic length z", 6);

        ConfigurationSection blocks = getSection().getConfigurationSection("blocks");
        if (blocks == null)
            return;

        bonusChest = "minecraft:" + blocks.getString("special.BONUS_CHEST", "chest").toLowerCase();

        ConfigurationSection otherBlocks = blocks.getConfigurationSection("other");
        if (otherBlocks == null)
            return;

        otherBlocks.getKeys(false).forEach(s -> blockMap.put("minecraft:" + s.toLowerCase(), "minecraft:" + otherBlocks.getString(s, "air").toLowerCase()));
    }

}