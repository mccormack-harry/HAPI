package me.hazedev.hapi.bossbar;

import me.hazedev.hapi.command.Argument;
import me.hazedev.hapi.command.SubCommand;
import me.hazedev.hapi.validation.Validators;
import me.hazedev.hapi.chat.ChatUtils;
import me.hazedev.hapi.command.CommandHandler;
import org.apache.commons.lang.WordUtils;
import org.bukkit.boss.BarColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class BossBarCommandHandler extends CommandHandler {

    BossBar bossBar;

    public BossBarCommandHandler(BossBar bossBar) {
        super("bossbar");
        this.bossBar = bossBar;

        registerSubCommand(new Title());
        registerSubCommand(new Color());
        registerSubCommand(new Progress());
    }

    class Title extends SubCommand {

        public Title() {
            super("title", null, new Argument[]{new Argument<>("title")}, null);
        }

        @Override
        public void onCommand(CommandSender sender, String label, List<String> args, List<String> flags) {
            StringBuilder title = new StringBuilder();
            for (String arg: args) {
                title.append(arg).append(" ");
            }
            bossBar.setTitle(title.toString());
            ChatUtils.sendMessage(sender, "&aNew title: &f" + title);
        }
    }

    class Color extends SubCommand {

        public Color() {
            super("color", null, new Argument[]{new Argument<>(Validators.enumValidator(BarColor.class), "color")}, null);
        }

        @Override
        public void onCommand(CommandSender sender, String label, List<String> args, List<String> flags) {
            BarColor color = Validators.enumValidator(BarColor.class).validateValue(args.get(0).toUpperCase());
            bossBar.setColor(color);
            ChatUtils.sendMessage(sender, "&aNew color: &f" + WordUtils.capitalizeFully(color.name()));
        }
    }

    class Progress extends SubCommand {

        public Progress() {
            super("progress", null, new Argument[]{new Argument<>(Validators.floatRange(0, 1), "progress")}, null);
        }

        @Override
        public void onCommand(CommandSender sender, String label, List<String> args, List<String> flags) {
            float progress = Validators.floatRange(0, 1).validateValue(args.get(0));
            bossBar.setProgress(progress);
            ChatUtils.sendMessage(sender, "&aNew progress: " + progress);
        }
    }

}
