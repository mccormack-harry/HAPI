package me.hazedev.hapi.quest;

public enum QuestPriority {

    HIGHEST(4),
    HIGH(3),
    NORMAL(2),
    LOW(1),
    LOWEST(0);

    final int value;

    QuestPriority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
