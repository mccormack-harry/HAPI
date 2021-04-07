package me.hazedev.hapi.component;

import me.hazedev.hapi.chat.ChatUtils;
import me.hazedev.hapi.command.Argument;
import me.hazedev.hapi.command.CommandHandler;
import me.hazedev.hapi.command.SubCommand;
import me.hazedev.hapi.validation.Validator;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Comparator;

public final class ComponentCommandHandler extends CommandHandler {

    private final ComponentManager manager;
    private final Validator<Component> componentValidator = new ComponentValidator();

    public ComponentCommandHandler(ComponentManager manager) {
        super("components", "ComponentManager command", null,  "list");
        this.manager =  manager;
        registerSubCommand(new List());
        registerSubCommand(new Reload());
        registerSubCommand(new Save());
    }

   private class List extends SubCommand {

       public List() {
           super("list", new String[]{"l"}, null, null);
       }

       @Override
       public void onCommand(CommandSender sender, String label, java.util.List<String> args, java.util.List<String> flags) {
           StringBuilder result = new StringBuilder("&aComponents: ");
           java.util.List<Component> components = new ArrayList<>(manager.getComponents());
           components.sort(Comparator.comparing(Component::getId));
           for (Component component: manager.getComponents()) {
               result.append(component.isEnabled() ? "&a": "&c").append(component.getId()).append("&a, ");
           }
           ChatUtils.sendMessage(sender, result.substring(0, result.length() - 1));
       }

   }

   private class Reload extends SubCommand {

       public Reload() {
           super("reload", new String[]{"rl"}, new Argument[]{new Argument<>(componentValidator, "component")}, null);
       }

       @Override
       public void onCommand(CommandSender sender, String label, java.util.List<String> args, java.util.List<String> flags) {
           Component component = componentValidator.validateValue(args.get(0));
           if (component.isEnabled()) {
               if (component.reload()) {
                   ChatUtils.sendMessage(sender, "&aSuccess");
               } else {
                   ChatUtils.sendMessage(sender, "&cError");
               }
           } else {
               ChatUtils.sendMessage(sender, "&c" + component.getId() + " is disabled!");
           }
       }
   }

    private class Save extends SubCommand {

        public Save() {
            super("save", new String[]{"s"}, new Argument[]{new Argument<>(componentValidator, "component")}, null);
        }

        @Override
        public void onCommand(CommandSender sender, String label, java.util.List<String> args, java.util.List<String> flags) {
            Component component = componentValidator.validateValue(args.get(0));
            if (component.isEnabled()) {
                if (manager.save(component)) {
                    ChatUtils.sendMessage(sender, "&aSuccess");
                } else {
                    ChatUtils.sendMessage(sender, "&cError");
                }
            } else {
                ChatUtils.sendMessage(sender, "&c" + component.getId() + " is disabled!");
            }
        }
    }

   public class ComponentValidator implements Validator<Component> {

       @Override
       public Component validateValue(String value) throws IllegalArgumentException {
           for (Component component: manager.getComponents()) {
               if (component.getId().equalsIgnoreCase(value)) {
                   return component;
               }
           }
           throw new IllegalArgumentException("Couldn't find component: " + value);
       }

       @Override
       public java.util.List<String> getPossibleValues(String search) {
           java.util.List<String> result = new ArrayList<>(0);
           for (Component component: manager.getComponents()) {
               if (component.getId().startsWith(search)) {
                   result.add(component.getId());
               }
           }
           return result;
       }

   }

}
