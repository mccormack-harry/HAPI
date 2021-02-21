package me.hazedev.hapi.chat.json;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.ChatColor;

public class JsonComponent {

    final String text;
    ChatColor color = null;
    HoverAction hoverAction = null;
    ClickAction clickAction = null;
    String insertion = null;


    public JsonComponent(String text) {
        this.text = (text == null ? "" : ChatColor.translateAlternateColorCodes('&', text));
    }

    public JsonComponent color(ChatColor color) {
        this.color = color;
        return this;
    }

    public JsonComponent onHover(HoverAction hoverAction) {
        this.hoverAction = hoverAction;
        return this;
    }

    public JsonComponent onClick(ClickAction clickAction) {
        this.clickAction = clickAction;
        return this;
    }

    public JsonComponent suggestText(String text) {
        this.insertion = text;
        return this;
    }

    public JsonObject serialize() {
        JsonObject result = new JsonObject();
        result.addProperty("text", text);
        if (color != null)
            result.addProperty("color", color.getName());
        if (hoverAction != null)
            result.add("hoverEvent", hoverAction.serialize());
        if (clickAction != null)
            result.add("clickEvent", clickAction.serialize());
        if (insertion != null && !insertion.isEmpty())
            result.addProperty("insertion", insertion);
        return result;
    }

    @Override
    public String toString() {
        return serialize().toString();
    }
}