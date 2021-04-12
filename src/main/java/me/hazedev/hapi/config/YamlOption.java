package me.hazedev.hapi.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.jetbrains.annotations.NotNull;

public abstract class YamlOption<T> {

    public final String path;
    private final T defaultValue;
    private T value;


    public YamlOption(@NotNull String path, @NotNull T defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    @NotNull
    public String getPath() {
        return path;
    }

    @NotNull
    public T getDefaultValue() {
        return defaultValue;
    }

    @NotNull
    public T get() {
        return value;
    }

    public void setValue(@NotNull T t) {
        value = t;
    }

    public void saveDefaultValue(@NotNull MemorySection configuration) {
        configuration.set(path, defaultValue);
    }

    public void saveValue(@NotNull ConfigurationSection configuration) {
        configuration.set(path, value);
    }

    public abstract void readValue(@NotNull ConfigurationSection config) throws IllegalArgumentException;

}
