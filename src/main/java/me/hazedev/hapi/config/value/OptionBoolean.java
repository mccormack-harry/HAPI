package me.hazedev.hapi.config.value;

import me.hazedev.hapi.config.YamlOption;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class OptionBoolean extends YamlOption<Boolean> {

    public OptionBoolean(@NotNull String path, @NotNull Boolean defaultValue) {
        super(path, defaultValue);
    }

    @Override
    public void readValue(@NotNull ConfigurationSection config) throws IllegalArgumentException {
        if (config.isBoolean(path)) {
            setValue(config.getBoolean(path));
        } else {
            throw new IllegalArgumentException("Value must be true/false");
        }
    }

}
