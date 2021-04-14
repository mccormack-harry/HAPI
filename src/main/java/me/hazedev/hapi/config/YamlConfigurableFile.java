package me.hazedev.hapi.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface YamlConfigurableFile extends YamlConfigurable {

    /**
     * @return The configuration for this class
     */
    @NotNull
    YamlConfiguration getConfiguration();

    /**
     * Used for Logging purposes: To identify which file has config errors
     *
     * @return Configuration file name
     */
    @NotNull
    String getFileName();

    /**
     * Saves the configuration to file
     *
     * @throws IOException if an error occurs while saving
     */
    void saveConfig() throws IOException;

    @NotNull
    @Override
    default YamlConfigurableFile getRoot() {
        return this;
    }

}
