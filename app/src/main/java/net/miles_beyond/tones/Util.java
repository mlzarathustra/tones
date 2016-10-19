package net.miles_beyond.tones;

public class Util {
    public static String keyStrip(String s) {
        return s.toLowerCase().replaceAll("[^a-z]","");
    }
}
