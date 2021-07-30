package me.hazedev.hapi.config.value;

import me.hazedev.hapi.config.YamlOption;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class OptionString extends YamlOption<String> {

    public OptionString(@NotNull String path, @NotNull String defaultValue) {
        super(path, defaultValue);
    }

    @Override
    public void readValue(@NotNull ConfigurationSection config) throws IllegalArgumentException {
        if (config.isString(path)) {
            setValue(config.getString(path));
        } else {
            throw new IllegalArgumentException("Value must be a string");
        }
    }

    @Override
    public String toString() {
        return get();
    }
}
