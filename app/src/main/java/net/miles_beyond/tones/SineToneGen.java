package net.miles_beyond.tones;

public class SineToneGen {
    static int DEFAULT_SAMPLE_RATE=44100; // per second
    static double DEFAULT_FREQ=440;       // hz

    short[] samples;
    double freq = 440, phase=0, twoPI=Math.PI*2.0;
    int amp = 10000, sampleRate=44100;

    int loops=3;


    SineToneGen() { this(1,DEFAULT_SAMPLE_RATE); }
    SineToneGen(int bufSize, int sampleRate) { this(bufSize, sampleRate, DEFAULT_FREQ); }

    SineToneGen(int bufSize, int sampleRate, double freq) {
        samples = new short[bufSize];
        this.sampleRate = sampleRate;
        this.freq = freq;
    }

    public void reset() { phase=0; }

    synchronized short[] nextBuf() {
        //System.out.print("*");

        for (int i = 0; i< samples.length; ++i) {
            samples[i] = (short) (amp * Math.sin(phase));
            phase += twoPI * freq / sampleRate;
        }
        //if (loops<3) { loops++; System.out.println(Arrays.toString(samples)); }
        return samples;
    }

    public void setFreq(double freq) { this.freq = freq; }
    public void setBufSize(int bufSize) {
        if (samples == null || samples.length != bufSize) samples=new short[bufSize];
    }
    public void setSampleRate(int sr) { sampleRate = sr; }

}
