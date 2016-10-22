package net.miles_beyond.tones.synth;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.audiofx.PresetReverb;
import android.os.Handler;
import android.os.Message;

public class ToneGen {

    private boolean DB=false;

    private int sampleRate = 44100;
    private Thread playThread;
    private boolean playing;
    private AudioTrack audTrack;
    private int bufSize;


    private WaveGen waveGen = WaveGen.getWaveGen("sine");
    private EnvGen envGen = new EnvGen("organ");

    private Handler noteOffHandler;   //  ##GREY

    public void setWaveGen(String s) {
        waveGen = WaveGen.getWaveGen(s);
        waveGen.setBufSize(bufSize);
        waveGen.setSampleRate(sampleRate);
    }
    public void setEnvGen(String s) {
        envGen = new EnvGen(s);
    }

    public String getWaveKey() {
        if (waveGen == null) return "";
        return waveGen.getWaveKey();
    }

    public String getEnvKey() {
        return envGen.getEnvKey();
    }

    public void setNoteOffHandler(Handler h) { noteOffHandler=h; }

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

        try {
            PresetReverb reverb = new PresetReverb(0, 0); // can't create in simulation phone
            audTrack.attachAuxEffect(reverb.getId());
        }
        catch (Exception ignore) {}

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
                    if (envGen.isComplete()) {
                        audTrack.stop();
                        playing = false;
                        //System.out.println("UNGREY - here");  //  ##GREY
//                        noteOffHandler.sendMessage(Message.obtain(noteOffHandler));
                        noteOffHandler.sendMessage(noteOffHandler.obtainMessage());
                        break;

                    }
                    short[] next= waveGen.nextBuf();
                    for (int i=0; i<next.length; ++i) {
                        next[i] *= envGen.nextVal();
                    }
                    int rs=audTrack.write(next,0,next.length);
                    if (DB && loops<100) {
                        System.out.println("audTrack.write() rs="+rs); loops++;
                    }
                }
            }

        };
        playThread.start();
        // todo - wait for join, then return back to the display thread?
        //          so it can turn the note off when it's done sounding
        //          We can't hold up here, because the calling function
        //          has to do a few things to start playing after this.
    }

    private synchronized void stop() {
        if (audTrack.getState() == AudioTrack.STATE_INITIALIZED) {
            // prevent click when note is released:
            audTrack.setStereoVolume(0,0);  // setVolume requires API 21

            audTrack.stop();
            playing = false;
            try { playThread.join(); } catch (Exception ignore) { }
        }
    }

    public void noteON(double freq) {
        waveGen.setFreq(freq);
        if (!playing) start();
        audTrack.setStereoVolume(1,1);
        envGen.noteON();

    }

    public void noteOFF() {
        //stop();
        envGen.noteOFF();
    }

    public boolean isPlaying() { return playing; }


}
