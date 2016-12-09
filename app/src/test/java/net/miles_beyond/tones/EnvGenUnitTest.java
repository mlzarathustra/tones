package net.miles_beyond.tones;

import net.miles_beyond.tones.synth.EnvGen;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *  displays only - doesn't assert
 *  todo - set up assertions from observed behavior
 */
public class EnvGenUnitTest {

    void show(double level) {  // level is from 0 to 1
        int stars =(int)( 80 * level );
        for (int i=0; i<stars; ++i) System.out.print("*");
        System.out.println("");
    }


    @Test
    public void envGenWorks() throws Exception {

        EnvGen eg=new EnvGen("test");
        for (int i=0; i<10; ++i) show(eg.nextVal());
        // should be 0 before noteON
        assertEquals(0, eg.nextVal(), 0);

        eg.noteON();
        for (int i=0; i<10; ++i) show(eg.nextVal());
        assertEquals(0.5, eg.nextVal(), 0);
        eg.noteOFF();
        for (int i=0; i<30; ++i) show(eg.nextVal());
        assertEquals(0, eg.nextVal(), 0);


        eg=new EnvGen("test1");
        eg.noteON();
        for (int i=0; i<10; ++i) show(eg.nextVal());
        eg.noteOFF();
        for (int i=0; i<30; ++i) show(eg.nextVal());

        eg=new EnvGen("test1");
        eg.noteON();
        for (int i=0; i<10; ++i) show(eg.nextVal());
        eg.noteOFF();
        for (int i=0; i<10; ++i) show(eg.nextVal());
        eg.noteON();
        for (int i=0; i<30; ++i) show(eg.nextVal());
        eg.noteOFF();
        for (int i=0; i<30; ++i) show(eg.nextVal());


        eg.noteON();
        for (int i=0; i<75; ++i) show(eg.nextVal());
        assertEquals(0.75, eg.nextVal(), 0);
        eg.noteOFF();
        for (int i=0; i<30; ++i) show(eg.nextVal());
        assertEquals(0, eg.nextVal(), 0);
    }
}