package net.miles_beyond.tones.synth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public abstract class WaveGen {
    static int DEFAULT_SAMPLE_RATE=44100; // per second
    static double DEFAULT_FREQ=440;       // hz

    short[] samples;
    double freq = 440;
    int amp = 10000, sampleRate=44100;

    private String waveLabel;

    void setBufSize(int bufSize) {
        if (samples == null || samples.length != bufSize) samples=new short[bufSize];
    }
    void setSampleRate(int sr) { sampleRate = sr; }

    void setFreq(double freq) { this.freq = freq; }

    abstract short[] nextBuf();
    abstract void reset();

    //  Manage subclasses (the concrete wave generators)
    static HashMap<String,Class> concreteClasses=new HashMap<>();
    public static String keyStrip(String s) {
        return s.toLowerCase().replaceAll("[^a-z]","");
    }
    private static void register(String key, Class c) {
        concreteClasses.put(keyStrip(key), c);
    }

    public static ArrayList<String> getKeys() {  // should be CharSequence
        ArrayList<String> list=new ArrayList<>(concreteClasses.keySet());
        Collections.sort(list);
        return list;
    }

    static WaveGen getWaveGen(String key) {
        Class wgClass = concreteClasses.get(keyStrip(key));
        try {
            WaveGen wg = (WaveGen) wgClass.newInstance();
            wg.setWaveLabel(key);
            return wg;
        }
        catch (Exception ex) {
            return new SineWaveGen();
        }
    }
    private void setWaveLabel(String key) { waveLabel=keyStrip(key); }
    String getWaveLabel() { return waveLabel; }

    static {
        // The labels are repeated in the concrete classes
        // as static elements. Otherwise, the class load could
        // deadlock here.  Is there a good way to do this without
        // repeating the labels?
        WaveGen.register("sine", SineWaveGen.class);
        WaveGen.register("square", SquareWaveGen.class);
    }



}
