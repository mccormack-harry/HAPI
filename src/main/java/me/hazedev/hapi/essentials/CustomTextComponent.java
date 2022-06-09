package me.hazedev.hapi.essentials;

import me.hazedev.hapi.chat.CCUtils;
import me.hazedev.hapi.command.CommandDelegator;
import me.hazedev.hapi.component.Component;
import me.hazedev.hapi.config.ConfigurableSection;
import me.hazedev.hapi.config.YamlConfigReader;
import me.hazedev.hapi.config.YamlConfigurableFile;
import me.hazedev.hapi.config.YamlOption;
import me.hazedev.hapi.config.value.OptionListConfigurableSection;
import me.hazedev.hapi.config.value.OptionListString;
import me.hazedev.hapi.config.value.OptionString;
import me.hazedev.hapi.io.YamlFileHandler;
import me.hazedev.hapi.nms.CommandMapUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomTextComponent extends Component implements YamlConfigurableFile {

    private YamlFileHandler fileHandler;
    private final Set<Command> commands = new HashSet<>();
    public final YamlOption<List<CustomText>> customTexts = new OptionListConfigurableSection<>("custom-texts", "rules", path -> new CustomText(this, path));

    public CustomTextComponent() {
        super("custom-text");
    }

    @Override
    protected boolean onEnable() throws Exception {
        fileHandler = getYamlFileHandler("config.yml");
        return reload();
    }

    @Override
    protected boolean reload() {
        commands.forEach(CommandMapUtils::unregister);
        commands.clear();
        YamlConfigReader.reload(this);
        for (CustomText customText: customTexts.get()) {
            String identifier = customText.getIdentifier();
            Command command = new CommandDelegator(customText, identifier, customText.getDescription(), "/" + identifier, customText.getAliases());
            commands.add(command);
            registerCommand(command);
        }
        return true;
    }

    @NotNull
    @Override
    public YamlConfiguration getConfiguration() {
        return fileHandler.getConfiguration();
    }

    @NotNull
    @Override
    public String getFileName() {
        return fileHandler.getFile().getName();
    }

    @Override
    public void saveConfig() throws IOException {
        fileHandler.saveConfig();
    }

    private static class CustomText extends ConfigurableSection implements CommandExecutor {

        public final String identifier;
        public final YamlOption<List<String>> message = new OptionListString("message", "Hello");
        public final YamlOption<String> description = new OptionString("description", "Custom Text Command");
        public final YamlOption<List<String>> aliases = new OptionListString("aliases", Collections.emptyList());

        public CustomText(@NotNull YamlConfigurableFile root, @NotNull String path) {
            super(root, path);
            identifier = path.substring(path.lastIndexOf('.') + 1);
        }

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
            sender.sendMessage(getMessage().toArray(new String[0]));
            return true;
        }

        public String getIdentifier() {
            return identifier;
        }

        public List<String> getMessage() {
            return CCUtils.addColor(message.get());
        }

        public String getDescription() {
            return description.get();
        }

        public List<String> getAliases() {
            return aliases.get();
        }
    }

}
