package me.hazedev.hapi.config.value;

import me.hazedev.hapi.config.YamlOption;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.jetbrains.annotations.NotNull;

public class OptionConfigurationSerializable<T extends ConfigurationSerializable> extends YamlOption<T> {

    private final Class<T> clazz;

    public OptionConfigurationSerializable(@NotNull String path, @NotNull Class<T> clazz, @NotNull T defaultValue) {
        super(path, defaultValue);
        this.clazz = clazz;
    }

    @Override
    public void saveValue(@NotNull ConfigurationSection configuration) {
        configuration.set(path, null);
        configuration.createSection(path, get().serialize());
    }

    @Override
    public void readValue(@NotNull ConfigurationSection config) throws IllegalArgumentException {
        if (config.isConfigurationSection(path)) {
             ConfigurationSection section = config.getConfigurationSection(path);
            ConfigurationSerializable deserialized = ConfigurationSerialization.deserializeObject(section.getValues(true), clazz);
            if (deserialized != null) {
                setValue((T) deserialized);
            } else {
                throw new IllegalArgumentException("Value could not be deserialized to a " + clazz.getName());
            }
        } else {
            throw new IllegalArgumentException("Value must be a configuration section");
        }
    }

}
