package me.hazedev.hapi.player.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import me.hazedev.hapi.component.Component;
import me.hazedev.hapi.logging.Log;
import me.hazedev.hapi.player.data.property.Property;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

// TODO convert playerdata to file per player
public class PlayerDataManager extends Component implements Listener {

    private final Set<PlayerData> playerDataSet = Collections.synchronizedSet(new HashSet<>());

    public PlayerDataManager() {
        super("player-data");
    }

    @Override
    public boolean onEnable() throws Exception {
//        playerDataFile = new File(getDataFolder(), "playerdata.json");
//        if (playerDataFile.exists()) {
//            try (FileReader fileReader = new FileReader(playerDataFile)) {
//                BufferedReader bufferedReader = new BufferedReader(fileReader);
//                JsonArray array = new JsonStreamParser(bufferedReader).next().getAsJsonArray();
//                for (JsonElement element : array) {
//                    try {
//                        JsonObject jsonObject = element.getAsJsonObject();
//                        playerDataSet.add(new PlayerData(jsonObject));
//                    } catch (Exception e) {
//                        Log.warning("Failed to load player data: " + element.toString());
//                        Log.error(this, e);
//                    }
//                }
//            } catch (IOException e) {
//                Log.error(this, e);
//                return false;
//            }
//        }
        return true;
    }

    @Override
    public void onDisable() {
        playerDataSet.clear();
    }

    @Override
    public void save() {
        synchronized (playerDataSet) {
            for (PlayerData playerData : playerDataSet) {
                File playerDataFile = getPlayerDataFile(playerData.getUniqueId());
                if (!playerDataFile.exists()) {
                    playerDataFile.getParentFile().mkdirs();
                    try {
                        playerDataFile.createNewFile();
                    } catch (IOException e) {
                        Log.error(this, e, "Failed to create " + playerDataFile.getName());
                        continue;
                    }
                }
                try (FileWriter fileWriter = new FileWriter(playerDataFile)) {
                    new Gson().toJson(playerData.getRoot(), fileWriter);
                } catch (IOException e) {
                    Log.error(this, e, "Failed to save " + playerDataFile.getName());
                }
            }
        }
    }

    public Set<PlayerData> getAllPlayerData() {
        return Collections.unmodifiableSet(playerDataSet);
    }

    @Nullable
    public <T> PlayerData getByProperty(@NotNull Property<T> property, @NotNull T value) {
        synchronized (playerDataSet) {
            for (PlayerData playerData : playerDataSet) {
                if (value.equals(playerData.getProperty(property))) {
                    return playerData;
                }
            }
        }
        return null;
    }

    private PlayerData createPlayerData(Player player) {
        PlayerData playerData = new PlayerData(player);
        return new PlayerData(player);
    }

    @Nullable
    public PlayerData getPlayerData(UUID uniqueId) {
        return getByProperty(PlayerData.UNIQUE_ID, uniqueId);
    }

    @Nullable
    public PlayerData getPlayerData(String name) {
        return getByProperty(PlayerData.NAME, name);
    }

    @NotNull
    public PlayerData getPlayerData(Player player) {
        PlayerData playerData = getPlayerData(player.getUniqueId());
        if (playerData == null) {
            playerData = createPlayerData(player);
            playerDataSet.add(playerData);
        }
        return playerData;
    }

    // No longer load playerdata for offline players
    @Deprecated
    public OfflinePlayer getOfflinePlayer(String name) {
        PlayerData playerData = getPlayerData(name);
        if (playerData != null) {
            return getOfflinePlayer(playerData.getProperty(PlayerData.UNIQUE_ID));
        }
        return null;
    }

    // No longer load playerdata for offline players
    @Deprecated
    public OfflinePlayer getOfflinePlayer(UUID uniqueId) {
        if (getPlayerData(uniqueId) != null) {
            return Bukkit.getOfflinePlayer(uniqueId);
        }
        return null;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Load data on join
        Player player = event.getPlayer();
        File playerDataFile = getPlayerDataFile(player.getUniqueId());
        PlayerData playerData;
        if (playerDataFile.exists()) {
            try (FileReader fileReader = new FileReader(playerDataFile)) {
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                JsonObject jsonObject = new JsonStreamParser(bufferedReader).next().getAsJsonObject();
                playerData = new PlayerData(jsonObject);
                playerDataSet.add(playerData);
            } catch (IOException e) {
                Log.error(this, e, "Failed to load player-data for " + player.getName());
                player.kickPlayer("Couldn't load your data, contact an administrator.");
                return;
            }
        } else {
            playerData = createPlayerData(player);
        }

        playerData.setProperty(PlayerData.NAME, player.getName());
    }

    private File getPlayerDataFile(UUID uniqueId) {
        return new File(getDataFolder(), uniqueId + ".json");
    }

}
