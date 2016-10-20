package net.miles_beyond.tones;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import net.miles_beyond.tones.synth.ToneGen;
import net.miles_beyond.tones.synth.WaveGen;
import static net.miles_beyond.tones.Util.keyStrip;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    boolean DB=false;

    boolean sharps=false;
    boolean hold=false;
    double baseFreq=220;
    ToneGen toneGen=new ToneGen();
    HashMap<Button,Note> noteMap=new HashMap<>();

    Button pressedButton;

    private void addNotes(LinearLayout layout) {
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
                    if (DB) System.out.println(event);
                    //System.out.println("onTouch: "+n);
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            if (hold && toneGen.isPlaying()) noteOFF();
                            else noteON((Button) v);
                            break;

                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            if (!hold) noteOFF();
                            break;
                    }
                    return false;
                }
            });
            noteMap.put(b,n);
        }
    }

    private void restoreSavedSettings() {
        SharedPreferences p=getPreferences(0);
        hold = p.getBoolean("hold", false);
        setSharps(p.getBoolean("sharps", false));
        baseFreq = p.getFloat("octave", 220);
        toneGen.setWaveGen(p.getString("wave","mellow"));
        toneGen.setEnvGen(p.getString("env","fade"));
    }

    private void saveSettings() {
        SharedPreferences p=getPreferences(0);
        SharedPreferences.Editor e=p.edit();
        e.putBoolean("hold",hold);
        e.putBoolean("sharps",sharps);
        e.putFloat("octave",(float)baseFreq); // N.B. should we restrict to int?
        e.putString("wave",toneGen.getWaveKey());
        e.putString("env", toneGen.getEnvKey());
        e.apply();
        System.out.println("Settings saved.");
    }

    private void alignUIWithSettings() {
        //  hold
        //
        CheckBox holdCheckBox=(CheckBox) findViewById(R.id.hold);
        holdCheckBox.setChecked(hold);

        // sharps
        //
        CheckBox sharpsCheckBox=(CheckBox) findViewById(R.id.sharps);
        sharpsCheckBox.setChecked(sharps);

        //  octave
        //
        Spinner octaveSpinner=(Spinner) findViewById(R.id.octave);
        String octaveStr=""+(int)baseFreq;
        for (int pos=0; pos<octaveSpinner.getCount(); ++pos) {
            String item=octaveSpinner.getItemAtPosition(pos).toString();
            //System.out.println(item);
            if (octaveStr.equals(item)) {
                octaveSpinner.setSelection(pos);
                break;
            }
        }
        octaveSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String octSelected=parent.getItemAtPosition(position).toString();
                if (DB) System.out.println("octave selected: "+parent.getItemAtPosition(position));
                try {
                    baseFreq = Double.parseDouble(octSelected);
                    noteREPLAY();
                }
                catch (Exception ex) {
                    System.out.println("Setting octave: "+ex);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });


        //  wave
        //

        Spinner waveSpinner=(Spinner) findViewById(R.id.wave);
        /*
        //   todo - for some reason, using the ArrayAdapter causes a gap above.
        //          neither debugging nor using the hierarchy viewer helped.
        //          for now, the values are in strings.xml
        //
        ArrayAdapter<String> waveList=
                new ArrayAdapter<>(this,
                        R.layout.support_simple_spinner_dropdown_item,
                        WaveGen.getKeys());
        try {
            waveSpinner.setAdapter(waveList);
        }
        catch (NullPointerException ex) {
            System.err.println("Error setting wave list " + ex);
        }
        */

        String waveKey=toneGen.getWaveKey();
        for (int pos=0; pos<waveSpinner.getCount(); ++pos) {
            if (keyStrip(waveSpinner.getItemAtPosition(pos).toString()).equals(waveKey)) {
                waveSpinner.setSelection(pos);
                break;
            }
        }

        waveSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String waveSelected = parent.getItemAtPosition(position).toString();
                if (DB) System.out.println("wave selected: "+waveSelected);
                Button pb=pressedButton;
                if (pb != null) noteOFF();
                toneGen.setWaveGen(keyStrip(waveSelected));
                if (pb != null) noteON(pb);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });


        //   envelope
        //
        Spinner envSpinner=(Spinner) findViewById(R.id.env);
        String envKey=keyStrip(toneGen.getEnvKey());

        for (int pos=0; pos<envSpinner.getCount(); ++pos) {
            if (keyStrip(envSpinner.getItemAtPosition(pos).toString()).equals(envKey)) {
                envSpinner.setSelection(pos);
                break;
            }
        }
         envSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 String envSelected = parent.getItemAtPosition(position).toString();
                 System.out.println("env selected: "+envSelected);
                 toneGen.setEnvGen(keyStrip(envSelected));

                 // todo - complete implementation
                 // copy current level from toneGen.envGen?
             }

             @Override
             public void onNothingSelected(AdapterView<?> parent) { }
         });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        LinearLayout layout = (LinearLayout) findViewById(R.id.keys);

        addNotes(layout);

        restoreSavedSettings();
        alignUIWithSettings();

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
        noteOFF();
        toneGen.pauseAudio();
        saveSettings();
    }

    void alert(String title, String msg) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(msg)
                .setNeutralButton("close",null).show();
        // why does this give an error when you close the alert?
        // apparently, it's a common Android bug
    }

    void noteON(Button b) {
        Note n=noteMap.get(b);
        toneGen.noteON(baseFreq * n.freq);
        pressedButton=b;
        pressedButton.setBackgroundColor(Color.GRAY);
    }

    void noteOFF() {
        toneGen.noteOFF();
        if (pressedButton != null) {
            Note n = noteMap.get(pressedButton);
            pressedButton.setBackgroundColor(n.white ? Color.WHITE : Color.BLACK);
            pressedButton = null;
        }
    }

    void noteREPLAY() {
        if (pressedButton != null) {
            Button pb = pressedButton; // noteOFF will set it to null
            noteOFF(); noteON(pb);
        }

    }

    private void setSharps(boolean s) {
        sharps = s;
        for (Button b : noteMap.keySet()) {
            Note n=noteMap.get(b);
            b.setText(sharps?n.sharpName:n.flatName);
        }
    }
    public void setSharps(View v) {
        setSharps(((CheckBox)v).isChecked());
    }

    public void setHold(View v) {
        hold=((CheckBox)v).isChecked();
        if (!hold) noteOFF();
    }


}
