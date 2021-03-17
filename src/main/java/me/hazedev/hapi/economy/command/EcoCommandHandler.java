package me.hazedev.hapi.economy.command;

import me.hazedev.hapi.chat.ChatUtils;
import me.hazedev.hapi.command.Argument;
import me.hazedev.hapi.command.CommandHandler;
import me.hazedev.hapi.command.SubCommand;
import me.hazedev.hapi.economy.AbstractCurrencyManager;
import me.hazedev.hapi.economy.Currency;
import me.hazedev.hapi.validation.Validator;
import me.hazedev.hapi.validation.Validators;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EcoCommandHandler extends CommandHandler {

    private final AbstractCurrencyManager manager;
    private final CurrencyValidator currencyValidator;

    public EcoCommandHandler(AbstractCurrencyManager manager) {
        super("eco");
        this.manager = manager;
        currencyValidator = new CurrencyValidator();
        registerSubCommand(new Give());
        registerSubCommand(new Take());
        registerSubCommand(new Set());
        registerSubCommand(new Get());
    }

    private class Give extends SubCommand {

        public Give() {
            super("give", null, new Argument[]{new Argument<>(Validators.PLAYER, "player"), new Argument<>(currencyValidator, "currency"), new Argument<>(Validators.POSITIVE_LONG, "amount")}, null);
        }

        @Override
        public void onCommand(CommandSender sender, String label, List<String> args, List<String> flags) {
            Player player = Validators.PLAYER.validateValue(args.get(0));
            Currency currency = currencyValidator.validateValue(args.get(1));
            long amount = Validators.POSITIVE_LONG.validateValue(args.get(2));
            currency.depositPlayer(player, amount);
            String strAmount = currency.format(amount, true);
            ChatUtils.sendMessage(sender, "&aGiven &f" + player.getName() + " " + strAmount);
            ChatUtils.sendMessage(player, "&a+&f" + strAmount);
        }
    }

    private class Take extends SubCommand {
        public Take() {
            super("take", null, new Argument[]{new Argument<>(Validators.PLAYER, "player"), new Argument<>(currencyValidator, "currency"), new Argument<>(Validators.POSITIVE_LONG, "amount")}, null);
        }

        @Override
        public void onCommand(CommandSender sender, String label, List<String> args, List<String> flags) {
            Player player = Validators.PLAYER.validateValue(args.get(0));
            Currency currency = currencyValidator.validateValue(args.get(1));
            long amount = Validators.POSITIVE_LONG.validateValue(args.get(2));
            currency.withdrawPlayer(player, amount);
            String strAmount = currency.format(amount, true);
            ChatUtils.sendMessage(sender, "&aTaken &f" + strAmount + " &afrom &f" + player.getName());
            ChatUtils.sendMessage(player, "&c-&f" + strAmount);
        }
    }

    private class Set extends SubCommand {

        public Set() {
            super("set", null, new Argument[]{new Argument<>(Validators.PLAYER, "player"), new Argument<>(currencyValidator, "currency"), new Argument<>(Validators.POSITIVE_LONG, "amount")}, null);
        }

        @Override
        public void onCommand(CommandSender sender, String label, List<String> args, List<String> flags) {
            Player player = Validators.PLAYER.validateValue(args.get(0));
            Currency currency = currencyValidator.validateValue(args.get(1));
            long amount = Validators.POSITIVE_LONG.validateValue(args.get(2));
            currency.set(player, amount);
            String strAmount = currency.format(amount, true);
            ChatUtils.sendMessage(sender, "&f" + player.getName() + " &anow has " + strAmount);
            ChatUtils.sendMessage(player, "&a+&f" + strAmount);
        }
    }

    private class Get extends SubCommand {

        public Get() {
            super("set", null, new Argument[]{new Argument<>(Validators.PLAYER, "player"), new Argument<>(currencyValidator, "currency")}, null);
        }

        @Override
        public void onCommand(CommandSender sender, String label, List<String> args, List<String> flags) {
            Player player = Validators.PLAYER.validateValue(args.get(0));
            Currency currency = currencyValidator.validateValue(args.get(1));
            String strAmount = currency.getBalanceFormatted(player, false);
            ChatUtils.sendMessage(sender, "&f" + player.getName() + " &ahas " + strAmount);
        }
    }

    private class CurrencyValidator implements Validator<Currency> {

        @Override
        public Currency validateValue(String value) throws IllegalArgumentException {
            return manager.getCurrency(value, true);
        }

        @Override
        public List<String> getPossibleValues(String search) {
            List<String> result = new ArrayList<>();
            for (Currency currency: manager.getCurrencySet()) {
                if (currency.getId().startsWith(search)) {
                    result.add(currency.getId());
                }
            }
            return result;
        }

    }

}
