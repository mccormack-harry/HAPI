package me.hazedev.hapi.joinquit;

import me.hazedev.hapi.chat.CCUtils;
import me.hazedev.hapi.chat.Formatter;
import me.hazedev.hapi.component.Component;
import me.hazedev.hapi.event.FirstJoinEvent;
import me.hazedev.hapi.logging.Log;
import me.hazedev.hapi.userdata.UserData;
import me.hazedev.hapi.userdata.UserDataManager;
import me.hazedev.hapi.userdata.properties.BooleanProperty;
import me.hazedev.hapi.userdata.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class JoinQuitHandler extends Component implements Listener {

    private static final Property<Boolean> WELCOMED_PROPERTY = new BooleanProperty("welcomed");
    Set<UUID> welcomed = new HashSet<>(0);

    public JoinQuitHandler() {
        super("welcomer");
    }

    @Override
    public boolean onEnable() throws Exception {
        verifyHardDependency(UserDataManager.class).getAllUserData().forEach(userData -> {
            if (userData.getProperty(WELCOMED_PROPERTY, false)) {
                this.welcomed.add(userData.getProperty(UserData.UNIQUE_ID));
            }
        });
        return true;
    }

    @Override
    public void save() {
        UserDataManager userDataManager = verifyHardDependency(UserDataManager.class);
        for (UserData userData: userDataManager.getAllUserData()) {
            UUID uniqueId = userData.getProperty(UserData.UNIQUE_ID);
            if (welcomed.contains(uniqueId)) {
                userData.setProperty(WELCOMED_PROPERTY, true);
            }
        }
    }

    @Override
    protected void reset() {
        this.welcomed.clear();
        verifyHardDependency(UserDataManager.class).getAllUserData().forEach(userData -> {
            userData.unsetProperty(WELCOMED_PROPERTY);
        });
    }

    public boolean shouldWelcome(@NotNull UUID uniqueId) {
        return !welcomed.contains(uniqueId);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String message;
        if (shouldWelcome(player.getUniqueId())) {
            welcomed.add(player.getUniqueId());
            message = CCUtils.addColor("&dWelcome &f" + player.getName() + " &dto the server! (#" + Formatter.formatLong(welcomed.size()) + ")");
            Bukkit.getPluginManager().callEvent(new FirstJoinEvent(player));
        } else {
            message = CCUtils.addColor("&8[&a+&8] &a" + player.getName());
        }
        event.setJoinMessage(message);
        Log.chat(message);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        String message = CCUtils.addColor("&8[&c-&8] &c" + event.getPlayer().getName());
        event.setQuitMessage(message);
        Log.chat(message);
    }

    @Override
    protected List<Class<? extends Component>> getDependencies(boolean hard) {
        if (hard) {
            return Collections.singletonList(UserDataManager.class);
        } else {
            return null;
        }
    }
}
