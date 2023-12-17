package io.github.itzispyder.pdk.utils;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiPredicate;

public final class StringUtils {

    public static String color(String s) {
        return nullable(s).replace('&', '§');
    }

    public static String nullable(String s) {
        return s == null ? "" : s;
    }

    public static boolean matchAll(String str, BiPredicate<String, String> predicate, String... values) {
        for (String value : values) {
            if (!predicate.test(str, value)) {
                return false;
            }
        }
        return true;
    }

    public static boolean matchAny(String str, BiPredicate<String, String> predicate, String... values) {
        for (String value : values) {
            if (predicate.test(str, value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * passing insert as null would world as backspace, deleting a character
     */
    public static String insertString(String str, int index, String insert) {
        if (str == null || str.isEmpty()) {
            return insert != null ? insert : "";
        }

        index = Math.max(Math.min(index, str.length()), 0);
        String begin = str.substring(0, Math.max(Math.min(insert != null ? index : index - 1, str.length()), 0));
        String end = str.substring(index);
        return begin + (insert != null ? insert : "") + end;
    }

    public static boolean containsAll(String str, String... values) {
        return matchAll(str, String::contains, values);
    }

    public static boolean containsAny(String str, String... values) {
        return matchAny(str, String::contains, values);
    }

    public static boolean startsWithAll(String str, String... values) {
        return matchAll(str, String::startsWith, values);
    }

    public static boolean startsWithAny(String str, String... values) {
        return matchAny(str, String::startsWith, values);
    }

    public static boolean endsWithAll(String str, String... values) {
        return matchAll(str, String::endsWith, values);
    }

    public static boolean endsWithAny(String str, String... values) {
        return matchAny(str, String::endsWith, values);
    }

    public static String decolor(String s) {
        while (s.length() >= 2 && s.contains("§")) {
            int index = s.indexOf("§");
            s = s.replace(s.substring(index, index + 2), "");
        }
        return s;
    }

    public static String format(String s, Object... args) {
        for (int i = 0; i < args.length; i++) {
            Object obj = args[i];
            String arg = obj == null ? "null" : obj.toString();
            s = s.replace("%" + i, arg);
            s = s.replaceFirst("%s", arg);
        }
        return s.replace("%n", "\n");
    }

    public static String reversed(String s) {
        return new StringBuilder(s).reverse().toString();
    }

    public static String capitalize(String s) {
        if (s.length() <= 1) return s.toUpperCase();
        s = s.toLowerCase();
        return String.valueOf(s.charAt(0)).toUpperCase() + s.substring(1);
    }

    public static String capitalizeWords(String s) {
        s = s.replaceAll("[_-]"," ");
        String[] sArray = s.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String str : sArray) sb.append(capitalize(str)).append(" ");
        return sb.toString().trim();
    }

    public static char nextChar(String s, int index) {
        index ++;
        if (index >= s.length()) return ' ';
        return s.charAt(index);
    }

    public static List<String> wrapLines(String s, int maxLen) {
        return wrapLines(s, maxLen, false);
    }

    public static List<String> wrapLines(String s, int maxLen, boolean wordWrap) {
        final List<String> lines = new ArrayList<>();

        if (s.length() <= maxLen) {
            lines.add(s);
            return lines;
        }

        while (s.length() >= maxLen) {
            if (wordWrap) {
                int wrapAt = maxLen - 1;
                while (nextChar(s, wrapAt) != ' ') {
                    wrapAt ++;
                }
                lines.add(s.substring(0, wrapAt + 1).trim());
                s = s.substring(wrapAt + 1);
            }
            else {
                lines.add(s.substring(0, maxLen).trim());
                s = s.substring(maxLen);
            }
        }
        if (s.length() > 0) {
            lines.add(s.trim());
        }

        return lines;
    }

    public static String getCurrentTimeStamp() {
        LocalDateTime now = LocalDateTime.now();
        String hr = min2placeDigit(now.getHour());
        String min = min2placeDigit(now.getMinute());
        String sec = min2placeDigit(now.getSecond());
        return "%s:%s:%s".formatted(hr, min, sec);
    }

    public static String min2placeDigit(int digit) {
        return (digit >= 0 && digit <= 9 ? "0" : "") + digit;
    }

    public static UUID toUUID(String uuid) {
        uuid = uuid.trim().replace("-", "");
        return new UUID(
                new BigInteger(uuid.substring(0, 16), 16).longValue(),
                new BigInteger(uuid.substring(16), 16).longValue()
        );
    }
}