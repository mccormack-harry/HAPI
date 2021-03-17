package me.hazedev.hapi.chat.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.hazedev.hapi.chat.CCUtils;
import net.md_5.bungee.api.ChatColor;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.md_5.bungee.api.ChatColor.COLOR_CHAR;

public class JsonComponent {


    // &x&F&F&F&F&F&F
    private static final Pattern HEX_PATTERN = Pattern.compile(COLOR_CHAR + "x([" + COLOR_CHAR + "][A-Fa-f0-9]){6}");

    private String text = "";
    private ChatColor color = null;
    private HoverAction hoverAction = null;
    private ClickAction clickAction = null;
    private String insertion = null;

    public JsonComponent() {}

    public JsonComponent(String text) {
        this();
        text(text);
    }

    public JsonComponent(JsonComponent component) {
        this.text = component.text;
        this.color = component.color;
        this.hoverAction = component.hoverAction;
        this.clickAction = component.clickAction;
        this.insertion = component.insertion;
    }

    public JsonComponent text(String text) {
        this.text = text == null ? "" : CCUtils.addColor(text);
        return this;
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

    public JsonElement serialize() {
        // Attempt translate HEX codes
        JsonArray hexResult = new JsonArray();
        String remaining = this.text;
        Matcher hexMatcher = HEX_PATTERN.matcher(remaining);
        while (hexMatcher.find()) {
            ChatColor hexColour = ChatColor.of(hexMatcher.group().replace("§x", "#").replace("§", "").toUpperCase(Locale.ROOT));
            String before = remaining.substring(0, hexMatcher.start());
            if (hexResult.size() == 0) {
                hexResult.add(new JsonComponent(this).text(before).serializeNoHex());
            } else {
                hexResult.add(new JsonComponent(before).serializeNoHex());
            }
            String after = remaining.substring(hexMatcher.end());
            int nextIndexOfColourChar = after.indexOf(COLOR_CHAR);
            String hexText;
            if (nextIndexOfColourChar >= 0) {
                hexText = after.substring(0, nextIndexOfColourChar);
                remaining = after.substring(nextIndexOfColourChar);
            } else {
                hexText = after;
                remaining = "";
            }
            hexResult.add(new JsonComponent(hexText).color(hexColour).serializeNoHex());
            hexMatcher = HEX_PATTERN.matcher(remaining);
        }
        if (hexResult.size() > 0) {
            if (!remaining.isEmpty()) {
                hexResult.add(new JsonComponent(remaining).serializeNoHex());
            }
            return hexResult;
        }

        // Regular translation, Supports vanilla colors
        return serializeNoHex();
    }

    private JsonObject serializeNoHex() {
        JsonObject result = new JsonObject();
        result.addProperty("text", this.text);
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

    public JsonMessage asJsonMessage() {
        return new JsonMessage().append(this);
    }

}