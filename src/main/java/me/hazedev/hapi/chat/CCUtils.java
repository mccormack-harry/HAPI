package me.hazedev.hapi.chat;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CCUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("<#([A-Fa-f0-9]){6}>");

    public static final String AQUA = ChatColor.AQUA.toString();
    public static final String BLACK = ChatColor.BLACK.toString();
    public static final String BLUE = ChatColor.BLUE.toString();
    public static final String BOLD = ChatColor.BOLD.toString();
    public static final String DARK_AQUA = ChatColor.DARK_AQUA.toString();
    public static final String DARK_BLUE = ChatColor.DARK_BLUE.toString();
    public static final String DARK_GRAY = ChatColor.DARK_GRAY.toString();
    public static final String DARK_GREEN = ChatColor.DARK_GREEN.toString();
    public static final String DARK_PURPLE = ChatColor.DARK_PURPLE.toString();
    public static final String DARK_RED = ChatColor.DARK_RED.toString();
    public static final String GOLD = ChatColor.GOLD.toString();
    public static final String GRAY = ChatColor.GRAY.toString();
    public static final String GREEN = ChatColor.GREEN.toString();
    public static final String ITALIC = ChatColor.ITALIC.toString();
    public static final String LIGHT_PURPLE = ChatColor.LIGHT_PURPLE.toString();
    public static final String MAGIC = ChatColor.MAGIC.toString();
    public static final ChatColor ORANGE = ChatColor.of(new Color(255, 128, 0));
    public static final String RED = ChatColor.RED.toString();
    public static final String RESET = ChatColor.RESET.toString();
    public static final String STRIKETHROUGH = ChatColor.STRIKETHROUGH.toString();
    public static final String UNDERLINE = ChatColor.UNDERLINE.toString();
    public static final String WHITE = ChatColor.WHITE.toString();
    public static final String YELLOW = ChatColor.YELLOW.toString();

    public static String addColor(String message) {
        if (message == null) return null;
        message = ChatColor.translateAlternateColorCodes('&', message);
        Matcher matcher = HEX_PATTERN.matcher(message);
        while (matcher.find()) {
            final ChatColor hexColor = ChatColor.of(matcher.group().substring(1, matcher.group().length() - 1));
            final String before = message.substring(0, matcher.start());
            final String after = message.substring(matcher.end());
            message = before + hexColor + after;
            matcher = HEX_PATTERN.matcher(message);
        }
        return message;
    }

    public static List<String> addColor(List<String> list) {
        if (list == null) return null;
        List<String> result = new ArrayList<>(0);
        for (String line: list) {
            result.add(addColor(line));
        }
        return result;
    }

    public static String[] addColor(String[] array) {
        if (array == null) return null;
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = addColor(array[i]);
        }
        return result;
    }

    public static String stripColor(String message) {
        if (message == null) return null;
        return ChatColor.stripColor(CCUtils.addColor(message));
    }

    public static ChatColor[] getFade(Color from, Color to, int count) {
        ChatColor[] fade = new ChatColor[count];
        float redIncrement = (float) (to.getRed() - from.getRed()) / (count - 1);
        float greenIncrement = (float) (to.getGreen() - from.getGreen()) / (count - 1);
        float blueIncrement = (float) (to.getBlue() - from.getBlue()) / (count - 1);
        for (int i = 0; i < fade.length; i++) {
            int red = (int) (from.getRed() + (redIncrement * i));
            int green = (int) (from.getGreen() + (greenIncrement * i));
            int blue = (int) (from.getBlue() + (blueIncrement * i));
            fade[i] = ChatColor.of(new Color(red, green, blue));
        }
        return fade;
    }

    public static String applyFade(String message, ChatColor from, ChatColor to) {
        message = CCUtils.stripColor(message);
        ChatColor[] fade = getFade(from.getColor(), to.getColor(), message.length());
        assert fade.length == message.length();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            char toColor = message.charAt(i);
            if (Character.isSpaceChar(toColor)) {
                result.append(" ");
            } else {
                result.append(fade[i]).append(message.charAt(i));
            }
        }
        return result.toString();
    }

}
