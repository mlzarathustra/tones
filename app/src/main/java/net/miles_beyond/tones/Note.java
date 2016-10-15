package net.miles_beyond.tones;



public class Note {
    String flatName, sharpName;
    double freq;

    public static final Note[] notes={
            new Note("A","A",440),
            new Note("Bb","A#",466.16376150731924),
            new Note("B","B",493.8833012675353),
            new Note("C","C",523.2511306011972),
            new Note("Db","C#",554.3652619409356),
            new Note("D","D",587.3295358483853),
            new Note("Eb","D#",622.2539674441618),
            new Note("E","E",659.2551138105079),
            new Note("F","F",698.4564628821455),
            new Note("Gb","F#",739.9888454232688),
            new Note("G","G",783.9908719453846),
            new Note("Ab","G#",830.6093951790814)
    };

    Note(String fn, String sn, double f) { flatName=fn; sharpName=sn; freq=f; }

    public String toString() {
        return "Note {"+flatName+"; "+freq+" }";
    }

}
