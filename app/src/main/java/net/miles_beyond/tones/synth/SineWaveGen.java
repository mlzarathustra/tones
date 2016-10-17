package net.miles_beyond.tones.synth;

public class SineWaveGen extends WaveGen {

    double phase=0, twoPI=Math.PI*2.0;

    SineWaveGen() { this(1,DEFAULT_SAMPLE_RATE); }
    SineWaveGen(int bufSize, int sampleRate) { this(bufSize, sampleRate, DEFAULT_FREQ); }

    SineWaveGen(int bufSize, int sampleRate, double freq) {
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




}