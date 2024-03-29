package me.hazedev.hapi.enchanting;

import me.hazedev.hapi.event.EnchantEvent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EffectEnchantment extends CustomEnchantment implements Listener {

    final PotionEffectType potionEffectType;
    final Set<UUID> activated = new HashSet<>(0);

    public EffectEnchantment(PotionEffectType potionEffect, String description, ChatColor displayColor, Material displayMaterial, EnchantmentTarget target, CostAlgorithm costAlgorithm, int maxLevel) {
        super(potionEffect.getName().toLowerCase(), description, displayColor, displayMaterial, target, costAlgorithm, maxLevel);
        this.potionEffectType = potionEffect;
    }

    public EffectEnchantment(PotionEffectType potionEffect, String description, ChatColor displayColor, Material displayMaterial, EnchantmentTarget[] targets, CostAlgorithm costAlgorithm, int maxLevel) {
        super(potionEffect.getName().toLowerCase(), description, displayColor, displayMaterial, targets, costAlgorithm, maxLevel);
        this.potionEffectType = potionEffect;
    }

    @EventHandler
    public void onPlayerSwitchItem(PlayerItemHeldEvent event){
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(manager.getComponent().getPlugin(), () -> updateEffect(player));
    }

    @EventHandler
    public void onEnchantEvent(EnchantEvent event) {
        if (event.getEnchantment() == this) {
            Player player = event.getPlayer();
            Bukkit.getScheduler().runTask(manager.getComponent().getPlugin(), () -> updateEffect(player));
        }
    }

    protected void removeEffect(Player player) {
        if (activated.contains(player.getUniqueId())) {
            player.removePotionEffect(potionEffectType);
            activated.remove(player.getUniqueId());
        }
    }

    protected void addEffect(Player player, int level) {
        if (level > 0) {
            player.addPotionEffect(new PotionEffect(potionEffectType, Integer.MAX_VALUE, level - 1, false, false, true));
            activated.add(player.getUniqueId());
        }
    }

    protected void updateEffect(Player player) {
        int level = getManager().getEnchantmentLevel(player, this);
        if (level > 0) {
            addEffect(player, level);
        } else {
            removeEffect(player);
        }
    }

}
