package me.hazedev.hapi.userdata.properties;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public abstract class PrimitiveProperty<T> extends Property<T> {

    public PrimitiveProperty(String id) {
        super(id);
    }

    @Override
    public abstract T fromJsonElement(JsonElement element);

    @Override
    public abstract JsonPrimitive toJsonElement(T value);
}
