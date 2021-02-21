package me.hazedev.hapi.stats;

import me.hazedev.hapi.userdata.UserData;
import me.hazedev.hapi.userdata.UserDataManager;

import java.util.*;

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
        UserDataManager userDataManager = manager.verifySoftDependency(UserDataManager.class);
        if (userDataManager != null) {
            Map<UUID, Long> all = new HashMap<>();
            for (UserData userData: userDataManager.getAllUserData()) {
                UUID uniqueId =  userData.getProperty(UserData.UNIQUE_ID);
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
    public void load(Set<UserData> userDataSet) {}

    @Override
    public void save(Set<UserData> userDataSet) {}

    @Override
    public void reset(Set<UserData> userDataSet) {}

    @Override
    public void loadOld() {}

    @Override
    protected void increment(UUID uniqueId, long amount) {}

    @Override
    protected void set(UUID uuid, long amount) {}

    @Override
    protected void setIfAbsent(UUID uuid, long amount) {}

}
