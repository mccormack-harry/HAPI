package me.hazedev.hapi.validation;

import me.hazedev.hapi.chat.Formatter;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Validators {

    // Default (String, No validation applied)
    public static final Validator<String> STRING = new Validator<String>() {
        @Override
        public String validateValue(String value) throws IllegalArgumentException {
            return value;
        }

        @Override
        public List<String> getPossibleValues(String search) {
            return new ArrayList<>(0);
        }
    };

    // org.bukkit.entity.Player
    public static final Validator<Player> PLAYER = new Validator<Player>() {

        @Override
        public Player validateValue(String value) throws IllegalArgumentException {
                if (!value.isEmpty()) {
                    Player player;
                    player = Bukkit.getPlayer(value);
                    if (player != null) {
                        return player;
                    }
                    try {
                        UUID uuid = UUID.fromString(value);
                        player = Bukkit.getPlayer(uuid);
                        if (player != null) {
                            return player;
                        }
                    } catch (IllegalArgumentException ignored) {}
                }
                throw new IllegalArgumentException("Invalid player: &f" + value);
        }

        @Override
        public List<String> getPossibleValues(String search) {
            List<String> result = new ArrayList<>(0);
            Bukkit.getOnlinePlayers().forEach(player -> {
                String name = player.getName();
                if (name.toLowerCase().startsWith(search.toLowerCase())) {
                    result.add(name);
                }
            });
            return result;
        }
    };

    // Boolean
    public static final Validator<Boolean> BOOLEAN = new Validator<Boolean>() {
        @Override
        public Boolean validateValue(String value) throws IllegalArgumentException {
            if ("true".equalsIgnoreCase(value) || "t".equalsIgnoreCase(value)) {
                return true;
            } else if ("false".equalsIgnoreCase(value) || "f".equalsIgnoreCase(value)) {
                return false;
            } else {
                try {
                    int intValue = INTEGER.validateValue(value);
                    return intValue != 0;
                } catch (IllegalArgumentException ignored) {}
            }
            throw new IllegalArgumentException(value + " must be true/false");
        }

        @Override
        public List<String> getPossibleValues(String search) {
            List<String> result = new ArrayList<>(0);
            String[] possibles = new String[]{"true", "false", "t", "f", "1", "0"};
            if (search.isEmpty()) {
                result.addAll(Arrays.asList(possibles));
            } else {
                for (String possible: possibles) {
                    if (possible.toLowerCase().startsWith(search.toLowerCase())) {
                        result.add(possible);
                    }
                }
            }
            return result;
        }
    };

    // Enum Constants
    public static <T extends Enum<T>> Validator<T> enumValidator(Class<T> e) {
        return new Validator<T>() {
            @Override
            public T validateValue(String value) throws IllegalArgumentException {
                return Enum.valueOf(e, value.toUpperCase());
            }

            @Override
            public List<String> getPossibleValues(String search) {
                List<String> result = new ArrayList<>(0);
                for (T value: e.getEnumConstants()) {
                    String name = value.name();
                    if (name.startsWith(search.toUpperCase())) {
                        result.add(WordUtils.capitalizeFully(name));
                    }
                }
                return result;
            }
        };
    }

    // Numbers
    public interface NumberValidator<T extends Number> extends Validator<T> {

        @Override
        default List<String> getPossibleValues(String search) {
            return new ArrayList<>(0);
        }

        static int validateInt(String value) {
            if (NumberUtils.isNumber(value)) {
                try {
                    return NumberUtils.createInteger(value);
                } catch (NumberFormatException ignored) {}
            }
            return (int) Formatter.fromShort(value);
        }

        static long validateLong(String value) {
            if (NumberUtils.isNumber(value)) {
                try {
                    return NumberUtils.createLong(value);
                } catch (NumberFormatException ignored) {}
            }
            return (long) Formatter.fromShort(value);
        }

        static float validateFloat(String value) {
            if (NumberUtils.isNumber(value)) {
                try {
                    return NumberUtils.createFloat(value);
                } catch (NumberFormatException ignored) {
                }
            }
            return (float) Formatter.fromShort(value);
        }

        static double validateDouble(String value) {
            if (NumberUtils.isNumber(value)) {
                try {
                    return NumberUtils.createDouble(value);
                } catch (NumberFormatException ignored) {
                }
            }
            return Formatter.fromShort(value);
        }

    }
    // Integer
    public static final NumberValidator<Integer> INTEGER = NumberValidator::validateInt;
    public static final NumberValidator<Integer> POSITIVE_INTEGER = value -> Math.abs(NumberValidator.validateInt(value));
    public static NumberValidator<Integer> integerRange(int min, int max) {
        return value -> {
            Integer integer = NumberValidator.validateInt(value);
            if (integer < min) {
                throw new IllegalArgumentException("Number below minimum (" + min + "): " + value);
            } else if (integer > max) {
                throw new IllegalArgumentException("Number above maximum (" + max + "): " + value);
            }
            return integer;
        };
    }
    // Long
    public static final NumberValidator<Long> LONG = NumberValidator::validateLong;
    public static final NumberValidator<Long> POSITIVE_LONG = value -> Math.abs(NumberValidator.validateLong(value));
    public static NumberValidator<Long> longRange(long min, long max) {
        return value -> {
            Long number = NumberValidator.validateLong(value);
            if (number < min) {
                throw new IllegalArgumentException("Number below minimum (" + min + "): " + number);
            } else if (number > max) {
                throw new IllegalArgumentException("Number above maximum (" + max + "): " + number);
            }
            return number;
        };
    }
    // Float
    public static final NumberValidator<Float> FLOAT = NumberValidator::validateFloat;
    public static final NumberValidator<Float> POSITIVE_FLOAT = value -> Math.abs(NumberValidator.validateFloat(value));
    public static NumberValidator<Float> floatRange(float min, float max) {
        return value -> {
            Float number = NumberValidator.validateFloat(value);
            if (number < min) {
                throw new IllegalArgumentException("Number below minimum (" + min + "): " + number);
            } else if (number > max) {
                throw new IllegalArgumentException("Number above maximum (" + max + "): " + number);
            }
            return number;
        };
    }
    // Double
    public static final NumberValidator<Double> DOUBLE = NumberValidator::validateDouble;
    public static final NumberValidator<Double> POSITIVE_DOUBLE = value -> Math.abs(NumberValidator.validateDouble(value));
    public static NumberValidator<Double> doubleRange(double min, double max) {
        return value -> {
            Double number = NumberValidator.validateDouble(value);
            if (number < min) {
                throw new IllegalArgumentException("Number below minimum (" + min + "): " + number);
            } else if (number > max) {
                throw new IllegalArgumentException("Number above maximum (" + max + "): " + number);
            }
            return number;
        };
    }

    
}
