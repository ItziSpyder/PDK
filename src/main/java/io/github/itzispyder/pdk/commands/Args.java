package io.github.itzispyder.pdk.commands;

import java.util.function.Consumer;
import java.util.regex.PatternSyntaxException;

public record Args(String... args) {

    public Arg getAll() {
        return getAll(0);
    }

    public Arg getAll(int beginIndex) {
        String str = "";
        for (int i = beginIndex; i < args.length; i++) {
            str = str.concat(args[i] + " ");
        }
        return new Arg(str.trim());
    }

    public Arg get(int index) {
        if (args.length == 0) {
            return new Arg("");
        }
        return new Arg(args[Math.min(Math.max(index, 0), args.length - 1)]);
    }

    public Arg first() {
        return get(0);
    }

    public Arg last() {
        return get(args.length - 1);
    }

    public boolean match(int index, String arg) {
        if (index < 0 || index >= args.length) {
            return false;
        }
        return get(index).toString().equalsIgnoreCase(arg);
    }

    public void when(int index, String match, Consumer<Arg> action) {
        if (match(index, match)) {
            action.accept(get(index));
        }
    }

    public int getSize() {
        return args.length;
    }

    public boolean isEmpty() {
        return args.length == 0;
    }

    public static class Arg {
        private final String arg;

        public Arg(String arg) {
            this.arg = arg;
        }

        public int toInt() {
            try {
                return Integer.parseInt(arg.replaceAll("[^0-9-+]", ""));
            } catch (NumberFormatException | PatternSyntaxException ex) {
                return 0;
            }
        }

        public long toLong() {
            try {
                return Long.parseLong(arg.replaceAll("[^0-9-+]", ""));
            } catch (NumberFormatException | PatternSyntaxException ex) {
                return 0L;
            }
        }

        public byte toByte() {
            try {
                return Byte.parseByte(arg.replaceAll("[^0-9-+]", ""));
            } catch (NumberFormatException | PatternSyntaxException ex) {
                return 0;
            }
        }

        public short toShort() {
            try {
                return Short.parseShort(arg.replaceAll("[^0-9-+]", ""));
            } catch (NumberFormatException | PatternSyntaxException ex) {
                return 0;
            }
        }

        public double toDouble() {
            try {
                return Double.parseDouble(arg.replaceAll("[^0-9-+e.]", ""));
            } catch (NumberFormatException | PatternSyntaxException ex) {
                return 0.0;
            }
        }

        public float toFloat() {
            try {
                return Float.parseFloat(arg.replaceAll("[^0-9-+e.]", ""));
            } catch (NumberFormatException | PatternSyntaxException ex) {
                return 0.0F;
            }
        }

        public boolean toBool() {
            return Boolean.parseBoolean(arg);
        }

        public char toChar() {
            return arg.isEmpty() ? ' ' : arg.charAt(0);
        }

        @Override
        public String toString() {
            return arg;
        }

        public <T extends Enum<?>> T toEnum(Class<T> enumType, T fallback) {
            for (T constant : enumType.getEnumConstants()) {
                if (arg.equalsIgnoreCase(constant.name())) {
                    return constant;
                }
            }
            return fallback;
        }
    }
}