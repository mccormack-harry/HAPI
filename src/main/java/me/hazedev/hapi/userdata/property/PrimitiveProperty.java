package me.hazedev.hapi.userdata.property;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

public abstract class PrimitiveProperty<T> extends Property<T> {

    public PrimitiveProperty(String id) {
        super(id);
    }

    @Override
    public abstract T fromJsonElement(@NotNull JsonElement element);

    @Override
    public abstract JsonPrimitive toJsonElement(@NotNull T value);
}
