package net.brutewars.worldpvp.utils;

import com.google.common.collect.ImmutableBiMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.brutewars.worldpvp.player.BPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class JSONMessage {

    static {
        ImmutableBiMap.Builder<ChatColor, String> builder = ImmutableBiMap.builder();
        for (final ChatColor style : ChatColor.values()) {
            if (!style.isFormat()) {
                continue;
            }

            String styleName;
            switch (style) {
                case MAGIC:
                    styleName = "obfuscated";
                    break;
                case UNDERLINE:
                    styleName = "underlined";
                    break;
                default:
                    styleName = style.name().toLowerCase();
                    break;
            }

            builder.put(style, styleName);
        }
        builder.build();
    }


    private final List<MessagePart> parts = new ArrayList<>();

    private JSONMessage(String text) {
        parts.add(new MessagePart(text));
    }

    public static JSONMessage create(String text) {
        return new JSONMessage(text);
    }

    public static JSONMessage create() {
        return create("");
    }

    public MessagePart last() {
        if (parts.size() <= 0) {
            throw new ArrayIndexOutOfBoundsException("No MessageParts exist!");
        }
        return parts.get(parts.size() - 1);
    }

    public JsonObject toJSON() {
        JsonObject obj = new JsonObject();

        obj.addProperty("text", "");

        JsonArray array = new JsonArray();

        parts.stream()
                .map(MessagePart::toJSON)
                .forEach(array::add);

        obj.add("extra", array);

        return obj;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }


    public void send(Player... players) {
        ReflectionHelper.sendPacket(ReflectionHelper.createTextPacket(toString()), players);
    }

    public void send(BPlayer... bPlayers) {
        for (BPlayer bPlayer : bPlayers)
            bPlayer.runIfOnline(player -> ReflectionHelper.sendPacket(ReflectionHelper.createTextPacket(toString()), player));
    }

    public void title(int fadeIn, int stay, int fadeOut, Player... players) {
        ReflectionHelper.sendPacket(ReflectionHelper.createTitleTimesPacket(fadeIn, stay, fadeOut), players);
        ReflectionHelper.sendPacket(ReflectionHelper.createTitlePacket(toString()), players);
    }

    public JSONMessage runCommand(String command) {
        last().setOnClick(ClickEvent.runCommand(command));
        return this;
    }

    public JSONMessage tooltip(String text) {
        last().setOnHover(HoverEvent.showText(text));
        return this;
    }

    public JSONMessage then(String text) {
        return then(new MessagePart(text));
    }

    public JSONMessage then(MessagePart nextPart) {
        parts.add(nextPart);
        return this;
    }

    public static class MessageEvent {

        private final String action;
        private final Object value;

        public MessageEvent(String action, Object value) {
            this.action = action;
            this.value = value;
        }

        public JsonObject toJSON() {
            JsonObject obj = new JsonObject();
            obj.addProperty("action", action);
            String valueType = (ReflectionHelper.MAJOR_VER >= 16 && action.startsWith("show_")) ? "contents" : "value";

            if (value instanceof JsonElement) {
                obj.add(valueType, (JsonElement) value);
            } else {
                obj.addProperty(valueType, value.toString());
            }
            return obj;
        }
    }

    public static class ClickEvent {
        public static MessageEvent runCommand(String command) {
            return new MessageEvent("run_command", command);
        }
    }

    public static class HoverEvent {
        public static MessageEvent showText(String text) {
            return new MessageEvent("show_text", text);
        }
    }

    private static class ReflectionHelper {

        private static final String version;
        private static Constructor<?> chatComponentText;

        private static Class<?> packetPlayOutChat;
        private static Field packetPlayOutChatComponent;
        private static Field packetPlayOutChatMessageType;
        private static Field packetPlayOutChatUuid;
        private static Object enumChatMessageTypeMessage;

        private static Constructor<?> titlePacketConstructor;
        private static Constructor<?> titleTimesPacketConstructor;
        private static Object enumActionTitle;

        private static Field connection;
        private static MethodHandle GET_HANDLE;
        private static MethodHandle SEND_PACKET;
        private static MethodHandle STRING_TO_CHAT;
        private static boolean SETUP;
        private static int MAJOR_VER = -1;

        static {
            String[] split = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
            version = split[split.length - 1];

            try {
                MAJOR_VER = Integer.parseInt(version.split("_")[1]);

                final Class<?> craftPlayer = getClass("{obc}.entity.CraftPlayer");
                Method getHandle = craftPlayer.getMethod("getHandle");
                connection = getHandle.getReturnType().getField("playerConnection");
                Method sendPacket = connection.getType().getMethod("sendPacket", getClass("{nms}.Packet"));

                chatComponentText = getClass("{nms}.ChatComponentText").getConstructor(String.class);

                final Class<?> iChatBaseComponent = getClass("{nms}.IChatBaseComponent");

                Method stringToChat;

                if (MAJOR_VER < 8) {
                    stringToChat = getClass("{nms}.ChatSerializer").getMethod("a", String.class);
                } else {
                    stringToChat = getClass("{nms}.IChatBaseComponent$ChatSerializer").getMethod("a", String.class);
                }

                GET_HANDLE = MethodHandles.lookup().unreflect(getHandle);
                SEND_PACKET = MethodHandles.lookup().unreflect(sendPacket);
                STRING_TO_CHAT = MethodHandles.lookup().unreflect(stringToChat);

                packetPlayOutChat = getClass("{nms}.PacketPlayOutChat");
                packetPlayOutChatComponent = getField(packetPlayOutChat, "a");
                packetPlayOutChatMessageType = getField(packetPlayOutChat, "b");
                packetPlayOutChatUuid = MAJOR_VER >= 16 ? getField(packetPlayOutChat, "c") : null;

                Class<?> packetPlayOutTitle = getClass("{nms}.PacketPlayOutTitle");
                Class<?> titleAction = getClass("{nms}.PacketPlayOutTitle$EnumTitleAction");

                titlePacketConstructor = packetPlayOutTitle.getConstructor(titleAction, iChatBaseComponent);
                titleTimesPacketConstructor = packetPlayOutTitle.getConstructor(int.class, int.class, int.class);

                enumActionTitle = titleAction.getField("TITLE").get(null);

                if (MAJOR_VER >= 12) {
                    Method getChatMessageType = getClass("{nms}.ChatMessageType").getMethod("a", byte.class);

                    enumChatMessageTypeMessage = getChatMessageType.invoke(null, (byte) 1);
                    getChatMessageType.invoke(null, (byte) 2);
                }

                SETUP = true;
            } catch (Exception e) {
                e.printStackTrace();
                SETUP = false;
            }
        }

        static void sendPacket(Object packet, Player... players) {
            assertIsSetup();

            if (packet == null) {
                return;
            }

            for (Player player : players) {
                try {
                    SEND_PACKET.bindTo(connection.get(GET_HANDLE.bindTo(player).invoke())).invoke(packet);
                } catch (Throwable e) {
                    System.err.println("Failed to send packet");
                    e.printStackTrace();
                }
            }

        }

        static Object createTextPacket(String message) {
            assertIsSetup();

            try {
                Object packet = packetPlayOutChat.newInstance();
                setFieldValue(packetPlayOutChatComponent, packet, fromJson(message));
                setFieldValue(packetPlayOutChatUuid, packet, UUID.randomUUID());
                setType(packet);
                return packet;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        static Object createTitlePacket(String message) {
            assertIsSetup();

            try {
                return titlePacketConstructor.newInstance(enumActionTitle, fromJson(message));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        static Object createTitleTimesPacket(int fadeIn, int stay, int fadeOut) {
            assertIsSetup();

            try {
                return titleTimesPacketConstructor.newInstance(fadeIn, stay, fadeOut);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private static void setType(Object chatPacket) {
            assertIsSetup();

            if (MAJOR_VER < 12) {
                setFieldValue(packetPlayOutChatMessageType, chatPacket, (byte) 1);
                return;
            }

            setFieldValue(packetPlayOutChatMessageType, chatPacket, enumChatMessageTypeMessage);
        }

        static Object componentText(String message) {
            assertIsSetup();

            try {
                return chatComponentText.newInstance(message);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        static Object fromJson(String json) {
            assertIsSetup();

            if (!json.trim().startsWith("{")) {
                return componentText(json);
            }

            try {
                return STRING_TO_CHAT.invoke(json);
            } catch (Throwable e) {
                e.printStackTrace();
                return null;
            }
        }

        private static void assertIsSetup() {
            if (!SETUP) {
                throw new IllegalStateException("JSONMessage.ReflectionHelper is not set up yet!");
            }
        }

        private static Class<?> getClass(String path) throws ClassNotFoundException {
            return Class.forName(path.replace("{nms}", "net.minecraft.server." + version).replace("{obc}", "org.bukkit.craftbukkit." + version));
        }

        private static void setFieldValue(Field field, Object instance, Object value) {
            if (field == null) {
                // useful for fields that might not exist
                return;
            }

            try {
                field.set(instance, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        private static Field getField(Class<?> classObject, String fieldName) {
            try {
                Field field = classObject.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static class MessagePart {
        private MessageEvent onClick;
        private MessageEvent onHover;
        private final String text;

        public MessagePart(String text) {
            this.text = text == null ? "null" : text;
        }

        public JsonObject toJSON() {
            Objects.requireNonNull(text);

            JsonObject obj = new JsonObject();
            obj.addProperty("text", text);

            if (onClick != null) {
                obj.add("clickEvent", onClick.toJSON());
            }

            if (onHover != null) {
                obj.add("hoverEvent", onHover.toJSON());
            }

            return obj;

        }

        public void setOnClick(MessageEvent onClick) {
            this.onClick = onClick;
        }

        public void setOnHover(MessageEvent onHover) {
            this.onHover = onHover;
        }
    }
}
