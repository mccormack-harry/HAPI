package me.hazedev.hapi.config.value;

import me.hazedev.hapi.config.ConfigurableSection;
import me.hazedev.hapi.config.YamlConfigReader;
import me.hazedev.hapi.config.YamlOption;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class OptionConfigurableSection<T extends ConfigurableSection> extends YamlOption<T> {

    public OptionConfigurableSection(@NotNull T section) {
        super(section.getPath(), section);
    }

    @Override
    public void setValue(@NotNull T t) {
        // Prevent resetting the value
    }

    @Override
    public void saveValue(@NotNull ConfigurationSection configuration) {
        Map<String, Object> serialized = YamlConfigReader.serialize(get());
        configuration.createSection(getPath(), serialized);
    }

    @Override
    public void readValue(@NotNull ConfigurationSection config) throws IllegalArgumentException {
        YamlConfigReader.read(get());
    }
}
