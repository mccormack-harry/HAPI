package me.hazedev.hapi.stats;

import me.hazedev.hapi.chat.Formatter;
import me.hazedev.hapi.event.LongStatisticIncrementEvent;
import me.hazedev.hapi.player.data.PlayerData;
import me.hazedev.hapi.player.data.PlayerDataManager;
import me.hazedev.hapi.player.data.property.LongProperty;
import me.hazedev.hapi.player.data.property.Property;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LongStatistic extends Statistic<Long> {

    protected final Map<UUID, Long> memory = new ConcurrentHashMap<>();
    private final Property<Long> playerdataProperty;

    public LongStatistic(String id, String name, boolean permanent) {
        super(id, name, permanent);
        playerdataProperty = new LongProperty(id);
    }

    public LongStatistic(String id, boolean permanent) {
        super(id, permanent);
        playerdataProperty = new LongProperty(id);
    }

    @Override
    public Map<UUID, Long> getAll() {
        return Collections.unmodifiableMap(memory);
    }

    @Override
    public int compare(Long t1, Long t2) {
        return Long.compare(t1, t2);
    }

    @Override
    public void load(PlayerDataManager playerDataManager) {
        playerDataManager.getAllPlayerData().forEach(playerdata -> {
            Long property = playerdata.getProperty(manager.getPath(), playerdataProperty);
            if (property != null) {
                memory.put(playerdata.getProperty(PlayerData.UNIQUE_ID), playerdata.getProperty(manager.getPath(), playerdataProperty, 0L));
            }
        });
    }

    @Override
    public void save(PlayerDataManager playerDataManager) {
        for (Map.Entry<UUID, Long> entry: memory.entrySet()) {
            playerDataManager.getPlayerData(entry.getKey()).setProperty(manager.getPath(), playerdataProperty, entry.getValue());
        }
//        playerdataSet.forEach(playerdata -> {
//            playerdata.setProperty(manager.getPath(), playerdataProperty, memory.get(playerdata.getProperty(Playerdata.UNIQUE_ID)));
//        });
    }

    @Override
    public void reset(PlayerDataManager playerDataManager) {
        playerDataManager.getAllPlayerData().forEach(playerdata -> {
            playerdata.unsetProperty(manager.getPath(), playerdataProperty);
        });
        memory.clear();
    }

    @Override
    public String format(Long value, boolean isShort) {
        if (isShort) {
            return Formatter.formatShort(value);
        } else {
            return Formatter.formatLong(value);
        }
    }

    @Override
    public Long getRawValue(UUID uniqueId) {
        if (memory.containsKey(uniqueId)) {
            return memory.get(uniqueId);
        }
        return 0L;
    }

    protected void increment(UUID uniqueId, long amount) {
        if (memory.containsKey(uniqueId)) {
            memory.put(uniqueId, memory.get(uniqueId) + amount);
        } else {
            memory.put(uniqueId, amount);
        }
        callIncrementEvent(uniqueId);
    }

    protected void set(UUID uniqueId, long amount) {
        memory.put(uniqueId, amount);
        callIncrementEvent(uniqueId);
    }

    protected void setIfAbsent(UUID uniqueId, long amount) {
        if (memory.get(uniqueId) == null) {
            memory.put(uniqueId, amount);
            callIncrementEvent(uniqueId);
        }
    }

    protected void callIncrementEvent(UUID uniqueId) {
        Bukkit.getPluginManager().callEvent(new LongStatisticIncrementEvent(this, Bukkit.getPlayer(uniqueId)));
    }

}
