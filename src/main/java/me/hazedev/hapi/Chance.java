package me.hazedev.hapi;

public class Chance {

    public static boolean willHappen(float chance) {
        return Math.random() < chance;
    }

}
