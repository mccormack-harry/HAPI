package me.hazedev.hapi.economy;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.hazedev.hapi.command.CommandDelegator;
import me.hazedev.hapi.component.Component;
import me.hazedev.hapi.economy.command.EcoCommandHandler;
import me.hazedev.hapi.economy.command.EconomyCommandsHandler;
import me.hazedev.hapi.player.data.PlayerData;
import me.hazedev.hapi.player.data.PlayerDataManager;
import me.hazedev.hapi.player.data.property.DoubleProperty;
import me.hazedev.hapi.player.data.property.Property;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractCurrencyManager extends Component {

    private final Set<Currency> currencySet = new HashSet<>();
    private Currency defaultCurrency;

    public AbstractCurrencyManager() {
        super("economy");
    }

    @Override
    protected final boolean onEnable() throws Exception {
        registerCurrencies();
        PlayerDataManager playerDataManager = verifyHardDependency(PlayerDataManager.class);
        currencySet.forEach(currency -> {
            Property<Double> currencyProperty = new DoubleProperty(currency.getId());
            playerDataManager.getAllPlayerData().forEach(playerdata -> {
                currency.setBalanceFromFile(playerdata.getProperty(PlayerData.UNIQUE_ID), playerdata.getProperty(getId(), currencyProperty, 0D));
            });
        });
        currencySet.forEach(Currency::setEnabled);
        registerPlaceholders();
        registerCommand(new EcoCommandHandler(this));
        EconomyCommandsHandler commandHandler = new EconomyCommandsHandler(this);
        registerCommand(new CommandDelegator(commandHandler, "balance", "Check your balances", "/balance <currency>", Collections.singletonList("bal")));
        registerCommand(new CommandDelegator(commandHandler, "baltop", "Balance leaderboard", "/baltop <currency>", null));
        return true;
    }

    @Override
    protected void save() {
        PlayerDataManager playerDataManager = verifyHardDependency(PlayerDataManager.class);
        currencySet.forEach(currency -> {
            Property<Double> currencyProperty = new DoubleProperty(currency.getId());
            playerDataManager.getAllPlayerData().forEach(playerdata -> {
                playerdata.setProperty(getId(), currencyProperty, currency.getBalance(playerdata.getProperty(PlayerData.UNIQUE_ID)));
            });
        });
    }

    @Override
    protected void reset() {
        PlayerDataManager playerDataManager = verifyHardDependency(PlayerDataManager.class);
        playerDataManager.getAllPlayerData().forEach(playerdata -> {
            playerdata.unsetProperty(getId());
        });
    }

    @Override
    protected void onDisable() {
        defaultCurrency = null;
        currencySet.clear();
    }

    public abstract void registerCurrencies();

    public final void registerCurrency(@NotNull Currency currency) {
        currencySet.add(currency);
    }

    public final void registerDefaultCurrency(@NotNull Currency currency) {
        registerCurrency(currency);
        if (defaultCurrency == null) {
            defaultCurrency = currency;
        } else {
            throw new IllegalStateException("Cannot have multiple default currencies");
        }
    }

    public final void registerPlaceholders() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            PlaceholderExpansion currencyPlaceholders = new EconomyPlaceholders(this);
            currencyPlaceholders.register();
        }
    }

    public Set<Currency> getCurrencySet() {
        return Collections.unmodifiableSet(currencySet);
    }

    public Currency getCurrency(@NotNull String id, boolean useDefault) {
        for (Currency currency: currencySet) {
            if (id.equals(currency.getId())) {
                return currency;
            }
        }
        return useDefault ? getDefaultCurrency() : null;
    }

    public Currency getDefaultCurrency() {
        return defaultCurrency;
    }

    @Override
    protected List<Class<? extends Component>> getDependencies(boolean hard) {
        if (hard) {
            return Collections.singletonList(PlayerDataManager.class);
        } else {
            return null;
        }
    }
}
