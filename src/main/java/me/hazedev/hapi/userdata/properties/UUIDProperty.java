package me.hazedev.hapi.userdata.properties;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.UUID;

public class UUIDProperty extends Property<UUID> {

    public UUIDProperty(String id) {
        super(id);
    }

    @Override
    public UUID fromJsonElement(JsonElement element) {
        return UUID.fromString(element.getAsJsonPrimitive().getAsString());
    }

    @Override
    public JsonElement toJsonElement(UUID value) {
        return new JsonPrimitive(value.toString());
    }

}
