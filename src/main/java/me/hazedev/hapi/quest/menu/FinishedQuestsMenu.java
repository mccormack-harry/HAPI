package me.hazedev.hapi.quest.menu;

import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.SlotIterator;
import fr.minuskube.inv.items.ClickableItem;
import me.hazedev.hapi.chat.CCUtils;
import me.hazedev.hapi.inventory.ItemBuilder;
import me.hazedev.hapi.quest.Quest;
import me.hazedev.hapi.quest.QuestManager;
import me.hazedev.hapi.quest.QuestStatus;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

public class FinishedQuestsMenu implements QuestDisplayMenu {

    private final QuestManager manager;
    private final SmartInventory inventory;

    FinishedQuestsMenu(QuestManager manager) {
        this.manager = manager;
        inventory = SmartInventory.builder()
                .title(CCUtils.addColor("&6[Quests] &cFinished Quests"))
                .size(3, 9)
                .provider(this)
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fill(ClickableItem.of(ItemBuilder.getBackItem(), onClick -> manager.showQuestMenu(player)));
        SlotIterator iterator = contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0);
        for (Quest quest: manager.getQuests(player, QuestStatus.FINISHED)) {
            List<String> lore = new ArrayList<>(quest.getFormattedDescription());
            lore.add("&aFinished: " + OffsetDateTime.ofInstant(Instant.ofEpochMilli(quest.getFinishedTime(player)), ZoneId.systemDefault()).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
            ItemStack questItem = new ItemBuilder()
                    .material(Material.BOOK)
                    .name(quest.getDisplayName())
                    .lore(lore)
                    .glow(true)
                    .hideAllFlags()
                    .build();
            iterator.next().set(ClickableItem.empty(questItem));
        }
    }

    @Override
    public ItemStack getTitleItem() {
        return new ItemBuilder()
                .material(Material.BOOK)
                .name("&6Finished Quests")
                .glow(true)
                .hideAllFlags()
                .build();
    }

    @Override
    public void open(Player player) {
        inventory.open(player);
    }
}
