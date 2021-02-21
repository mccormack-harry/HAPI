package me.hazedev.hapi.fly;

import me.hazedev.hapi.chat.ChatUtils;
import me.hazedev.hapi.component.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class Fly extends Component implements Listener, CommandExecutor {

    public final static String PERMISSION = "fly.use";

    public Fly() {
        super("fly");
    }

    @Override
    protected boolean onEnable() {
        getCommand("fly").ifPresent(command -> command.setExecutor(this));
        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setAllowFlight(player.hasPermission(PERMISSION));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if ("fly".equalsIgnoreCase(command.getName())) {
            if (sender instanceof Player) {
                if (sender.hasPermission(PERMISSION)) {
                    Player player = (Player) sender;
                    player.setAllowFlight(!player.getAllowFlight());
                    if (player.getAllowFlight()) {
                        ChatUtils.sendMessage(sender, "&6Flight &cenabled");
                    } else {
                        ChatUtils.sendMessage(player, "&6Flight &cdisabled");
                    }
                } else {
                    ChatUtils.sendMessage(sender, "&cYou don't have permission!");
                }
            }
        }
        return true;
    }



}
