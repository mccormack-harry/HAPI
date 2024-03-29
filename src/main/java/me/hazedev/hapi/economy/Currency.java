package me.hazedev.hapi.economy;

import me.hazedev.hapi.chat.Formatter;
import me.hazedev.hapi.event.BalanceChangeEvent;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class Currency implements Economy {

    private final String id;
    private final Map<UUID, Double> balanceMap = new HashMap<>();
    private boolean enabled = false;

    protected Currency(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    final void setBalanceFromFile(UUID uniqueId, double amount) {
        balanceMap.putIfAbsent(uniqueId, amount);
    }

    final Map<UUID, Double> getBalanceMap() {
        return Collections.unmodifiableMap(balanceMap);
    }

    final void setEnabled() {
        this.enabled = true;
    }

    @Override
    public final boolean isEnabled() {
        return enabled;
    }

    public Map<UUID, Double> getTop(int amount) {
        List<Map.Entry<UUID, Double>> entryList = new ArrayList<>(balanceMap.entrySet());
        entryList.sort(Map.Entry.comparingByValue());
        Collections.reverse(entryList); // Order highest -> lowest
        entryList.removeIf(entry -> entry.getKey().toString().equals("7e6258e2-938b-4c95-a75d-b476f38e6a18")); // TODO hide based on permissions ??

        Map<UUID, Double> top = new LinkedHashMap<>();
        for (Map.Entry<UUID, Double> entry : entryList) {
            if (top.size() == amount) break;
            top.put(entry.getKey(), entry.getValue());
        }
        return top;
    }

    @Override
    public String getName() {
        return WordUtils.capitalizeFully(id);
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    public String format(double v, boolean isShort) {
        if (isShort) {
            return Formatter.formatShort(v);
        } else {
            return Formatter.formatLong(v);
        }
    }

    @Override @Deprecated
    public String format(double v) {
        return format(v, false);
    }

    public final boolean hasAccount(UUID uniqueId) {
        return balanceMap.containsKey(uniqueId);
    }

    @Override
    public final boolean hasAccount(OfflinePlayer offlinePlayer) {
        return hasAccount(offlinePlayer.getUniqueId());
    }

    public final void set(UUID uniqueId, double v) {
        Double oldBalance = balanceMap.get(uniqueId);
        BalanceChangeEvent event = new BalanceChangeEvent(uniqueId, this, oldBalance != null ? oldBalance : 0, v);
        Bukkit.getPluginManager().callEvent(event);
        balanceMap.put(uniqueId, event.getNewBalance());
    }

    public final void set(OfflinePlayer offlinePlayer, double v) {
        set(offlinePlayer.getUniqueId(), v);
    }

    public String getBalanceFormatted(UUID uniqueId, boolean isShort) {
        return format(getBalance(uniqueId), isShort);
    }

    public String getBalanceFormatted(OfflinePlayer offlinePlayer, boolean isShort) {
        return getBalanceFormatted(offlinePlayer.getUniqueId(), isShort);
    }

    public final double getBalance(UUID uniqueId) {
        return balanceMap.getOrDefault(uniqueId, 0D);
    }

    @Override
    public final double getBalance(OfflinePlayer offlinePlayer) {
        return getBalance(offlinePlayer.getUniqueId());
    }

    public final boolean has(UUID uniqueId, double v) {
        return getBalance(uniqueId) >= v;
    }

    @Override
    public final boolean has(OfflinePlayer offlinePlayer, double v) {
        return has(offlinePlayer.getUniqueId(), v);
    }

    public final EconomyResponse withdrawPlayer(UUID uniqueId, double v) {
        if (has(uniqueId, v)) {
            set(uniqueId, getBalance(uniqueId) - v);
            return new EconomyResponse(v, getBalance(uniqueId), EconomyResponse.ResponseType.SUCCESS, null);
        }
        return new EconomyResponse(0, getBalance(uniqueId), EconomyResponse.ResponseType.FAILURE, "You don't have " + format(v));
    }

    @Override
    public final EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        return withdrawPlayer(offlinePlayer.getUniqueId(), v);
    }

    public final EconomyResponse depositPlayer(UUID uniqueId, double v) {
        set(uniqueId, getBalance(uniqueId) + v);
        return new EconomyResponse(v,getBalance(uniqueId), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public final EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        return depositPlayer(offlinePlayer.getUniqueId(), v);
    }

    @Override
    public final boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        balanceMap.putIfAbsent(offlinePlayer.getUniqueId(), 0D);
        return true;
    }


    // =================================================================================================================
    //
    //                                             UNUSED METHODS BELOW
    //
    // =================================================================================================================

    // World Specific methods

    @Override
    public final boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return hasAccount(offlinePlayer);
    }

    @Override
    public final double getBalance(OfflinePlayer offlinePlayer, String s) {
        return getBalance(offlinePlayer);
    }

    @Override
    public final boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return has(offlinePlayer, v);
    }

    @Override
    public final EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return withdrawPlayer(offlinePlayer, v);
    }

    @Override
    public final EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return depositPlayer(offlinePlayer, v);
    }

    @Override
    public final boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return createPlayerAccount(offlinePlayer);
    }

    // Username methods

    @Override
    public final boolean hasAccount(String s) {
        throw new UnsupportedOperationException("No username support!");
    }

    @Override
    public final boolean hasAccount(String s, String s1) {
        return hasAccount(s);
    }

    @Override
    public final double getBalance(String s) {
        throw new UnsupportedOperationException("No username support!");
    }

    @Override
    public final double getBalance(String s, String s1) {
        return getBalance(s);
    }

    @Override
    public final boolean has(String s, double v) {
        throw new UnsupportedOperationException("No username support!");
    }

    @Override
    public final boolean has(String s, String s1, double v) {
        return has(s, v);
    }

    @Override
    public final EconomyResponse withdrawPlayer(String s, double v) {
        throw new UnsupportedOperationException("No username support!");
    }

    @Override
    public final EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return withdrawPlayer(s, v);
    }

    @Override
    public final EconomyResponse depositPlayer(String s, double v) {
        throw new UnsupportedOperationException("No username support!");
    }

    @Override
    public final EconomyResponse depositPlayer(String s, String s1, double v) {
        return depositPlayer(s, v);
    }

    @Override
    public final boolean createPlayerAccount(String s) {
        throw new UnsupportedOperationException("No username support!");
    }

    @Override
    public final boolean createPlayerAccount(String s, String s1) {
        return createPlayerAccount(s);
    }

    // Bank Methods

    @Override
    public final boolean hasBankSupport() {
        return false;
    }

    @Override
    public final EconomyResponse createBank(String s, String s1) {
        throw new UnsupportedOperationException("No bank support!");
    }

    @Override
    public final EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        throw new UnsupportedOperationException("No bank support!");
    }

    @Override
    public final EconomyResponse deleteBank(String s) {
        throw new UnsupportedOperationException("No bank support!");
    }

    @Override
    public final EconomyResponse bankBalance(String s) {
        throw new UnsupportedOperationException("No bank support!");
    }

    @Override
    public final EconomyResponse bankHas(String s, double v) {
        throw new UnsupportedOperationException("No bank support!");
    }

    @Override
    public final EconomyResponse bankWithdraw(String s, double v) {
        throw new UnsupportedOperationException("No bank support!");    }

    @Override
    public final EconomyResponse bankDeposit(String s, double v) {
        throw new UnsupportedOperationException("No bank support!");    }

    @Override
    public final EconomyResponse isBankOwner(String s, String s1) {
        throw new UnsupportedOperationException("No bank support!");    }

    @Override
    public final EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        throw new UnsupportedOperationException("No bank support!");    }

    @Override
    public final EconomyResponse isBankMember(String s, String s1) {
        throw new UnsupportedOperationException("No bank support!");    }

    @Override
    public final EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        throw new UnsupportedOperationException("No bank support!");    }

    @Override
    public final List<String> getBanks() {
        throw new UnsupportedOperationException("No bank support!");
    }

}
