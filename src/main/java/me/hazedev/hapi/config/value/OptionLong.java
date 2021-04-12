package me.hazedev.hapi.config.value;

import me.hazedev.hapi.config.YamlOption;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class OptionLong extends YamlOption<Long> {

    public OptionLong(@NotNull String path, @NotNull Long defaultValue) {
        super(path, defaultValue);
    }

    @Override
    public void readValue(@NotNull ConfigurationSection config) throws IllegalArgumentException {
        if (config.isLong(path) || config.isInt(path)) {
            setValue(config.getLong(path));
        } else {
            throw new IllegalArgumentException("Value must be an integer (long)");
        }
    }

}
