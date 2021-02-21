package me.hazedev.hapi.economy;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class EconomyPlaceholders extends PlaceholderExpansion {

    private final AbstractCurrencyManager manager;

    public EconomyPlaceholders(AbstractCurrencyManager manager) {
        this.manager = manager;
    }

    @Override
    public String getIdentifier() {
        return "currency";
    }

    @Override
    public String getAuthor() {
        return "haz8989";
    }

    @Override
    public String getVersion() {
        return "1.1";
    }

    @Override
    public boolean canRegister() {
        return !PlaceholderAPI.isRegistered(getIdentifier());
    }

    @Override
    public boolean persist() {
        return true;
    }

    // %currency_<id>%
    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (manager.isEnabled()) {
            if (player != null) {
                String currencyName = params;
                boolean isShort = true;
                if (params.endsWith("_long")) {
                    isShort = false;
                    currencyName = currencyName.replace("_long", "");
                }
                Currency currency = manager.getCurrency(currencyName, false);
                if (currency != null) {
                    return currency.format(currency.getBalance(player), isShort);
                }
            }
        }
        return null;
    }

}
