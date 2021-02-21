package me.hazedev.hapi.io;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Consumer;

import java.io.File;
import java.io.IOException;

public class YamlFileHandler {

    final File file;
    YamlConfiguration configuration;

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

    public void save() throws IOException {
        configuration.save(file);
    }

    public File getFile() {
        return file;
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    public void setDefaults(Consumer<Configuration> defaults) throws IOException {
        defaults.accept(configuration);
        configuration.options().copyDefaults(true);
        configuration.save(file);
    }

}
