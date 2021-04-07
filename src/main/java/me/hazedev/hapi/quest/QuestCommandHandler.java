package me.hazedev.hapi.quest;

import me.hazedev.hapi.command.CommandHandler;
import me.hazedev.hapi.command.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class QuestCommandHandler extends CommandHandler {

    final QuestManager manager;

    public QuestCommandHandler(QuestManager manager) {
        super("quests", "View your quests", Collections.singletonList("q"),  "list");
        this.manager = manager;
        registerSubCommand(new List());
    }

    private class List extends SubCommand {

        public List() {
            super( "list", null, null, null);
        }

        @Override
        public void onCommand(CommandSender sender, String label, java.util.List<String> args, java.util.List<String> flags) {
            manager.showQuestMenu(validatePlayer(sender));
        }
    }

}
