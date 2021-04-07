package me.hazedev.hapi.component;

import me.hazedev.hapi.io.YamlFileHandler;
import me.hazedev.hapi.logging.Log;
import me.hazedev.hapi.nms.CommandMapUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class Component {

    ComponentManager componentManager;
    boolean enabled;
    final String id;
    final Set<Listener> listeners = new HashSet<>();
    final Set<Command> commands = new HashSet<>();

    public Component(String id) {
        this.id = id;
    }

    public final ComponentManager getComponentManager() {
        return componentManager;
    }

    public final ComponentManager getPlugin() {
        return componentManager;
    }

    public final String getId() {
        return id;
    }

    public final File getDataFolder() {
        return new File(componentManager.getDataFolder(), getId());
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
            componentManager.saveResource(path, replace);
        }
    }

    public final void registerCommand(Command command) {
        if (CommandMapUtils.register(this, command)) {
            commands.add(command);
        } else {
            Log.warning(this, "Failed to register command: " + command.getName() + " | class:" + command.getClass().getName());
        }
    }

    public final void registerListener(Listener listener) {
        try {
            Bukkit.getPluginManager().registerEvents(listener, getPlugin());
        } catch (Exception e) {
            Log.warning(this, "Failed to register listener: " + listener.getClass().getName());
            Log.error(this, e);
            return;
        }
        listeners.add(listener);
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
