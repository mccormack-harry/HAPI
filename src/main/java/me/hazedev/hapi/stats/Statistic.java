package me.hazedev.hapi.stats;

import me.hazedev.hapi.io.YamlFileHandler;
import me.hazedev.hapi.userdata.UserData;
import org.apache.commons.lang.WordUtils;

import java.util.*;

public abstract class Statistic<T> {

    protected AbstractStatisticManager manager;
    protected YamlFileHandler oldFileHandler;

    private final String id;
    private final String name;
    private final boolean permanent;

    public Statistic(String id, String name, boolean permanent) {
        this.id = id;
        this.name = name;
        this.permanent = permanent;
    }

    public Statistic(String id, boolean permanent) {
        this(id, WordUtils.capitalizeFully(id.replace("_", " ")), permanent);
    }

    public final String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public abstract Map<UUID, T> getAll();

    public abstract void load(Set<UserData> userDataSet);

    public abstract void save(Set<UserData> userDataSet);

    public abstract void reset(Set<UserData> userDataSet);

    public abstract void loadOld();

    public abstract int compare(T t1, T t2);

    public String getValue(UUID uniqueId, boolean isShort) {
        return format(getRawValue(uniqueId), isShort);
    }

    public abstract T getRawValue(UUID uniqueId);

    public abstract String format(T value, boolean isShort);

    public Map<UUID, String> getTopFormatted(int amount, boolean isShort) {
        return getTopFormatted(0, amount, isShort);
    }

    public Map<UUID, String> getTopFormatted(int start, int end, boolean isShort) {
        Map<UUID, T> top = getTop(start, end);
        Map<UUID, String> topFormatted = new LinkedHashMap<>();
        top.forEach((uniqueId, t) -> {
            topFormatted.put(uniqueId, format(t, isShort));
        });
        return topFormatted;
    }

    public Map<UUID, T> getTop(int amount) {
        return getTop(0, amount);
    }

    public Map<UUID, T> getTop(int start, int end) {
        Map<UUID, T> all = getAll();
        end = Math.min(end, all.size());
        if (end <= 0) return new HashMap<>();
        if (start < 0) start = 0;
        if (start >= end) throw new IllegalArgumentException("start must be before than end");

        List<Map.Entry<UUID, T>> entryList = new ArrayList<>(all.entrySet());
        entryList.sort(Map.Entry.comparingByValue(this::compare));
        Collections.reverse(entryList); // Order highest to lowest
        entryList.removeIf(entry -> entry.getKey().toString().equals("7e6258e2-938b-4c95-a75d-b476f38e6a18")); // TODO hide based on permissions ??

        Map<UUID, T> top = new LinkedHashMap<>();
        for (int i = start; i < end; i++) {
            Map.Entry<UUID, T> entry = entryList.get(i);
            top.put(entry.getKey(), entry.getValue());
        }

        return top;
    }

}
