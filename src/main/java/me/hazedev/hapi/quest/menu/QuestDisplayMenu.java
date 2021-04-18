package me.hazedev.hapi.quest.menu;

import me.hazedev.gui.content.InventoryProvider;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface QuestDisplayMenu extends InventoryProvider {

    ItemStack getTitleItem();

    void open(Player player);

    default List<String> formatRewards(String[] rewards) {
        List<String> result = new ArrayList<>();
        if (rewards != null && rewards.length > 0) {
            if (rewards.length == 1) {
                result.add("&6Reward: &f" + rewards[0]);
            } else {
                result.add("&6Rewards:");
                for (String reward : rewards) {
                    result.add("&f- " + reward);
                }
            }
        }
        return result;
    }

}
