package me.hazedev.hapi.player.data.property;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UUIDProperty extends Property<UUID> {

    public UUIDProperty(String id) {
        super(id);
    }

    @Override
    public UUID fromJsonElement(@NotNull JsonElement element) {
        return UUID.fromString(element.getAsJsonPrimitive().getAsString());
    }

    @Override
    public JsonElement toJsonElement(@NotNull UUID value) {
        return new JsonPrimitive(value.toString());
    }

}
