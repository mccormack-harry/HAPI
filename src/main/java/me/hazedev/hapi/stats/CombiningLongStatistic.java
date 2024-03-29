package me.hazedev.hapi.stats;

import me.hazedev.hapi.player.data.PlayerData;
import me.hazedev.hapi.player.data.PlayerDataManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CombiningLongStatistic extends LongStatistic {

    private final List<Class<? extends LongStatistic>> combined;

    public CombiningLongStatistic(String id, String name, List<Class<? extends LongStatistic>> combined) {
        super(id, name, false);
        this.combined = combined;
    }

    public CombiningLongStatistic(String id, List<Class<? extends LongStatistic>> combined) {
        super(id, false);
        this.combined = combined;
    }

    @Override
    public Map<UUID, Long> getAll() {
        PlayerDataManager playerDataManager = manager.verifySoftDependency(PlayerDataManager.class);
        if (playerDataManager != null) {
            Map<UUID, Long> all = new HashMap<>();
            for (PlayerData playerData : playerDataManager.getAllPlayerData()) {
                UUID uniqueId =  playerData.getProperty(PlayerData.UNIQUE_ID);
                all.put(uniqueId, getRawValue(uniqueId));
            }
            return all;
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public Long getRawValue(UUID uniqueId) {
        long total = 0;
        for (Class<? extends LongStatistic> toCombine: combined) {
            LongStatistic statistic = manager.getStatistic(toCombine);
            if (statistic != null) total += statistic.getRawValue(uniqueId);
        }
        return total;
    }

    @Override
    public void load(PlayerDataManager playerDataManager) {}

    @Override
    public void save(PlayerDataManager playerDataManager) {}

    @Override
    public void reset(PlayerDataManager playerDataManager) {}

    @Override
    protected void increment(UUID uniqueId, long amount) {}

    @Override
    protected void set(UUID uuid, long amount) {}

    @Override
    protected void setIfAbsent(UUID uuid, long amount) {}

}
