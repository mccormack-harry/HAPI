package me.hazedev.hapi.util;

public class Chance {

    public static boolean willHappen(float chance) {
        return Math.random() < chance;
    }

}
