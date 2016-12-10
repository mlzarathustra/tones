package net.miles_beyond.tones.synth;

/**
 * Generate a plain square wave, with duty cycle of 0.5
 */
class SquareWaveGen extends WaveGen {

    private double phase = 0; // range: 0-1
    private double dutyCycle = 0.5;

    @Override
    short[] nextBuf() {
        for (int i = 0; i< samples.length; ++i) {
            while (phase > 1) phase -= 1;
            samples[i] = (short) ((phase>dutyCycle)?amp:-amp);
            phase += freq / sampleRate;
        }
        //if (loops<3) { loops++; System.out.println(Arrays.toString(samples)); }
        return samples;
    }

    @Override
    void reset() {
        phase = 0;
    }

}
