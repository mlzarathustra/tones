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

import net.miles_beyond.tones.synth.ToneGen;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    boolean useFlatName=true;

    ToneGen toneGen=new ToneGen();


    HashMap<Button,Note> noteMap=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        LinearLayout layout = (LinearLayout) findViewById(R.id.keys);
        /*
        System.out.println("layout padding: bottom="+layout.getPaddingBottom()+
                "; top="+layout.getPaddingTop()+"; divider: "+layout.getDividerPadding()+
                "; showDividers is "+layout.getShowDividers()

        );
        // I guess system.out isn't available here
        */

        for (Note n : Note.notes) {
            Button b = new Button(getApplication());
            b.setText(useFlatName?n.flatName:n.sharpName);
            b.setAllCaps(false);
            //b.setMaxHeight(10);
            //b.setPadding(0,0,0,0); // no effect
            /*
            b.setLayoutParams(new ViewGroup.LayoutParams(
                    android.app.ActionBar.LayoutParams.WRAP_CONTENT,
                    android.app.ActionBar.LayoutParams.WRAP_CONTENT));
                    // thought this would make the buttons narrower. It didn't.
            */


            layout.addView(b);

            b.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    System.out.println(event);
                    Note n=noteMap.get(v);
                    System.out.println("onTouch: "+n);
                    //if (n != null) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                toneGen.noteON(n.freq);
                                break;

                            case MotionEvent.ACTION_UP:
                            case MotionEvent.ACTION_CANCEL:
                                toneGen.noteOFF();
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

    @Override
    public void onResume() {
        super.onResume();
        toneGen.resumeAudio();

    }

    @Override
    public void onPause() {
        super.onPause();
        toneGen.noteOFF();
        toneGen.pauseAudio();

    }

    void alert(String title, String msg) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(msg)
                .setNeutralButton("close",null).show();
        // why does this give an error when you close the alert?
        // apparently, it's a common Android bug
    }


}
