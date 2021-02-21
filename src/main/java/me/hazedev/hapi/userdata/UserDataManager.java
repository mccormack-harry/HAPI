package me.hazedev.hapi.userdata;

import com.google.gson.*;
import me.hazedev.hapi.component.Component;
import me.hazedev.hapi.logging.Log;
import me.hazedev.hapi.userdata.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserDataManager extends Component implements Listener {

    private File userDataFile;
    private final Set<UserData> userDataSet = new HashSet<>();

    public UserDataManager() {
        super("user-data");
    }

    @Override
    public boolean onEnable() {
        userDataFile = new File(getDataFolder(), "userdata.json");
        if (userDataFile.exists()) {
            try (FileReader fileReader = new FileReader(userDataFile)) {
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                JsonArray array = new JsonStreamParser(bufferedReader).next().getAsJsonArray();
                for (JsonElement element : array) {
                    try {
                        JsonObject jsonObject = element.getAsJsonObject();
                        userDataSet.add(new UserData(jsonObject));
                    } catch (Exception e) {
                        Log.warning("Failed to load user data: " + element.toString());
                        Log.error(this, e);
                    }
                }
            } catch (IOException e) {
                Log.error(this, e);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onDisable() {
        userDataSet.clear();
    }

    @Override
    public void save() {
        if (!userDataFile.exists()) {
            userDataFile.getParentFile().mkdirs();
            try {
                userDataFile.createNewFile();
            } catch (IOException e) {
                Log.warning(this, "Failed to create " + userDataFile.getName());
                Log.error(this, e);
            }
        }
        JsonArray jsonArray = new JsonArray();
        userDataSet.forEach(userData -> jsonArray.add(userData.getRoot()));
        try (FileWriter fileWriter = new FileWriter(userDataFile)) {
            new Gson().toJson(jsonArray, fileWriter);
        } catch (IOException e) {
            Log.warning(this, "Failed to save " + userDataFile.getName());
            Log.error(this, e);
        }
    }

    public Set<UserData> getAllUserData() {
        return Collections.unmodifiableSet(userDataSet);
    }

    public <T> UserData getByProperty(Property<T> property, T value) {
        if (property == null || value == null) throw new NullPointerException();
        for (UserData userData: userDataSet) {
            if (value.equals(userData.getProperty(property))) {
                return userData;
            }
        }
        return null;
    }

    @Deprecated
    public UserData createUserData(UUID uniqueId) {
        UserData userData = getUserData(uniqueId);
        if (userData == null) {
            userData = new UserData(new JsonObject());
            userData.setProperty(UserData.UNIQUE_ID, uniqueId);
            userDataSet.add(userData);
            userData.setProperty(UserData.NAME, Bukkit.getOfflinePlayer(uniqueId).getName());
        }
        return userData;
    }

    public UserData getUserData(UUID uniqueId) {
        return getByProperty(UserData.UNIQUE_ID, uniqueId);
    }

    public UserData getUserData(String name) {
        return getByProperty(UserData.NAME, name);
    }

    public UserData getUserData(Player player) {
        UserData userData = getUserData(player.getUniqueId());
        if (userData == null) {
            userData = new UserData(player);
            userDataSet.add(userData);
        }
        return userData;
    }

    public OfflinePlayer getOfflinePlayer(String name) {
        UserData userData = getUserData(name);
        if (userData != null) {
            return getOfflinePlayer(userData.getProperty(UserData.UNIQUE_ID));
        }
        return null;
    }

    public OfflinePlayer getOfflinePlayer(UUID uniqueId) {
        if (getUserData(uniqueId) != null) {
            return Bukkit.getOfflinePlayer(uniqueId);
        }
        return null;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UserData userData = getUserData(player);
        userData.setProperty(UserData.NAME, player.getName());
    }

}
