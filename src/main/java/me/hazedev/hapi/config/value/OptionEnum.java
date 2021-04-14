package me.hazedev.hapi.config.value;

import me.hazedev.hapi.config.YamlOption;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OptionEnum<T extends Enum<T>> extends YamlOption<T> {

    Class<T> enumClazz;

    public OptionEnum(@NotNull String path, @NotNull Class<T> enumClazz, @NotNull T defaultValue) {
        super(path, defaultValue);
        this.enumClazz = enumClazz;
    }

    @Override
    public void saveValue(@NotNull ConfigurationSection configuration) {
        configuration.set(getPath(), get().name());
    }

    @Override
    public void readValue(@NotNull ConfigurationSection config) throws IllegalArgumentException {
        if (config.isString(path)) {
            String value = config.getString(path);
            try {
                setValue(Enum.valueOf(enumClazz, Objects.requireNonNull(value)));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Value is not an enum constant from " + enumClazz.getName());
            }

        } else {
            throw new IllegalArgumentException("Value must be a string");
        }
    }
}
