package me.hazedev.hapi.userdata.properties;

import com.google.gson.JsonElement;

public abstract class Property<T> {

    private final String id;

    public Property(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public abstract T fromJsonElement(JsonElement element);

    public abstract JsonElement toJsonElement(T value);

}
