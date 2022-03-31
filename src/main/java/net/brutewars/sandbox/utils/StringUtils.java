package net.brutewars.sandbox.utils;

import org.bukkit.ChatColor;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public final class StringUtils {
    public static String colour(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String removeColour(String s) {
        return ChatColor.stripColor(colour(s));
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
        return formatTimeFromMilliseconds(timeUnit.toMillis(time));
    }

    private static String formatTimeFromMilliseconds(long millis) {
        Duration duration = Duration.ofMillis(millis);
        StringBuilder timeBuilder = new StringBuilder();

        {
            long days = duration.toDays();

            if (days > 0) {
                timeBuilder.insert(0, days).insert(0, " ").insert(0, days == 1 ? "day" : "days").insert(0, ", ");
                duration = duration.minusDays(days);
            }
        }

        {
            long hours = duration.toHours();

            if (hours > 0) {
                timeBuilder.insert(0, hours).insert(0, " ").insert(0, hours == 1 ? "hour" : "hours").insert(0, ", ");
                duration = duration.minusHours(hours);
            }
        }

        {
            long minutes = duration.toMinutes();

            if (minutes > 0) {
                timeBuilder.insert(0, minutes).insert(0, " ").insert(0, minutes == 1 ? "minute" : "minutes").insert(0, " ,");
                duration = duration.minusMinutes(minutes);
            }
        }

        {
            long seconds = duration.getSeconds();

            if (seconds > 0) {
                timeBuilder.insert(0, seconds).insert(0, " ").insert(0, seconds == 1 ? "second" : "seconds").insert(0, " ,");
            }
        }

        if (timeBuilder.length() == 0) {
            timeBuilder.insert(0, "1 ").append("second").insert(0, " ,");
        }

        return timeBuilder.substring(2);
    }

}