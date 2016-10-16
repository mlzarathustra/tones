package net.miles_beyond.tones.synth;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class ToneGen {

    private boolean DB=false;

    private double freq=440;
    private int sampleRate = 44100;
    private Thread playThread;
    private boolean playing;
    private AudioTrack audTrack;
    private int bufSize;


    //  STUB
    private SineWaveGen waveGen = new SineWaveGen();


    public void resumeAudio() {
        bufSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        audTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,

                bufSize, AudioTrack.MODE_STREAM
        );
        //audTrack.setVolume((float)(audTrack.getMaxVolume() * 0.8));
        // requires api level >= 21 (it was 15 by default)
    }

    public void pauseAudio() {
        audTrack.release();
        audTrack = null;
    }


    private synchronized void start() {
        playThread = new Thread(){

            public void run() {
                playing = true;
                setPriority(Thread.MAX_PRIORITY);

                if (DB) System.out.println("buffer size is "+bufSize);
                waveGen.setBufSize(bufSize);
                waveGen.reset();
                waveGen.setSampleRate(sampleRate);

                audTrack.play();

                int loops=0;
                while (playing) {
                    short[] next= waveGen.nextBuf();
                    int rs=audTrack.write(next,0,next.length);
                    if (DB && loops<100) {
                        System.out.println("audTrack.write() rs="+rs); loops++;
                    }
                }
            }

        };
        playThread.start();
    }

    private synchronized void stop() {
        if (audTrack.getState() == AudioTrack.STATE_INITIALIZED) {
            audTrack.stop();
            playing = false;
            try { playThread.join(); } catch (Exception ignore) { }
        }
    }

    public void noteON(double freq) {
        waveGen.setFreq(freq);
        if (!playing) start();
    }

    public void noteOFF() {
        stop(); // XXX - should wait for release before stopping
    }


}
