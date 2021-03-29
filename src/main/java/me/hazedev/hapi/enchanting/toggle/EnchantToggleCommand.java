package me.hazedev.hapi.enchanting.toggle;

import me.hazedev.hapi.chat.CCUtils;
import me.hazedev.hapi.chat.ChatUtils;
import me.hazedev.hapi.enchanting.EnchantmentInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class EnchantToggleCommand<T extends EnchantmentInfo & ToggleableEnchantment> extends BukkitCommand {

    final T enchantment;

    public EnchantToggleCommand(T enchantment) {
        super(enchantment.getId(),
                CCUtils.addColor("Toggles the " + enchantment.getDisplayColor() + enchantment.getName() + " &renchantment"), // Doesn't support hex
                "/" + enchantment.getId(),
                Collections.emptyList());
        this.enchantment = enchantment;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Player player;
        if (sender instanceof Player) {
            player = (Player) sender;
            enchantment.toggle(player.getUniqueId());
            String status = enchantment.isEnabled(player.getUniqueId()) ? "Enabled" : "Disabled";
            ChatUtils.sendMessage(player, "&a" + status + " " + enchantment.getDisplayName());
        } else {
            ChatUtils.sendMessage(sender, "&cYou must be a player!");
        }
        return true;
    }
}
