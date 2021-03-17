package me.hazedev.hapi.chat.json;

import com.google.gson.JsonArray;
import me.hazedev.hapi.logging.Log;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;

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

    public BaseComponent[] toBaseComponents() {
        ComponentBuilder componentBuilder = new ComponentBuilder();
        for (JsonComponent jsonComponent: content) {
            componentBuilder.append(ComponentSerializer.parse(jsonComponent.toString()));
        }
        return componentBuilder.create();
    }

    @Override
    public String toString() {
        return serialize().toString();
    }
}
