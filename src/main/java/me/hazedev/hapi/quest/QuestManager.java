package me.hazedev.hapi.quest;

import me.hazedev.hapi.chat.CCUtils;
import me.hazedev.hapi.chat.ChatUtils;
import me.hazedev.hapi.component.Component;
import me.hazedev.hapi.logging.Log;
import me.hazedev.hapi.quest.menu.QuestsMainMenu;
import me.hazedev.hapi.player.data.PlayerDataManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuestManager extends Component implements Listener {

    private PlayerDataManager playerDataManager;

    public static final String PREFIX = CCUtils.addColor("&c[Quests] ");
    public static final String COLOR = CCUtils.GOLD;

    private final Set<Quest> quests = new HashSet<>();
    private final List<Milestone> milestones = new ArrayList<>();
    private QuestsMainMenu menu;

    public QuestManager() {
        super("quests");
    }

    @Override
    public boolean onEnable() throws Exception {
        playerDataManager = verifyHardDependency(PlayerDataManager.class);
        menu = new QuestsMainMenu(this);
        registerListener(new QuestUpdater(this));
        registerCommand(new QuestCommandHandler(this));
        return true;
    }

    @Override
    protected void reset() {
        playerDataManager.getAllPlayerData().forEach(playerdata -> playerdata.unsetProperty(getId()));
    }

    public void updateQuests(Player player) {
        if (player != null) {
            quests.forEach(quest -> quest.updateStatus(player));
        }
    }

    public void registerMilestone(Milestone milestone) {
        if (getMilestone(milestone.getClass()) == null) {
            milestone.manager = this;
            registerListener(milestone);
            milestones.add(milestone);
        } else {
            Log.warning(this, "Duplicate milestone: " + milestone.getClass().getName());
        }
    }

    public void registerQuest(Quest quest) {
        if (getQuest(quest.getClass()) == null) {
            quest.manager = this;
            if (quest instanceof Listener) {
                registerListener((Listener) quest);
            }
            quests.add(quest);
        } else {
            Log.warning(this, "Duplicate quest: " + quest.getClass().getName());
            throw new IllegalArgumentException("Quest already registered!");
        }
    }

    public Quest getQuest(Class<? extends Quest> clazz) {
        for (Quest quest: quests) {
            if (quest.getClass() == clazz) {
                return quest;
            }
        }
        return null;
    }

    public Milestone getMilestone(Class<? extends Milestone> clazz) {
        for (Milestone milestone: milestones) {
            if (milestone.getClass() == clazz) {
                return milestone;
            }
        }
        return null;
    }

    public @NotNull Set<Quest> getQuests(@NotNull Player player, @NotNull QuestStatus questStatus) {
        Set<Quest> quests = new HashSet<>();
        for (Quest quest: this.quests) {
            if (quest.getQuestStatus(player) == questStatus) {
                quests.add(quest);
            }
        }
        return quests;
    }

    public @NotNull List<Milestone> getMilestones() {
        return new ArrayList<>(milestones);
    }

    public static void sendMessage(CommandSender sender, String message) {
        if (message != null) {
            ChatUtils.sendMessage(sender, PREFIX + COLOR + message.replace("&r", COLOR));
        }
    }

    public void showQuestMenu(Player player) {
        menu.open(player);
    }

    public PlayerDataManager getPlayerdataManager() {
        return playerDataManager;
    }

    @Override
    protected List<Class<? extends Component>> getDependencies(boolean hard) {
        if (hard) {
            return Collections.singletonList(PlayerDataManager.class);
        } else {
            return null;
        }
    }
}
