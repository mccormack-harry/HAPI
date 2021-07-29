package me.hazedev.hapi.config;

import me.hazedev.hapi.logging.Log;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class uses reflection to allow {@link YamlConfigurableFile} classes to easily define it's {@link YamlOption}s
 */
public final class YamlConfigReader {

    private static final Map<YamlConfigurable, Set<YamlOption<?>>> CONFIGURABLE_CACHE = new HashMap<>();

    public static void clearCache() {
        CONFIGURABLE_CACHE.clear();
    }

    public static void clearCache(@NotNull YamlConfigurable section) {
        CONFIGURABLE_CACHE.remove(section);
    }

    /**
     * Uses reflection to get all YamlOptions of a configurable
     *
     * @param configurable The configurable
     * @return The YamlOptions of the configurable class (Uses cache for performance)
     */
    public static Set<YamlOption<?>> getYamlOptions(@NotNull YamlConfigurable configurable) {
        Set<YamlOption<?>> result = CONFIGURABLE_CACHE.get(configurable);

        if (result == null) {
            result = new HashSet<>();

            Field[] fields = configurable.getClass().getFields();
            for (Field field : fields) {
                if (YamlOption.class.isAssignableFrom(field.getType())) {
                    try {
                        field.setAccessible(true);
                        YamlOption<?> option = (YamlOption<?>) field.get(configurable);
                        if (option != null) result.add(option);
                    } catch (IllegalAccessException e) {
                        Log.error(null, e, "Failed to access YamlOption field: " + field);
                    }
                }
            }

            CONFIGURABLE_CACHE.put(configurable, result);
        }
        return result;
    }

    /**
     * Saves {@link YamlOption}'s default values to the configuration file
     *
     * @param configurableFile The configurable
     */
    public static void saveDefaults(@NotNull YamlConfigurableFile configurableFile) {
        MemoryConfiguration defaults = new MemoryConfiguration();

        for (YamlOption<?> option: getYamlOptions(configurableFile)) {
            option.saveDefaultValue(defaults);
        }

        YamlConfiguration configuration = configurableFile.getConfiguration();
        configuration.setDefaults(defaults);
        configuration.options().copyDefaults(true);
        try {
            configurableFile.saveConfig();
        } catch (IOException e) {
            Log.error(null, e, "Failed to save defaults to config " + configurableFile.getFileName());
        }
    }

    public static void save(@NotNull YamlConfigurableFile configurableFile) {
        YamlConfiguration configuration = configurableFile.getConfiguration();

        for (YamlOption<?> option: getYamlOptions(configurableFile)) {
            option.saveValue(configuration);
        }

        try {
            configurableFile.saveConfig();
        } catch (IOException e) {
            Log.error(null, e, "Failed to save config " + configurableFile.getFileName());
        }
    }

    /**
     * Reads {@link YamlOption}'s values from the configuration file
     *
     * @param configurable The configurable
     */
    public static void read(@NotNull YamlConfigurable configurable) {

        ConfigurationSection configuration = configurable.getConfiguration();
        Set<YamlOption<?>> options = getYamlOptions(configurable);

        for (YamlOption<?> option : options) {
            try {
                option.readValue(configuration);
            } catch (IllegalArgumentException e) {
                Log.error(null, e, "Invalid configuration value in " + configurable.getRoot().getFileName() + "@" + (configurable instanceof ConfigurableSection ? ((ConfigurableSection) configurable).getPath() + "." : "") + option.getPath());
            }
        }
    }

    public static Map<String, Object> serialize(@NotNull YamlConfigurable configurable) {
        MemoryConfiguration serializedConfig = new MemoryConfiguration();
        for (YamlOption<?> option: getYamlOptions(configurable)) {
            option.saveValue(serializedConfig);
        }
        return serializedConfig.getValues(true);
    }

}
