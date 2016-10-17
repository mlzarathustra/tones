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

    public void setBufSize(int bufSize) {
        if (samples == null || samples.length != bufSize) samples=new short[bufSize];
    }
    public void setSampleRate(int sr) { sampleRate = sr; }

    public void setFreq(double freq) { this.freq = freq; }

    abstract short[] nextBuf();
    abstract void reset();

    static HashMap<String,Class> concreteClasses=new HashMap<>();
    static void register(String key, Class c) {
        key=key.toLowerCase().replaceAll("[^a-z]","");
        concreteClasses.put(key, c);
    }

    public static ArrayList getKeys() {  // should be CharSequence
        ArrayList<String> list=new ArrayList<>(concreteClasses.keySet());
        Collections.sort(list);
        return list;
    }

    static {
        WaveGen.register("sine", SineWaveGen.class);
        WaveGen.register("square", SquareWaveGen.class);
    }

}
