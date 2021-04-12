package me.hazedev.hapi.config;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public interface YamlConfigurable {

    @NotNull
    ConfigurationSection getConfiguration();

    @NotNull
    YamlConfigurableFile getRoot();

}
