package me.hazedev.hapi.command;

import me.hazedev.hapi.chat.ChatUtils;
import me.hazedev.hapi.logging.Log;
import me.hazedev.hapi.validation.Validator;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class CommandHandler extends Command {

    protected Set<SubCommand> subCommands = new HashSet<>(0);
    private final String defaultSubCommand;

    public CommandHandler(@NotNull String name, @NotNull String description, @Nullable List<String> aliases) {
        this(name, description, aliases, null);
    }

    public CommandHandler(@NotNull String name, @NotNull String description, @Nullable List<String> aliases, @Nullable String defaultSubCommand) {
        super(name, description, "/" + name + "help", Collections.emptyList());
        if (defaultSubCommand != null) {
            this.defaultSubCommand = defaultSubCommand;
        } else {
            this.defaultSubCommand = "help";
        }
        registerSubCommand(new Help());
    }



    protected void registerSubCommand(SubCommand subCommand) {
        if (subCommand != null) {
            for (SubCommand registeredCommand : subCommands) {
                if (registeredCommand.getName().equals(subCommand.getName())) {
                    return;
                }
            }
            boolean optionalPresent = false;
            for (Argument argument: subCommand.getArguments()) {
                if (optionalPresent && !argument.isOptional()) {
                    Log.warning(null, "Failed to register subcommand: " + subCommand.getClass().getName());
                } else if (!optionalPresent) {
                    optionalPresent = argument.isOptional();
                }
            }
            subCommands.add(subCommand);
        }
    }

    public SubCommand getSubCommand(String subLabel) {
        for (SubCommand subCommand : subCommands) {
            if (subCommand.getName().equalsIgnoreCase(subLabel)) {
                return subCommand;
            } else {
                String[] aliases = subCommand.getAliases();
                if (aliases != null) {
                    for (String alias : aliases) {
                        if (alias.equalsIgnoreCase(subLabel)) {
                            return subCommand;
                        }
                    }
                }
            }
        }
        return null;
    }

    public boolean hasPermission(CommandSender sender, SubCommand subCommand) {
        return sender.hasPermission(getName().toLowerCase() + "." + subCommand.getName().toLowerCase());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {

        String subLabel = null;
        if (args.length > 0) {
            subLabel = args[0];
        }
        if (subLabel == null) {
            subLabel = defaultSubCommand;
        }

        subLabel = subLabel.toLowerCase();
        SubCommand subCommand = getSubCommand(subLabel);
        if (subCommand != null) {
            if (hasPermission(sender, subCommand)) {
                // Read arguments and flags
                List<String> flags = new ArrayList<>(0);
                List<String> arguments = new ArrayList<>(0);
                for (int i = 1; i < args.length; i++) {
                    String arg = args[i];
                    if (arg.startsWith("-") && StringUtils.isAlpha(arg.substring(1))) {
                        flags.add(arg.toLowerCase());
                    } else {
                        arguments.add(arg);
                    }
                }

                // Check if enough arguments
                Argument<?>[] commandArguments = subCommand.getArguments();
                if (commandArguments != null && commandArguments.length > 0) {
                    int requiredArguments = 0;
                    for (Argument<?> argument : commandArguments) {
                        if (!argument.isOptional()) {
                            requiredArguments++;
                        }
                    }
                    if (arguments.size() < requiredArguments) {
                        ChatUtils.sendMessage(sender, "&cNot enough arguments!");
                        ChatUtils.sendMessage(sender, "&cUsage: &7" + getUsage(subCommand, label, subLabel));
                        return true;
                    }
                }
                // Attempt to run command
                try {
                    subCommand.onCommand(sender, this, subLabel, arguments, flags);
                    return true;
                } catch (IllegalArgumentException e) {
                    ChatUtils.sendMessage(sender, "&c" + e.getMessage());
                    return true;
                }
            } else {
                String customMsg = subCommand.getCustomNoPermissionMessage();
                if (customMsg != null) {
                    ChatUtils.sendMessage(sender, "&c" + customMsg);
                } else {
                    ChatUtils.sendMessage(sender, CommandHelper.NO_PERM);
                }
                return true;
            }
        } else {
            ChatUtils.sendMessage(sender, "&cUnknown command: &7/" + label +  " " + subLabel);
            return true;
        }
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        List<String> result = new ArrayList<>(0);
        if (args.length == 1) { // Suggest sub commands
            for (SubCommand subCommand : subCommands) {
                String name = subCommand.getName();
                if (hasPermission(sender, subCommand)) {
                    if (args[0].isEmpty()) { // Only Command Names
                        result.add(name);
                    } else { // Also Aliases
                        String search = args[0];
                        if (name.toLowerCase().startsWith(search.toLowerCase())) {
                            result.add(name);
                        }
                        for (String alias : subCommand.getAliases()) {
                            if (alias.toLowerCase().startsWith(search.toLowerCase())) {
                                result.add(alias);
                            }
                        }
                    }
                }
            }
        } else { // Argument completion
            // Get command to run
            String subLabel = args[0].toLowerCase();
            SubCommand subCommand = getSubCommand(subLabel);
            // Get possible values
            if (subCommand != null) {
                if (hasPermission(sender, subCommand)) {
                    String search = args[args.length - 1];
                    // Calculate index of current argument
                    int argIndex = -1;
                    for (int i = 1; i < args.length; i++) {
                        String arg = args[i];
                        if (!arg.startsWith("-") || !StringUtils.isAlpha(arg.substring(1))) {
                            argIndex++;
                        }
                    }
                    Argument<?> argument = null;
                    if (argIndex >= 0) {
                        Argument<?>[] arguments = subCommand.getArguments();
                        if (arguments != null && arguments.length > argIndex) {
                            argument = arguments[argIndex];
                            result = subCommand.onTabComplete(sender, Arrays.copyOfRange(args, 1, args.length), argIndex);
                            if (result == null || result.isEmpty()) {
                                Validator<?> validator = argument.getValidator();
                                if (validator != null) {
                                    result = argument.getValidator().getPossibleValues(search);
                                }
                            }
                        }
                    }
                    if (result == null || result.isEmpty()) {
                        result = new ArrayList<>(0);
                        if (argument != null) {
                            result = new ArrayList<>(0);
                            String prompt = argument.isOptional() ? "[" + argument.getPrompt() + "]" : "<" + argument.getPrompt() + ">";
                            result.add(prompt);
                        } else {
                            if (search.startsWith("-")) { // Suggest flags
                                search = search.substring(1);
                                for (Flag flag : subCommand.flags) {
                                    // Check to make sure flag isn't already specified
                                    if (flag.getIdentifier().toLowerCase().startsWith(search.toLowerCase()) && Arrays.stream(args).noneMatch(s -> s.equals("-" + flag.getIdentifier()))) {
                                        result.add("-" + flag.getIdentifier());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public String getUsage(SubCommand subCommand, String masterLabel) {
        return getUsage(subCommand, masterLabel, subCommand.getName());
    }

    public String getUsage(SubCommand subCommand, String masterLabel, String subLabel) {
        StringBuilder usage = new StringBuilder("/" + masterLabel + " " + subLabel + " ");
        for (Argument<?> argument: subCommand.getArguments()) {
            usage.append(argument.isOptional() ? "[" : "<")
                    .append(argument.getPrompt())
                    .append(argument.isOptional() ? "]" : ">")
                    .append(" ");
        }
        return usage.toString();
    }

    public class Help extends SubCommand {

        public Help() {
            super("help", null, new Argument[]{new Argument<>("subcommand", "")}, null);
        }

        @Override
        public void onCommand(CommandSender sender, Command command, String label, List<String> args, List<String> flags) {
            if (args.isEmpty() || args.get(0).isEmpty()) {
                // TODO JSON MESSAGE CLICKABLE
                List<String> help = new ArrayList<>(0);
                help.add("&6Help for /" + command.getName());
                for (SubCommand subCommand: subCommands) {
                    if (subCommand != null && hasPermission(sender, subCommand)) {
                        help.add("&e" + getUsage(subCommand, command.getName()));
                    }
                }
                ChatUtils.sendMessage(sender, help.toArray(new String[0]));
            } else {
                SubCommand subCommand = getSubCommand(args.get(0));
                if (subCommand != null) {
                    ChatUtils.sendMessage(sender, "&6" + getUsage(subCommand, command.getName(), args.get(0)));
                } else {
                    ChatUtils.sendMessage(sender, "&cCouldn't find command /" + label + " " + args.get(0));
                }
            }
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, String[] args, int argIndex) {
            List<String> result = new ArrayList<>();
            boolean aliases = args != null && args[0].isEmpty();
            for (SubCommand subCommand: subCommands) {
                if (hasPermission(sender, subCommand)) {
                    result.add(subCommand.getName());
                    if (aliases) {
                        Collections.addAll(result, subCommand.getAliases());
                    }
                }
            }
            return result;
        }
    }

}