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
import java.util.List;

public abstract class ComponentManager extends JavaPlugin {

    private boolean acceptingNew = true;
    private List<Component> components = new ArrayList<>();
    private static final String FILE_NAME = "components.yml";

    @Override
    public void onEnable() {
        registerComponents();
        acceptingNew = false;
        new DependencyProcessor().processDependencies();
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
            if (getComponent(component.getClass()) == null) {
                components.add(component);
            } else {
                Log.warning("Attempted to register duplicate component: " + component.getClass().getName());
            }
        } else {
            throw new IllegalStateException("Components must only be registered in registerComponents() implementation");
        }
    }

    private <T extends Component> T getComponent(Class<T> clazz) {
        if (clazz != null) {
            for (Component component : components) {
                if (component.getClass() == clazz) {
                    return (T) component;
                }
            }
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
        return components;
    }

    protected List<Component> getComponentsReversed() {
        List<Component> components = new ArrayList<>(getComponents());
        Collections.reverse(components);
        return components;
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
                    Log.warning("Failed to enable component: " + id + " (" + component.getClass().getName() + ")");
                    Log.error(component, e);
                    continue;
                }
                if (component.enabled && component instanceof Listener) {
                    try {
                        Bukkit.getPluginManager().registerEvents((Listener) component, this);
                    } catch (Exception e) {
                        Log.warning("Failed to register Component as listener: " + id);
                        Log.error(component, e);
                    }
                }
            }
        }

        try {
            config.save(file);
        } catch (IOException e) {
            Log.error(e);
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
                Log.warning("An error occurred while disabling a component: " + component.getClass().getName());
                Log.error(e);
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
                Log.warning("Failed to save component: " + component.getId());
                Log.error(component, e);
                return false;
            }
            long timeTaken = System.currentTimeMillis() - before;
            if (timeTaken >= 5) {
                Log.info("Saved component: " + component.getId() + " (" + Formatter.formatLong((System.currentTimeMillis() - before)) + "ms)");
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
                Log.warning("Failed to reset component: " + component.getId());
                Log.error(component, e);
            }
        }
    }

    private void startAutoSave() {
        int saveDelayInMinutes = 2;
        long saveDelay = saveDelayInMinutes * 60 * 20; // ticks
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::saveAll, saveDelay, saveDelay);
    }

    public class DependencyProcessor {

        private final List<Component> ordered = new ArrayList<>();
        private final List<Component> hardDependencyMissing = new ArrayList<>();

        public void processDependencies() {
            for (Component component: components) {
                addComponentAndDependencies(component);
            }
            components = Collections.unmodifiableList(ordered);
            StringBuilder componentOrder = new StringBuilder();
            ordered.forEach(component -> componentOrder.append(component.getId()).append(", "));
            Log.info(componentOrder.toString());
        }

        public void addComponentAndDependencies(Component component) {
            if (!ordered.contains(component) && !hardDependencyMissing.contains(component)) {
                List<Class<? extends Component>> hardDependencies = component.getDependencies(true);
                if (hardDependencies != null) {
                    for (Class<? extends Component> dependencyClass : hardDependencies) {
                        Component dependency = getComponent(dependencyClass);
                        if (dependency != null) {
                            addComponentAndDependencies(dependency);
                        } else {
                            Log.warning("Hard dependency missing! " + component.getClass().getName() + " -> " + dependencyClass.getName());
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
                            Log.warning("Soft dependency missing! " + component.getClass().getName() + " -> " + dependencyClass.getName());
                        }
                    }
                }
                ordered.add(component);
            }
        }

    }

}
