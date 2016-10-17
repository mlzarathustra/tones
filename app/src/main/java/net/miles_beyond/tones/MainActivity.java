package net.miles_beyond.tones;

import android.graphics.Color;
import android.media.AudioManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import net.miles_beyond.tones.synth.ToneGen;
import net.miles_beyond.tones.synth.WaveGen;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    boolean sharps=false;
    boolean hold=false;
    double baseFreq=440;
    ToneGen toneGen=new ToneGen();
    HashMap<Button,Note> noteMap=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        LinearLayout layout = (LinearLayout) findViewById(R.id.keys);

        for (Note n : Note.notes) {

            Button b = new Button(this);
            //Button b = new Button(getApplication()); // different style

            b.setText(sharps ?n.sharpName:n.flatName);
            b.setAllCaps(false);
            b.setBackgroundColor(n.white?Color.WHITE:Color.rgb(10,10,10));
            b.setTextColor(n.white?Color.BLACK:Color.WHITE);
            layout.addView(b);

            b.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    System.out.println(event);
                    Note n=noteMap.get(v);
                    System.out.println("onTouch: "+n);
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            if (hold && toneGen.isPlaying()) toneGen.noteOFF();
                            else toneGen.noteON(baseFreq * n.freq);
                            break;

                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            if (!hold) toneGen.noteOFF();
                            break;
                    }
                    return false;
                }
            });
            noteMap.put(b,n);
        }


        //  TODO - restore from saved config

        //  Align config UI with settings
        //
        Spinner wave=(Spinner) findViewById(R.id.wave);
        ArrayAdapter<CharSequence> waveList=
                new ArrayAdapter<CharSequence>(this, R.layout.support_simple_spinner_dropdown_item,
                        WaveGen.getKeys());
        wave.setAdapter(waveList);

        CheckBox sharpsCheckBox=(CheckBox) findViewById(R.id.sharps);
        sharpsCheckBox.setChecked(sharps);

        CheckBox holdCheckBox=(CheckBox) findViewById(R.id.hold);
        holdCheckBox.setChecked(hold);

        Spinner octave=(Spinner) findViewById(R.id.octave);


        //
        // end align config UI

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

        //  TODO - save config
    }

    void alert(String title, String msg) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(msg)
                .setNeutralButton("close",null).show();
        // why does this give an error when you close the alert?
        // apparently, it's a common Android bug
    }

    public void setSharps(View v) {
        sharps =((CheckBox)v).isChecked();
        for (Button b : noteMap.keySet()) {
            Note n=noteMap.get(b);
            b.setText(sharps?n.sharpName:n.flatName);
        }
    }
    public void setHold(View v) {
        hold=((CheckBox)v).isChecked();
    }


}
