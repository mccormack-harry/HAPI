package me.hazedev.hapi.config.value;

import me.hazedev.hapi.config.ConfigurableSection;
import me.hazedev.hapi.config.YamlConfigReader;
import me.hazedev.hapi.config.YamlOption;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class OptionListConfigurableSection<T extends ConfigurableSection> extends YamlOption<List<T>> {

    private final ConfigurableSectionCreator<T> sectionCreator;

    public OptionListConfigurableSection(@NotNull String path, @NotNull ConfigurableSectionCreator<T> sectionCreator) {
        super(path, Collections.emptyList());
        super.setValue(new ArrayList<>());
        this.sectionCreator = sectionCreator;
    }

    public OptionListConfigurableSection(@NotNull String path, @NotNull ConfigurableSectionCreator<T> sectionCreator, @NotNull String defaultSection) {
        super(path, Collections.singletonList(sectionCreator.createDefaultInstance(path + "." + defaultSection)));
        super.setValue(new ArrayList<>(getDefaultValue()));
        this.sectionCreator = sectionCreator;
    }

    @Override
    public void setValue(@NotNull List<T> ts) {
        // Prevent setting value
    }

    @Override
    public void saveDefaultValue(@NotNull MemorySection configuration) {
        for (ConfigurableSection section: getDefaultValue()) {
            configuration.createSection(section.getPath(), YamlConfigReader.serialize(section));
        }
    }

    @Override
    public void saveValue(@NotNull ConfigurationSection configuration) {
        for (ConfigurableSection section: get()) {
            configuration.createSection(section.getPath(), YamlConfigReader.serialize(section));
        }
    }

    @Override
    public void readValue(@NotNull ConfigurationSection config) throws IllegalArgumentException {
        if (config.isConfigurationSection(path)) {
            ConfigurationSection listSection = config.getConfigurationSection(path);
            Set<String> keys = listSection.getKeys(false);

            // Remove non existent entries
            get().removeIf(t -> !config.isConfigurationSection(t.getPath()));

            // Update entries
            for (String key: keys) {
                T section = getExistingSection(path + "." + key);
                if (section == null) {
                    section = sectionCreator.createDefaultInstance(path + "." + key);
                    get().add(section);
                }
                YamlConfigReader.read(section);
            }
        }
    }

    @Nullable
    private T getExistingSection(String path) {
        for (T t: get()) {
            if (t.getPath().equals(path)) {
                return t;
            }
        }
        return null;
    }

    @NotNull
    public T createSection(@NotNull String key) {
        T section = sectionCreator.createDefaultInstance(path + "." + key);
        get().add(section);
        return section;
    }

    public interface ConfigurableSectionCreator<T extends ConfigurableSection> {

        @NotNull
        T createDefaultInstance(@NotNull String path);

    }

}
