package net.miles_beyond.tones.synth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static net.miles_beyond.tones.Util.keyStrip;

public abstract class WaveGen {
    static int DEFAULT_SAMPLE_RATE=44100; // per second
    static double DEFAULT_FREQ=440;       // hz

    short[] samples;
    double freq = 440;
    int amp = 10000, sampleRate=44100;

    private String waveKey;

    void setBufSize(int bufSize) { // typically: 4104
        if (samples == null || samples.length != bufSize) samples=new short[bufSize];
    }
    void setSampleRate(int sr) { sampleRate = sr; }

    void setFreq(double freq) { this.freq = freq; }

    abstract short[] nextBuf();
    abstract void reset();

    //  Manage subclasses (the concrete wave generators)
    static HashMap<String,Class> concreteClasses=new HashMap<>();
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
            wg.setWaveKey(key);
            return wg;
        }
        catch (Exception ex) {
            return new SineWaveGen();
        }
    }
    protected void setWaveKey(String key) { waveKey =keyStrip(key); }
    String getWaveKey() { return waveKey; }

    static {
        //
        WaveGen.register("sine", SineWaveGen.class);
        WaveGen.register("square", SquareWaveGen.class);
        WaveGen.register("saw", SawWaveGen.class);
        WaveGen.register("pwm", PWMWaveGen.class);
        WaveGen.register("mellow", CompositeWaveGen.class);
        WaveGen.register("bell", CompositeWaveGen.class);
        WaveGen.register("organ", CompositeWaveGen.class);
    }



}
