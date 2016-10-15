package net.miles_beyond.tones;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    boolean DB=false;
    boolean useFlatName=true;

    double freq=440;

    int sampleRate = 44100;
    Thread playThread;
    boolean playing;
    AudioTrack audTrack;
    HashMap<Button,Note> noteMap=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        LinearLayout layout = (LinearLayout) findViewById(R.id.keys);

        for (Note n : Note.notes) {
            Button b = new Button(getApplication());
            b.setText(useFlatName?n.flatName:n.sharpName);
            b.setAllCaps(false);
            /*
            b.setLayoutParams(new ViewGroup.LayoutParams(
                    android.app.ActionBar.LayoutParams.WRAP_CONTENT,
                    android.app.ActionBar.LayoutParams.WRAP_CONTENT));
                    // thought this would make the buttons narrower. It didn't.
            */


            layout.addView(b);
            /*
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note n=noteMap.get(v);
                    System.out.println(n);
                    playSound();
                }
            });
            */
            b.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    System.out.println(event);
                    Note n=noteMap.get(v);
                    System.out.println("onTouch: "+n);
                    //if (n != null) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                gen.setFreq(n.freq);
                                if (!playing) start();
                                break;

                            case MotionEvent.ACTION_UP:
                                stop();
                                break;
                        }
                    //}
                    return false;
                }
            });
            noteMap.put(b,n);
        }

        setVolumeControlStream(AudioManager.STREAM_MUSIC); // recommended
        // https://developer.android.com/training/managing-audio/volume-playback.html
    }

    SineToneGen gen=new SineToneGen();

    synchronized void start() {
        playThread = new Thread(){

            public void run() {
                playing = true;
                int bufSize = AudioTrack.getMinBufferSize(
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

                setPriority(Thread.MAX_PRIORITY);

                if (DB) System.out.println("buffer size is "+bufSize);
                gen.setBufSize(bufSize);
                gen.setSampleRate(sampleRate);

                audTrack.play();
                int loops=0;
                while (playing) {
                    short[] next=gen.nextBuf();
                    int rs=audTrack.write(next,0,next.length);
                    if (DB && loops<100) {
                        System.out.println("audTrack.write() rs="+rs); loops++;
                    }
                }
            }

        };
        playThread.start();
    }

    void alert(String title, String msg) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(msg)
                .setNeutralButton("close",null).show();
        // why does this give an error when you close the alert?
        // apparently, it's a common Android bug
    }

    synchronized void stop() {
        audTrack.stop();
        playing=false;
        try { playThread.join(); }
        catch (Exception ignore) { }
        audTrack.stop();
        audTrack.release();

    }
/*
    public void playSound() {
        if (playing) stop();
        else start();
        //alert("playback","playing is "+playing);
        if (DB) System.out.println(" >> playing is "+playing);
    }
*/


}
