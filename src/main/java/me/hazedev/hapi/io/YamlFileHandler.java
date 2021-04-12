package me.hazedev.hapi.io;

import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Consumer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class YamlFileHandler {

    protected final File file;
    protected YamlConfiguration configuration;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public YamlFileHandler(File yamlFile) throws IOException {
        this.file = yamlFile;
        if (!yamlFile.exists()) {
            yamlFile.getParentFile().mkdirs();
            yamlFile.createNewFile();
        }
        reloadConfig();
    }

    public void reloadConfig() {
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void saveConfig() throws IOException {
        configuration.save(file);
    }

    public File getFile() {
        return file;
    }

    @NotNull
    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    public void setDefaults(Consumer<MemoryConfiguration> defaults) throws IOException {
        MemoryConfiguration defaultsSection = new MemoryConfiguration();
        defaults.accept(defaultsSection);
        configuration.addDefaults(defaultsSection);
        configuration.options().copyDefaults(true);
        configuration.save(file);
    }

}
