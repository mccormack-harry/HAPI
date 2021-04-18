package me.hazedev.hapi.quest.menu;

import me.hazedev.gui.SmartInventory;
import me.hazedev.gui.content.InventoryContents;
import me.hazedev.gui.content.SlotIterator;
import me.hazedev.gui.items.ClickableItem;
import me.hazedev.hapi.chat.CCUtils;
import me.hazedev.hapi.inventory.ItemBuilder;
import me.hazedev.hapi.quest.Milestone;
import me.hazedev.hapi.quest.QuestManager;
import me.hazedev.hapi.quest.QuestStatus;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MilestonesMenu implements QuestDisplayMenu {

    private final QuestManager manager;
    private final SmartInventory inventory;

    MilestonesMenu(QuestManager manager) {
        this.manager = manager;
        inventory = SmartInventory.builder()
                .title(CCUtils.addColor("&6[Quests] &cMilestones"))
                .size(3, 9)
                .provider(this)
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fill(ClickableItem.of(ItemBuilder.getBackItem(), onClick -> manager.showQuestMenu(player)));
        SlotIterator iterator = contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0);
        for (Milestone milestone: manager.getMilestones()) {
            List<String> lore = new ArrayList<>();
            if (milestone.getQuestStatus(player) == QuestStatus.FINISHED) {
                lore.add("&eTier: &f" + milestone.getTiers().length + "&e/&f" + milestone.getTiers().length);
                lore.add("&6Finished!");
            } else {
                lore.add("&eTier: &f" + milestone.getCurrentTier(player) + "&a/&f" + milestone.getTiers().length);
                lore.add("&eProgress: &f" + milestone.format(milestone.getProgress(player)) + "&e/&f" + milestone.format(milestone.getGoal(player)));
                lore.addAll(formatRewards(milestone.getRewards(player)));
                lore.addAll(milestone.getFormattedDescription(player));
            }
            ItemStack milestoneItem = new ItemBuilder()
                    .material(Material.BOOK)
                    .name("&c" + milestone.getDisplayName())
                    .lore(lore)
                    .glow(true)
                    .hideAllFlags()
                    .build();
            iterator.next().set(ClickableItem.empty(milestoneItem));
        }
    }

    @Override
    public ItemStack getTitleItem() {
        return new ItemBuilder()
                .material(Material.BOOKSHELF)
                .name("&6Milestones")
                .glow(true)
                .hideAllFlags()
                .build();
    }

    @Override
    public void open(Player player) {
        inventory.open(player);
    }
}
