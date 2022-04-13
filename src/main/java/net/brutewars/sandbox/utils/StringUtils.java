package net.brutewars.sandbox.utils;

import org.bukkit.ChatColor;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public final class StringUtils {
    public static String colour(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String replaceArgs(String msg, Object... objects) {
        if (msg == null) return null;

        for (int i = 0; i < objects.length; i++) {
            String objectString = objects[i].toString();
            msg = msg.replace("{" + i + "}", objectString);
        }

        return msg;
    }

    /*
    ===================
           TIME
    ===================
    */

    public static String formatTime(long time, TimeUnit timeUnit) {
        Duration duration = Duration.ofMillis(timeUnit.toMillis(time));
        StringBuilder timeBuilder = new StringBuilder();

        boolean addComma = false;

        long days = duration.toDays();
        if (days > 0) {
            timeBuilder.append(days).append(days == 1 ? " day" : " days");
            duration = duration.minusDays(days);
            addComma = true;
        }

        long hours = duration.toHours();
        if (hours > 0) {
            if (addComma)
                timeBuilder.append(", ");
            timeBuilder.append(hours).append(" ").append(hours == 1 ? "hour" : "hours");
            duration = duration.minusHours(hours);
            addComma = true;
        }

        long minutes = duration.toMinutes();
        if (minutes > 0) {
            if (addComma)
                timeBuilder.append(", ");
            timeBuilder.append(minutes).append(" ").append(minutes == 1 ? "minute" : "minutes");
            duration = duration.minusMinutes(minutes);
            addComma = true;
        }

        long seconds = duration.getSeconds();
        if (seconds > 0) {
            if (addComma)
                timeBuilder.append(", ");
            timeBuilder.append(seconds).append(" ").append(seconds == 1 ? "second" : "seconds");
        }

        if (timeBuilder.length() == 0)
            timeBuilder.append("0 ").append("second");

        return timeBuilder.toString();
    }

}