package me.hazedev.hapi.component;

import me.hazedev.hapi.chat.Formatter;
import me.hazedev.hapi.logging.Log;
import me.hazedev.hapi.nms.CommandMapUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class ComponentManager extends JavaPlugin {

    private static final String FILE_NAME = "components.yml";

    private boolean acceptingNew = true;
    private final Map<Class<? extends Component>, Component> components = Collections.synchronizedMap(new LinkedHashMap<>());
    private List<Component> ordered;

    @Override
    public void onEnable() {
        registerComponents();
        acceptingNew = false;
        ordered = Collections.unmodifiableList(new DependencyProcessor().processDependencies());
        enableComponents();
        checkReset();
        startAutoSave();
        CommandMapUtils.register(this, new ComponentCommandHandler(this));
    }

    @Override
    public void onDisable() {
        disableAll();
    }

    protected abstract void registerComponents();

    protected void registerComponent(@NotNull Component component) {
        if (acceptingNew) {
            Class<? extends Component> clazz = component.getClass();
            if (!components.containsKey(clazz)) {
                components.put(clazz, component);
            } else {
                Log.warning(component, "Component is already registered");
            }
        } else {
            throw new IllegalStateException("Components must only be registered in ComponentManager#registerComponents()");
        }
    }

    private <T extends Component> T getComponent(Class<T> clazz) {
        if (clazz != null) {
            return clazz.cast(components.get(clazz));
        }
        return null;
    }

    <T extends Component> T getComponentIfEnabled(Class<T> clazz) {
        T component = getComponent(clazz);
        if (component != null && component.isEnabled()) {
            return component;
        } else {
            return null;
        }
    }

    protected List<Component> getComponents() {
        return ordered;
    }

    protected List<Component> getComponentsReversed() {
        List<Component> components = new ArrayList<>(ordered);
        Collections.reverse(components);
        return Collections.unmodifiableList(components);
    }

    private void enableComponents() {
        File file = new File(getDataFolder(), FILE_NAME);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (Component component : getComponents()) {
            String id = component.getId();
            if (config.getBoolean(id, true)) { // If is enabled
                config.set(id, true);
                component.componentManager = this;
                try {
                    component.enabled = component.onEnable();
                } catch (Exception e) {
                    Log.error(component, e, "Failed to enable component");
                    continue;
                }
                if (component.enabled && component instanceof Listener) {
                    try {
                        Bukkit.getPluginManager().registerEvents((Listener) component, this);
                    } catch (Exception e) {
                        Log.error(component, e, "Failed to register Component as listener");
                    }
                }
            }
        }

        try {
            config.save(file);
        } catch (IOException e) {
            Log.error(null, e, null);
        }
    }

    private void disableAll() {
        getComponentsReversed().forEach(this::disable);
    }

    public void disable(@NotNull Component component) {
        if (component.enabled) {
            save(component);
            try {
                component.onDisable();
            } catch (Exception e) {
                Log.error(component, e, "An error occurred while disabling a component");
            }
            component.enabled = false;
            for (Command command: component.commands) {
                CommandMapUtils.unregister(command);
            }
            if (component instanceof Listener) {
                HandlerList.unregisterAll((Listener) component);
            }
            for (Listener listener: component.listeners) {
                HandlerList.unregisterAll(listener);
            }
            component.listeners.clear();
        }
    }

    public void saveAll() {
        getComponentsReversed().forEach(this::save);
    }

    public boolean save(@NotNull Component component) {
        if (component.enabled) {
            long before = System.currentTimeMillis();
            try {
                component.save();
            } catch (Exception e) {
                Log.error(component, e, "Failed to save component");
                return false;
            }
            long timeTaken = System.currentTimeMillis() - before;
            if (timeTaken >= 5) {
                Log.info(component, "Saved in " + Formatter.formatLong((System.currentTimeMillis() - before)) + "ms");
            }
        }
        return true;
    }

    private void checkReset() {
        if (getConfig().getBoolean("reset", false)) {
            resetAll();
        }
        getConfig().set("reset", false);
        saveConfig();
    }

    private void resetAll() {
        getComponentsReversed().forEach(this::reset);
    }

    private void reset(Component component) {
        if (component.enabled) {
            try {
                component.reset();
            } catch (Exception e) {
                Log.error(component, e, "Failed to reset component");
            }
        }
    }

    private void startAutoSave() {
        int saveDelayInMinutes = 2;
        long saveDelay = saveDelayInMinutes * 60 * 20; // ticks
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::saveAll, saveDelay, saveDelay);
    }

    public class DependencyProcessor {

        private List<Component> ordered = null;
        private final List<Component> hardDependencyMissing = new ArrayList<>();

        public List<Component> processDependencies() {
            if (ordered == null) {
                ordered = new ArrayList<>();
                for (Component component : components.values()) {
                    addComponentAndDependencies(component);
                }
            }
            return ordered;
        }

        private void addComponentAndDependencies(Component component) {
            if (!ordered.contains(component) && !hardDependencyMissing.contains(component)) {
                List<Class<? extends Component>> hardDependencies = component.getDependencies(true);
                if (hardDependencies != null) {
                    for (Class<? extends Component> dependencyClass : hardDependencies) {
                        Component dependency = getComponent(dependencyClass);
                        if (dependency != null) {
                            addComponentAndDependencies(dependency);
                        } else {
                            Log.warning(component, "Hard dependency missing! " + dependencyClass.getName());
                            hardDependencyMissing.add(component);
                            return;
                        }
                    }
                }
                List<Class<? extends Component>> softDependencies = component.getDependencies(false);
                if (softDependencies != null) {
                    for (Class<? extends Component> dependencyClass : softDependencies) {
                        Component dependency = getComponent(dependencyClass);
                        if (dependency != null) {
                            addComponentAndDependencies(dependency);
                        } else {
                            Log.warning(component, "Soft dependency missing! " + dependencyClass.getName());
                        }
                    }
                }
                ordered.add(component);
            }
        }

    }

}
