package me.hazedev.hapi.chat;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formatter {

    private static final NavigableMap<Long, Character> suffixes = new TreeMap<>();
    private static final Pattern SHORT_PATTERN = Pattern.compile("[0-9]+([.][0-9]+)?[kKmMbBtTqQ]?");

    static {
        suffixes.put(1_000L, 'k');
        suffixes.put(1_000_000L, 'M');
        suffixes.put(1_000_000_000L, 'B');
        suffixes.put(1_000_000_000_000L, 'T');
        suffixes.put(1_000_000_000_000_000L, 'Q');
    }

    public static String formatLong(double amount) {
        if (amount < 0) return "-" + formatLong(-amount);

        long amountAsLong = (long) amount;
        if (amountAsLong == amount) {
            return String.format("%,d", amountAsLong);
        } else {
            return String.format("%,.2f", amount);
        }
    }

    // https://stackoverflow.com/a/30661479
    public static String formatShort(double amount) {

        long value = (long) amount;

        if (value == Long.MIN_VALUE) return formatShort(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + formatShort(-value);
        if (value < 1000) return String.valueOf(value); // Deal with easy case

        Map.Entry<Long, Character> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = String.valueOf(e.getValue());

        long truncated = value / (divideBy / 10); // The number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public static double fromShort(String input) {
        Matcher matcher = SHORT_PATTERN.matcher(input);
        if (matcher.matches()) {
            String formatted = matcher.group();
            int suffixIndex = formatted.length() - 1;
            char suffix = formatted.charAt(suffixIndex);
            long multiplier = 1;
            String doubleStr;
            if (Character.isLetter(suffix)) {
                String suffixStr = String.valueOf(suffix);
                doubleStr = formatted.substring(0, suffixIndex);
                for (Map.Entry<Long, Character> entry : suffixes.entrySet()) {
                    if (suffixStr.equalsIgnoreCase(String.valueOf(entry.getValue()))) {
                        multiplier = entry.getKey();
                        break;
                    }
                }
            } else {
                doubleStr = formatted;
            }
            return Double.parseDouble(doubleStr) * multiplier;
        } else {
            throw new IllegalArgumentException("Cannot interpret numeric value: " + input);
        }
    }

    public static String formatTimeRemaining(long length, int totalUnits) {
        String result = "";
        int units = 0;
        long days = length / (24 * 60 * 60 * 1000);
        if (days > 0) {
            result += days + "d ";
            units++;
        }
        long hours = length / (60 * 60 * 1000) % 24;
        if (hours > 0 && units < totalUnits) {
            result += hours + "h ";
            units++;
        }
        long minutes = length / (60 * 1000) % 60;
        if (minutes > 0 && units < totalUnits) {
            result += minutes + "m ";
            units++;
        }
        double seconds = (double) length / 1000 % 60;
        if (seconds > 0 && units < totalUnits) {
            if (result.isEmpty()) {
                result = String.format("%,.2fs", seconds);
            } else {
                result += (long) seconds + "s ";
            }
        }
        return result.trim();
    }

    public static List<String> splitLines(String lines, int charsPerLine, String defaultLineColor) {
        List<String> result = new ArrayList<>(0);
        String[] words = lines.replace("&r", defaultLineColor).split(" ");
        StringBuilder line = new StringBuilder(defaultLineColor);
        for (String word: words) {
            line.append(word).append(" ");
            if (CCUtils.stripColor(line.toString()).length() >= charsPerLine) {
                result.add(CCUtils.addColor(line.toString()));
                int indexOfLastColor = line.lastIndexOf("&") + 1;
                if (indexOfLastColor > 0 && indexOfLastColor < line.length()) {
                    line = new StringBuilder("&" + line.charAt(indexOfLastColor));
                } else {
                    line = new StringBuilder(defaultLineColor);
                }
            }
        }
        if (line.length() > 0 && !CCUtils.stripColor(line.toString()).isEmpty()) {
            result.add(line.toString());
        }
        return result;
    }

}
