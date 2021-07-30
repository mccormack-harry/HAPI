package me.hazedev.hapi.economy.command;

import me.hazedev.hapi.chat.ChatUtils;
import me.hazedev.hapi.economy.AbstractCurrencyManager;
import me.hazedev.hapi.economy.Currency;
import me.hazedev.hapi.player.data.PlayerDataManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EconomyCommandsHandler implements TabExecutor {

    private final AbstractCurrencyManager manager;

    public EconomyCommandsHandler(AbstractCurrencyManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if ("balance".equalsIgnoreCase(command.getName())) {
            if (sender instanceof Player) {
                Currency currency;
                if (args.length > 0) {
                    currency = manager.getCurrency(args[0], true);
                } else {
                    currency = manager.getDefaultCurrency();
                }
                Player player = (Player) sender;
                ChatUtils.sendMessage(player, "&aBalance: " + currency.getBalanceFormatted(player, false));
            } else {
                ChatUtils.sendMessage(sender, "&cOnly players can check their balance!");
            }
        } else if ("balancetop".equalsIgnoreCase(command.getName())) {
            Currency currency;
            if (args.length > 0) {
                currency = manager.getCurrency(args[0], true);
            } else {
                currency = manager.getDefaultCurrency();
            }
            Map<UUID, Double> topMap = currency.getTop(10);
            if (!topMap.isEmpty()) {
                List<String> baltop = new ArrayList<>();
                baltop.add("&a--- &2&lBaltop &a-- &f" + currency.getName() + " &a---");
                PlayerDataManager playerDataManager = manager.verifyHardDependency(PlayerDataManager.class);
                int pos = 1;
                for (Map.Entry<UUID, Double> entry: topMap.entrySet()) {
                    String name = "?";
                    OfflinePlayer player = playerDataManager.getOfflinePlayer(entry.getKey());
                    if (player != null && player.getName() != null) {
                        name = player.getName();
                    }
                    baltop.add("&a" + pos + "&7. &f" + name + "&7: &f" + currency.format(entry.getValue(), true));
                    pos++;
                }
                ChatUtils.sendMessage(sender, baltop);
            } else {
                ChatUtils.sendMessage(sender, "&cNobody has any " + currency.getName());
            }
        } else if ("withdraw".equalsIgnoreCase(command.getName())) {
            ChatUtils.sendMessage(sender, "&cComing soon! for now use &f/token withdraw &cor &f/mine withdraw");
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> currencies = new ArrayList<>();
            for (Currency currency: manager.getCurrencySet()) {
                currencies.add(currency.getId());
            }
            return currencies;
        }
        return null;
    }

}
