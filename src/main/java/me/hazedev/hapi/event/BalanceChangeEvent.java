package me.hazedev.hapi.event;

import me.hazedev.hapi.economy.Currency;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BalanceChangeEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final UUID uniqueId;
    private final Currency currency;
    private final double oldBalance;
    private double newBalance;

    public BalanceChangeEvent(@NotNull UUID uniqueId, @NotNull Currency currency, double oldBalance, double newBalance) {
        this.uniqueId = uniqueId;
        this.currency = currency;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
    }

    @NotNull
    public UUID getPlayer() {
        return uniqueId;
    }

    @NotNull
    public Currency getCurrency() {
        return currency;
    }

    public double getOldBalance() {
        return oldBalance;
    }

    public double getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(double newBalance) {
        this.newBalance = newBalance;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
