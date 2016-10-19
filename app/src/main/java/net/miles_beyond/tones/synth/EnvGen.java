package net.miles_beyond.tones.synth;

import java.util.HashMap;

public class EnvGen {

    private static HashMap<String,Env>envs = new HashMap<>();

    static {
        try {
            envs.put("organ", new Env(3, 100, 0, 100, 3, 0));
            envs.put("clavier", new Env(0, 100, 100, 50, 10, 0));
            envs.put("fade", new Env(50, 100, 25, 50, 15, 0));
            envs.put("test", new Env(0, 100, 2, 50, 3, 0));
            envs.put("test1", new Env(2, 100, 3, 50, 3, 75, 3, 0));
        }
        catch (InstantiationException ex) {
            System.out.println("Creating envelopes: "+ex);
            System.exit(-1);
        }
    }

    private Env env;

    public EnvGen(String envKey) {
        env=envs.get(envKey);
        if (env == null) env=envs.get("organ");
    }


    public void noteON() { env.noteON(); }
    public void noteOFF() { env.noteOFF(); }
    public double nextVal() { return env.nextVal(); }
    boolean isComplete() { return env.isComplete(); }



}
