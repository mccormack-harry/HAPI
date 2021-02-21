package me.hazedev.hapi.component;

import me.hazedev.hapi.io.YamlFileHandler;
import me.hazedev.hapi.logging.Log;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Component {

    ComponentManager questManager;
    boolean enabled;
    final String id;
    final Set<Listener> listeners = new HashSet<>(0);

    public Component(String id) {
        this.id = id;
    }

    public final ComponentManager getComponentManager() {
        return questManager;
    }

    public final ComponentManager getPlugin() {
        return questManager;
    }

    public final String getId() {
        return id;
    }

    public final File getDataFolder() {
        return new File(questManager.getDataFolder(), getId());
    }

    public final YamlFileHandler getYamlFileHandler(String path) throws IOException {
        Objects.requireNonNull(path, "path is null");
        if (!path.endsWith(".yml")) path = path + ".yml";
        File file = new File(getDataFolder(), path);
        return new YamlFileHandler(file);
    }

    public final void saveResource(String name, boolean replace) throws IllegalArgumentException {
        String path = id + File.separatorChar + name;
        if (replace || !new File(getDataFolder(), name).exists()) {
            questManager.saveResource(path, replace);
        }
    }

    public final Optional<PluginCommand> getCommand(String name) {
        return Optional.ofNullable(questManager.getCommand(name));
    }

    public final boolean registerListener(Listener listener) {
        try {
            Bukkit.getPluginManager().registerEvents(listener, getPlugin());
        } catch (Exception e) {
            Log.warning("Failed to register listener");
            Log.error(this, e);
            return false;
        }
        listeners.add(listener);
        return true;
    }

    public final NamespacedKey getNamespacedKey(String key) {
        //noinspection deprecation
        return new NamespacedKey(id, key);
    }

    public final <T extends Component> T verifySoftDependency(Class<T> dependencyClazz) {
        return getComponentManager().getComponentIfEnabled(dependencyClazz);
    }

    public final <T extends Component> T verifyHardDependency(Class<T> dependencyClazz) {
        T dependency = verifySoftDependency(dependencyClazz);
        if (dependency != null) {
            return dependency;
        } else {
            throw new MissingDependencyException(dependencyClazz);
        }
    }

    public final boolean isEnabled() {
        return enabled;
    }

    /**
     * @return Whether the component has been successfully enabled
     */
    protected boolean onEnable() {
        return true;
    }

    protected void onDisable() {}

    protected boolean reload() {
        return true;
    }

    protected void save() {}

    protected void reset() {}

    protected List<Class<? extends Component>> getDependencies(boolean hard) {
        return null;
    }

}
