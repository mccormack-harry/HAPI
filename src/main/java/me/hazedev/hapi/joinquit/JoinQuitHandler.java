package me.hazedev.hapi.joinquit;

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
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class JoinQuitHandler extends Component implements Listener {

    private static final Property<Boolean> WELCOMED_PROPERTY = new BooleanProperty("welcomed");
    Set<UUID> welcomed = new HashSet<>(0);

    public JoinQuitHandler() {
        super("welcomer");
    }

    @Override
    public boolean onEnable() {
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
        for (UUID uniqueId: welcomed) {
            UserData userData = userDataManager.getUserData(uniqueId);
            if (userData != null) {
                userData.setProperty(WELCOMED_PROPERTY, true);
            } else {
                Log.warning(this, "UserData for welcomed player is non-existent?");
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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (shouldWelcome(player.getUniqueId())) {
            welcomed.add(player.getUniqueId());
            event.setJoinMessage("&dWelcome &f" + player.getName() + " &dto the server! (#" + Formatter.formatLong(welcomed.size()) + ")");
            Bukkit.getPluginManager().callEvent(new FirstJoinEvent(player));
        } else {
            event.setJoinMessage("&8[&a+&8] &a" + player.getName());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage("&8[&c-&8] &c" + event.getPlayer().getName());
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
