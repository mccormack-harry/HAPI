package me.hazedev.hapi.chat.json;

import com.google.gson.JsonObject;

public class ClickAction {

    private final String action;
    private final String value;

    public ClickAction(String action, String value) {
        this.action = action;
        this.value = value;
    }

    public static ClickAction runCommand(String command) {
        if (!command.startsWith("/")) {
            command = "/" + command;
        }
        return new ClickAction("run_command", command);
    }

    public static ClickAction suggestCommand(String command) {
        if (!command.startsWith("/")) {
            command = "/" + command;
        }
        return new ClickAction("suggest_command", command);
    }

    public static ClickAction openUrl(String text) {
        return new ClickAction("open_url", text);
    }

    public static ClickAction copyToClipboard(String text) {
        return new ClickAction("copy_to_clipboard", text);
    }

    public JsonObject serialize() {
        JsonObject serialized = new JsonObject();
        serialized.addProperty("action", action);
        serialized.addProperty("value", value);
        return serialized;
    }

    @Override
    public String toString() {
        return serialize().toString();
    }
}
