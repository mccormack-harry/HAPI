package me.hazedev.hapi.stats;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.hazedev.hapi.component.Component;
import me.hazedev.hapi.io.YamlFileHandler;
import me.hazedev.hapi.logging.Log;
import me.hazedev.hapi.userdata.UserDataManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractStatisticManager extends Component {

    private UserDataManager userDataManager;
    private final List<Statistic> statistics = new ArrayList<>();

    public AbstractStatisticManager() {
        super("statistics");
    }

    @Override
    protected boolean onEnable() {
        userDataManager = verifyHardDependency(UserDataManager.class);
        registerStatistics();
        loadAll();
        registerListeners();
        registerCommands();
        registerPlaceholders();
        return true;
    }

    public String getPath() {
        return "stats";
    }

    @Override
    protected void save() {
        saveAll();
    }

    @Override
    protected void reset() {
        resetAll();
    }

    protected abstract void registerStatistics();

    public void registerStatistic(Statistic<?> statistic) {
        statistic.manager = this;
        statistics.add(statistic);
    }

    public Statistic<?> getStatistic(@NotNull String id) {
        for (Statistic<?> statistic: statistics) {
            if (id.equalsIgnoreCase(statistic.getId())) {
                return statistic;
            }
        }
        return null;
    }

    public <T extends Statistic<?>> T getStatistic(@NotNull Class<T> clazz) {
        for (Statistic<?> statistic: statistics) {
            if (statistic.getClass() == clazz) {
                return (T) statistic;
            }
        }
        return null;
    }

    public void loadAll() {
        statistics.forEach(this::load);
    }

    public void load(Statistic<?> statistic) {
        statistic.load(userDataManager);
    }

    private void loadOldStats() {
        for (Statistic<?> statistic: statistics) {
            YamlFileHandler oldFileHandler;
            try {
                oldFileHandler = getYamlFileHandler(statistic.getId() + ".yml");
            } catch (IOException e) {
                Log.warning(this, "Failed to open file for " + statistic.getId());
                Log.error(this, e);
                continue;
            }
            statistic.oldFileHandler = oldFileHandler;
            statistic.loadOld();
        }
    }

    public void saveAll() {
        statistics.forEach(this::save);
    }

    public void save(Statistic<?> statistic) {
        statistic.save(userDataManager);
    }

    public void resetAll() {
        statistics.forEach(this::reset);
    }

    public void reset(Statistic<?> statistic) {
        if (statistic.isPermanent()) return;
        statistic.reset(userDataManager);
    }

    private void registerListeners() {
        for (Statistic<?> statistic: statistics) {
            if (statistic instanceof Listener) {
                try {
                    registerListener((Listener) statistic);
                } catch(Exception e) {
                    Log.warning(this, "Failed to register listener for " + statistic.getId());
                    Log.error(this, e);
                }
            }
        }
    }

    private void registerCommands() {
        getCommand("stats").ifPresent(pluginCommand -> pluginCommand.setExecutor(new StatsCommandHandler(this)));
        getCommand("stat").ifPresent(pluginCommand -> pluginCommand.setExecutor(new StatCommandHandler(this)));
        getCommand("top").ifPresent(pluginCommand -> pluginCommand.setExecutor(new TopCommandHandler(this)));
    }

    private void registerPlaceholders() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            PlaceholderExpansion statisticPlaceholders = new StatisticPlaceholders(this);
            if (statisticPlaceholders.canRegister()) {
                statisticPlaceholders.register();
            }
        }
    }

    @Override
    protected List<Class<? extends Component>> getDependencies(boolean hard) {
        if (hard) {
            return Collections.singletonList(UserDataManager.class);
        } else {
            return null;
        }
    }

    public List<Statistic> getStatistics() {
        return Collections.unmodifiableList(statistics);
    }
}
