package me.hazedev.hapi.command;

public class Flag {

    private final String identifier;
    private final String description;

    public Flag(String identifier, String description) {
        this.identifier = identifier;
        this.description = description;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDescription() {
        return description;
    }
}
