package net.miles_beyond.tones.synth;


import java.util.ArrayList;
import java.util.HashMap;


/**
 * One step (state) of the envelope.  rate is 1..100; destLevel is 0..1
 * <p>
 *
 * Assuming a sample rate of 44100, the rate will range between
 * 1 sample and about 23 seconds (from the formula 1/rate**3
 * in setRate() below)
 * <p>
 *
 * 1 is the quickest rate, 100 the slowest.
 *
 */
class Step {
    int rate;
    double destLevel, delta;

    Step(int r, double l) {
        rate=Math.max(1, Math.min(100,r));
        destLevel=Math.max(0,Math.min(1,l));
    }

    //   todo - make sure this gets called every time we enter a new step
    void setRate(int r, double curLevel) {
        rate=r;
        delta = 1.0 / (double)(rate*rate*rate);
        if (destLevel < curLevel) delta = -delta;
    }
}

/**
 * Env is basically a state machine. The current state is
 * represented by stepIdx
 * <p>
 *
 * The first step is triggered by the attack.
 * <p>
 *
 * When we reach (or pass) the destLevel of a step, we move on
 * to the next step.
 * <p>
 *
 * The destLevel of the last element of steps is the sustain,
 * held until the noteOFF triggers the release step.
 *
 */
class Env {
    /**
     stepIdx range: 0..steps.size() inclusive
         < steps.size() means it's an index into steps
         == steps.size() indicates the release
    */
    private int stepIdx = 0;

    double curLevel;  // range: 0 to 1

    private ArrayList<Step> steps = new ArrayList<Step>();
    private Step release=new Step(0,0);

    public Step currentStep() {
        if (steps.size() == 0) return release;
        if (stepIdx < 0) {
            stepIdx=0; return steps.get(0);
        }
        else if (stepIdx>= steps.size()) {
            stepIdx=steps.size(); return release;
        }
        return steps.get(stepIdx);
    }

    public Step nextStep() {
        stepIdx++;
        return currentStep(); // will adjust into range
    }

    /**
     * Expects pairs of integers alternating: rate,level;
     * rate is 1-100, level is 0-100. Rate will range from
     * one sample width to about 23 seconds, with 1 being
     * the quickest.
     * <p>
     *
     * The level is (obviously) a percent of full volume.
     *
     */
    Env(Integer... params) {
        for (int idx=0; idx<params.length-1; idx+=2) {
            steps.add(new Step(params[idx],((double)params[idx+1])/100.0));
        }
        release = steps.remove(steps.size()-1); // the last step is the release



    }

}

public class EnvGen {

    static HashMap<String,Env>envs = new HashMap<>();

    static {
        envs.put("flat", new Env(3, 1, 0, 1, 3, 0));
        envs.put("clavier", new Env(0,1, 100,50, 10,0));
        envs.put("fade", new Env(50,1, 25,50, 15,0));
    }

    Env env;

    void setEnv(String envKey) {
        env=envs.get(envKey);
        if (env == null) env=envs.get("flat");
    }


    public void noteON() {
        // todo -implement
    }

    public void noteOFF() {
        // todo - implement
    }

    public double nextVal() {
        // todo - implement
        return 1.0;
    }




}
