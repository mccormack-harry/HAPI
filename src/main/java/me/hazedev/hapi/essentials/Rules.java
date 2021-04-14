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
    protected boolean onEnable() {
        registerCommand(new CommandRules());
        rules = Collections.emptyList();
        reload();
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
                Log.error(this, e);
                Log.warning(this, "Failed to read rules file");
            }
        } else {
            try {
                rulesFile.createNewFile();
            } catch (IOException e) {
                Log.error(this, e);
                Log.warning("Failed to create rules.txt");
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
                ChatUtils.sendMessage(sender, rules);
            }
            return true;
        }

    }

}
