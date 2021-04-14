package me.hazedev.hapi.config.value;

import me.hazedev.hapi.config.YamlOption;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class OptionDouble extends YamlOption<Double> {

    public OptionDouble(@NotNull String path, @NotNull Double defaultValue) {
        super(path, defaultValue);
    }

    @Override
    public void readValue(@NotNull ConfigurationSection config) throws IllegalArgumentException {
        if (config.isDouble(path) || config.isLong(path) || config.isInt(path)) {
            setValue(config.getDouble(path));
        } else {
            throw new IllegalArgumentException("Value must be a decimal number");
        }
    }

}
