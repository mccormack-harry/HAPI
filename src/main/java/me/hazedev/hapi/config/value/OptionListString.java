package me.hazedev.hapi.config.value;

import me.hazedev.hapi.config.YamlOption;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OptionListString extends YamlOption<List<String>> {

    public OptionListString(@NotNull String path, @NotNull List<String> defaultValue) {
        super(path, defaultValue);
    }

    public OptionListString(@NotNull String path, @NotNull String... defaultValue) {
        super(path, Arrays.asList(defaultValue));
    }

    @Override
    public void readValue(@NotNull ConfigurationSection config) throws IllegalArgumentException {
        if (config.isString(path)) {
            setValue(Collections.singletonList(config.getString(path)));
        } else if (config.isList(path)) {
            setValue(config.getStringList(path));
        } else {
            throw new IllegalArgumentException("Value must be a list of strings");
        }
    }

}
