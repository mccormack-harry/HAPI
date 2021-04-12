package me.hazedev.hapi.config.value;

import me.hazedev.hapi.config.YamlOption;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class OptionInteger extends YamlOption<Integer> {

    public OptionInteger(@NotNull String path, @NotNull Integer defaultValue) {
        super(path, defaultValue);
    }

    @Override
    public void readValue(@NotNull ConfigurationSection config) throws IllegalArgumentException {
        if (config.isInt(path)) {
            setValue(config.getInt(path));
        } else {
            throw new IllegalArgumentException("Value must be an integer");
        }
    }

}
