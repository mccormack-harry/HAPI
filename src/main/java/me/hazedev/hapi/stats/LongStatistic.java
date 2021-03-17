package me.hazedev.hapi.stats;

import me.hazedev.hapi.chat.Formatter;
import me.hazedev.hapi.event.LongStatisticIncrementEvent;
import me.hazedev.hapi.userdata.UserData;
import me.hazedev.hapi.userdata.UserDataManager;
import me.hazedev.hapi.userdata.properties.LongProperty;
import me.hazedev.hapi.userdata.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LongStatistic extends Statistic<Long> {

    protected final Map<UUID, Long> memory = new ConcurrentHashMap<>();
    private final Property<Long> userDataProperty;

    public LongStatistic(String id, String name, boolean permanent) {
        super(id, name, permanent);
        userDataProperty = new LongProperty(id);
    }

    public LongStatistic(String id, boolean permanent) {
        super(id, permanent);
        userDataProperty = new LongProperty(id);
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
    public void load(UserDataManager userDataManager) {
        userDataManager.getAllUserData().forEach(userData -> {
            Long property = userData.getProperty(manager.getPath(), userDataProperty);
            if (property != null) {
                memory.put(userData.getProperty(UserData.UNIQUE_ID), userData.getProperty(manager.getPath(), userDataProperty, 0L));
            }
        });
    }

    @Override
    public void save(UserDataManager userDataManager) {
        for (Map.Entry<UUID, Long> entry: memory.entrySet()) {
            userDataManager.getUserData(entry.getKey()).setProperty(manager.getPath(), userDataProperty, entry.getValue());
        }
//        userDataSet.forEach(userData -> {
//            userData.setProperty(manager.getPath(), userDataProperty, memory.get(userData.getProperty(UserData.UNIQUE_ID)));
//        });
    }

    @Override
    public void reset(UserDataManager userDataManager) {
        userDataManager.getAllUserData().forEach(userData -> {
            userData.unsetProperty(manager.getPath(), userDataProperty);
        });
        memory.clear();
    }

    @Override
    public void loadOld() {
        YamlConfiguration storage = oldFileHandler.getConfiguration();
        for (String key : storage.getKeys(false)) {
            UUID uniqueId;
            try {
                uniqueId = UUID.fromString(key);
            } catch (IllegalArgumentException ignored) {
                continue;
            }
            memory.put(uniqueId, storage.getLong(key));
        }
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
