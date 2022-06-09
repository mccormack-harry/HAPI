package me.hazedev.hapi.essentials;

import me.hazedev.hapi.chat.ChatUtils;
import me.hazedev.hapi.command.CommandHelper;
import me.hazedev.hapi.component.Component;
import me.hazedev.hapi.logging.Log;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

public class Rules extends Component {

    private List<String> rules;

    public Rules() {
        super("rules");
    }

    @Override
    protected boolean onEnable() throws Exception {
        rules = Collections.emptyList();
        reload();
        registerCommand(new CommandRules());
        return true;
    }

    @Override
    protected boolean reload() {
        File rulesFile = new File(getDataFolder(), "rules.txt");
        if (rulesFile.exists()) {
            try {
                List<String> rules = Files.readAllLines(rulesFile.toPath());
                if (!rules.isEmpty()) {
                    this.rules = rules;
                }
            } catch (IOException e) {
                Log.error(this, e, "Failed to read rules file");
            }
        } else {
            if (rulesFile.getParentFile().mkdirs()) {
                try {
                    return rulesFile.createNewFile();
                } catch (IOException e) {
                    Log.error(this, e, "Failed to create rules.txt");
                    return false;
                }
            }
        }
        return true;
    }

    private class CommandRules extends Command {

        private CommandRules() {
            super("rules", "Server rules", "/rules", Collections.emptyList());
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String[] strings) {
            if (CommandHelper.checkPermission(sender, getName() + ".use")) {
                if (rules.isEmpty()) {
                    ChatUtils.sendMessage(sender, "&cThe rules file hasn't been configured");
                } else {
                    ChatUtils.sendMessage(sender, rules);
                }
            }
            return true;
        }

    }

}
