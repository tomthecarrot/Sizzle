package com.carrotcorp.sizzle;

import android.app.Activity;
import android.media.MediaPlayer;

/**
 * Sizzle Game
 * by Thomas Suarez, Chief Engineer @ CarrotCorp.
 */
public class Music {
    private Activity activity; // calling activity
    private static MediaPlayer mp; // music player
    private static boolean shouldStop = true; // should the music stop when requested ?

    public Music(MainActivity activity) {
        this.activity = activity;
    }

    public void start() {
        if (mp != null) { // if already created & initialized
            if (!mp.isPlaying()) { // if not playing anymore
                create(); // then restart playing
            }
        }
        else { // not yet created, so create it.
            create();
        }
    }

    public void create() {
        // Create & start music
        mp = MediaPlayer.create(activity, R.raw.bgmusic);
        mp.setLooping(true);
        mp.start();
    }

    public static void stop() {
        // Stop music (unless override is set)
        if (shouldStop) {
            mp.reset();
        }
    }

    public static void allowPlay() { // temporary music override while switching activities
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    shouldStop = false;
                    Thread.sleep(300);
                    shouldStop = true;
                }
                catch (InterruptedException e) {}
            }
        };
        new Thread(r).start();
    }
}
