package me.hazedev.hapi.stats;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.hazedev.hapi.component.Component;
import me.hazedev.hapi.logging.Log;
import me.hazedev.hapi.player.data.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractStatisticManager extends Component {

    private PlayerDataManager playerDataManager;
    private final List<Statistic<?>> statistics = new ArrayList<>();

    public AbstractStatisticManager() {
        super("statistics");
    }

    @Override
    protected boolean onEnable() throws Exception {
        playerDataManager = verifyHardDependency(PlayerDataManager.class);
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
        statistic.load(playerDataManager);
    }

    public void saveAll() {
        statistics.forEach(this::save);
    }

    public void save(Statistic<?> statistic) {
        statistic.save(playerDataManager);
    }

    public void resetAll() {
        statistics.forEach(this::reset);
    }

    public void reset(Statistic<?> statistic) {
        if (statistic.isPermanent()) return;
        statistic.reset(playerDataManager);
    }

    private void registerListeners() {
        for (Statistic<?> statistic: statistics) {
            if (statistic instanceof Listener) {
                try {
                    registerListener((Listener) statistic);
                } catch(Exception e) {
                    Log.error(this, e, "Failed to register listener for " + statistic.getId());
                }
            }
        }
    }

    private void registerCommands() {
        registerCommand(new CommandStats(this));
        registerCommand(new CommandStat(this));
        registerCommand(new CommandTop(this));
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
            return Collections.singletonList(PlayerDataManager.class);
        } else {
            return null;
        }
    }

    public List<Statistic<?>> getStatistics() {
        return Collections.unmodifiableList(statistics);
    }
}
