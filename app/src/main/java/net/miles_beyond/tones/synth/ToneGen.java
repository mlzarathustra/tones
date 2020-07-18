package net.miles_beyond.tones.synth;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.audiofx.PresetReverb;
import android.os.Handler;
import android.os.Message;
import android.widget.LinearLayout;


/**
 * This class manages the Wave Generator and the Envelope Generator classes,
 * acting as the facade facing the Android's audio system to encapsulate the
 * details of the WaveGen and EnvGen classes.
 *
 */
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


    /**
     * Start the playback thread that loops, sending the samples to the
     * Android's Audio Track.
     */
    private synchronized void start() {
        playThread = new Thread(){

            public void run() {
                playing = true;  // to exit the thread cleanly, we use a flag.
                setPriority(Thread.MAX_PRIORITY);

                if (DB) System.out.println("buffer size is "+bufSize);
                waveGen.setBufSize(bufSize);
                waveGen.reset();
                waveGen.setSampleRate(sampleRate);

                audTrack.play();

                //
                //   The MAIN loop
                //
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

                    //  get the next set of wave samples from waveGen,
                    //  then multiply each by the next set of envelope
                    //  values from envGen
                    //
                    short[] next= waveGen.nextBuf();
                    for (int i=0; i<next.length; ++i) {
                        next[i] *= envGen.nextVal();
                    }

                    //  send the samples to the Android
                    //  I assume this blocks if needed
                    //
                    int rs=audTrack.write(next,0,next.length);
                    if (DB && loops<100) {
                        System.out.println("audTrack.write() rs="+rs); loops++;
                    }
                }
            }

        };
        playThread.start();
    }

    /**
     * Shut down the audio track for this application,
     * and tell the playback thread to exit.
     */
    private synchronized void stop() {
        if (audTrack.getState() == AudioTrack.STATE_INITIALIZED) {
            // prevent click when note is released:
            audTrack.setStereoVolume(0,0);  // setVolume requires API 21

            audTrack.stop();
            playing = false;
            try { playThread.join(); } catch (Exception ignore) { }
        }
    }

    /**
     * This is the method the button's event handler calls
     * @see net.miles_beyond.tones.MainActivity, addNotes(LinearLayout)
     * @param freq - the frequency of the pitch to play
     */
    public void noteON(double freq) {
        waveGen.setFreq(freq);
        if (!playing) start();
        audTrack.setStereoVolume(1,1);
        envGen.noteON();

    }

    /**
     * for the button's event handler
     * @see net.miles_beyond.tones.MainActivity, addNotes(LinearLayout)
     */
    public void noteOFF() {
        //stop();
        envGen.noteOFF();
    }

    public boolean isPlaying() { return playing; }


}
