package me.hazedev.hapi.quest.menu;

import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.items.ClickableItem;
import me.hazedev.hapi.chat.CCUtils;
import me.hazedev.hapi.inventory.ItemBuilder;
import me.hazedev.hapi.quest.QuestManager;
import org.bukkit.entity.Player;

public class QuestsMainMenu implements InventoryProvider {

    private final ActiveQuestsMenu activeQuestsMenu;
    private final MilestonesMenu milestonesMenu;
    private final FinishedQuestsMenu finishedQuestsMenu;
    private final SmartInventory inventory;

    public QuestsMainMenu(QuestManager manager) {
        activeQuestsMenu = new ActiveQuestsMenu(manager);
        milestonesMenu = new MilestonesMenu(manager);
        finishedQuestsMenu = new FinishedQuestsMenu(manager);
        inventory = SmartInventory.builder()
                .title(CCUtils.addColor("&6[Quests] &cMain Menu"))
                .size(3, 9)
                .provider(this)
                .build();;
    }

    public void open(Player player) {
        inventory.open(player);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fill(ClickableItem.of(ItemBuilder.getExitItem(), event -> contents.inventory().close(player)));
        contents.set(1, 1, ClickableItem.of(activeQuestsMenu.getTitleItem(), event -> activeQuestsMenu.open(player)));
        contents.set(1, 4, ClickableItem.of(milestonesMenu.getTitleItem(), event -> milestonesMenu.open(player)));
        contents.set(1, 7, ClickableItem.of(finishedQuestsMenu.getTitleItem(), event -> finishedQuestsMenu.open(player)));
    }

}
