package me.hazedev.hapi.component;

import me.hazedev.hapi.io.YamlFileHandler;
import me.hazedev.hapi.logging.Log;
import me.hazedev.hapi.nms.CommandMapUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
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

    @NotNull
    public final ComponentManager getComponentManager() {
        return componentManager;
    }

    @NotNull
    public final ComponentManager getPlugin() {
        return componentManager;
    }

    @NotNull
    public final String getId() {
        return id;
    }

    @NotNull
    public final File getDataFolder() {
        return new File(componentManager.getDataFolder(), getId());
    }

    @NotNull
    public final YamlFileHandler getYamlFileHandler(@NotNull String path) throws IOException {
        if (!path.endsWith(".yml")) path = path + ".yml";
        File file = new File(getDataFolder(), path);
        return new YamlFileHandler(file);
    }

    public final void saveResource(@NotNull String name, boolean replace) throws IllegalArgumentException {
        String path = id + File.separatorChar + name;
        if (replace || !new File(getDataFolder(), name).exists()) {
            componentManager.saveResource(path, replace);
        }
    }

    public final void registerCommand(@NotNull Command command) {
        if (CommandMapUtils.register(this, command)) {
            commands.add(command);
        } else {
            Log.warning(this, "Failed to register command: " + command.getName() + " | class:" + command.getClass().getName());
        }
    }

    public final void registerListener(@NotNull Listener listener) {
        try {
            Bukkit.getPluginManager().registerEvents(listener, getPlugin());
        } catch (Exception e) {
            Log.error(this, e, "Failed to register listener: " + listener.getClass().getName());
            return;
        }
        listeners.add(listener);
    }

    @NotNull
    public final NamespacedKey getNamespacedKey(@NotNull String key) {
        //noinspection deprecation
        return new NamespacedKey(id, key);
    }

    @Nullable
    public final <T extends Component> T verifySoftDependency(@NotNull Class<T> dependencyClazz) {
        return getComponentManager().getComponentIfEnabled(dependencyClazz);
    }

    @NotNull
    public final <T extends Component> T verifyHardDependency(@NotNull Class<T> dependencyClazz) throws MissingDependencyException {
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
    protected boolean onEnable() throws Exception {
        return true;
    }

    protected void onDisable() {}

    protected boolean reload() {
        return true;
    }

    protected void save() {}

    protected void reset() {}

    @Nullable
    protected List<Class<? extends Component>> getDependencies(boolean hard) {
        return null;
    }

}
