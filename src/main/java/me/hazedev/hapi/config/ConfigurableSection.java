package me.hazedev.hapi.config;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public abstract class ConfigurableSection implements YamlConfigurable {

    private final YamlConfigurableFile root;
    private final String path;

    public ConfigurableSection(@NotNull YamlConfigurableFile root, @NotNull String path) {
        this.root = root;
        this.path = path;
    }

    @NotNull
    @Override
    public ConfigurationSection getConfiguration() {
        Configuration configuration = getRoot().getConfiguration();
        if (!configuration.isSet(getPath()) || !configuration.isConfigurationSection(getPath())) {
            return configuration.createSection(getPath());
        } else {
            return configuration.getConfigurationSection(getPath());
        }
    }

    @NotNull
    public String getPath() {
        return path;
    }

    @NotNull
    public YamlConfigurableFile getRoot() {
        return root;
    }

}
