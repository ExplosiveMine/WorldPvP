package net.brutewars.worldpvp.utils;

import net.brutewars.worldpvp.player.BPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class ActionBar {

    private final PacketPlayOutChat packet;

    public ActionBar(String text) {
        this.packet = new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + text + "\"}"), (byte) 2);
    }

    public void send(BPlayer bPlayer) {
        bPlayer.runIfOnline(player -> ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet));
    }

    public void send() {
        for (Player p : Bukkit.getServer().getOnlinePlayers())
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);

    }
}

