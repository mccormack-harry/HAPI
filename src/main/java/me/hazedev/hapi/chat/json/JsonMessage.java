package me.hazedev.hapi.chat.json;

import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.List;

public class JsonMessage {

    final List<JsonComponent> content;

    public JsonMessage() {
        content = new ArrayList<>(0);
    }

    public JsonMessage append(JsonComponent component) {
        content.add(component);
        return this;
    }

    public JsonMessage append(String text) {
        return append(new JsonComponent(text));
    }

    public JsonMessage space() {
        return append(" ");
    }

    public JsonMessage newLine() {
        return append("\n");
    }

    public JsonArray serialize() {
        JsonArray result = new JsonArray();
        for (JsonComponent component: content) {
            result.add(component.serialize());
        }
        return result;
    }

    @Override
    public String toString() {
        return serialize().toString();
    }
}
