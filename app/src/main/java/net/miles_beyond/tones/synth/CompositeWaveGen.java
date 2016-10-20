package net.miles_beyond.tones.synth;

import java.util.ArrayList;

class CompositeWaveGen extends WaveGen {

    static double twoPI=Math.PI*2.0;
    private double phase=0;
    private int scaledAmp;

    @Override
    void setFreq(double freq) {
        super.setFreq(freq);

        //scaledAmp = amp;

        scaledAmp = (int)(amp * (1.0 + 55.0/(2*freq) ));
        // pitch scaling, to help out the bass notes
    }

    @Override
    public void reset() { phase=0; }

    // a crude inefficient (but easy to initialize) hashmap
    //
    private String[] labels={ "mellow","odd","bell", "organ" };
    private double[][] waves= {
            {1,1, 2,2, 3,3},
            {1,1, 2,2, 6,3, 14,3},
            {1,1, 2,2, 11,3, 14,3, 17,3},
            {1,1, 2,2, 3,3, 4,2, 8,2, 12,3}
    };

    private double[] wave=waves[0];

    @Override
    protected void setWaveKey(String key) {
        for (int i=0; i<labels.length; ++i) {
            if (key.equals(labels[i])) wave=waves[i];
        }
    }


    @Override
    synchronized short[] nextBuf() {
        //System.out.print("*");

        while (phase > twoPI) phase -= twoPI;
        for (int i = 0; i< samples.length; ++i) {
            double sum=0;
            for (int ov=0; ov<wave.length-1; ov+=2) {
                sum += Math.sin(phase*wave[ov])/wave[ov+1];
            }

            samples[i] = (short) (scaledAmp * sum);
            phase += twoPI * freq / sampleRate;
        }
        //if (loops<3) { loops++; System.out.println(Arrays.toString(samples)); }
        return samples;
    }



}
