package com.carrotcorp.sizzle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chartboost.sdk.Chartboost;

import java.util.ArrayList;
import java.util.Random;

/**
 * Sizzle Game
 * by Thomas Suarez, Chief Engineer @ CarrotCorp.
 */
public class Game extends Activity {
    protected final int dotSize = 250; // width/height of each dot.
    protected int dotSize2 = 0; // half of width/height of each dot. used for new anchor point, see touchy() in Dot class.
    protected int nextLevel = 2;
    protected int maxDots = 0; // maximum amount of dots for this game. -1 = unlimited.
    protected int gameTime = 0; // seconds of total game time
    protected int time = 0; // how long has the game been running for?
    protected boolean gameRunning = false; // is the game currently running?
    protected int score = 0; // current score in-game
    protected int numDotsTotal = 0; // number of dots ever on screen (during this game)
    protected int numDotsLeft = 0; // number of dots currently on screen
    protected int maxX = 0; // maximum X coordinate value for device screen size
    protected int maxY = 0; // maximum Y coordinate value for device screen size
    protected int fireLimit = 0; // minimum Y coordinate value for fire burn-up
    protected ViewGroup layout; // UI layout of this Activity
    protected TextView scoreView; // UI score textview
    protected TextView timeView; // UI countdown timer textview
    protected Random r; // object that generates Random numbers
    protected LinearLayout.LayoutParams lp; // layout params for each Dot
    protected ArrayList<ImageView> dots; // all dots on screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Initialize game
        dotSize2 = dotSize / 2;
        int[] res = getScreenResolution();
        maxX = res[0] - dotSize;
        maxY = res[1] / 3; // 1/3 down.
        fireLimit = maxY * 2; // 2/3 down.
        layout = (ViewGroup) findViewById(android.R.id.content);
        scoreView = (TextView) findViewById(R.id.scoreView);
        timeView = (TextView) findViewById(R.id.timeView);
        r = new Random();
        lp = new LinearLayout.LayoutParams(dotSize, dotSize);
        dots = new ArrayList<>();

        // Configuration
        Intent intent = getIntent();
        nextLevel = intent.getIntExtra("nextLevel", 2);
        maxDots = intent.getIntExtra("maxDots", 0);
        gameTime = intent.getIntExtra("gameTime", 0);
        updateScore(maxDots*10);

        // Start game!
        startGame();
    }

    /**
     * Returns the screen resolution of the device.
     * @return int[]: [0] X [1] Y
     */
    private int[] getScreenResolution() {
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);

        int[] arr = new int[]{outSize.x, outSize.y};
        return arr;
    }

    /**
     * Starts the game.
     */
    private void startGame() {
        gameRunning = true;

        timeView.setText(gameTime + "s");

        readySetGo();
    }

    /**
     * Ends the game.
     */
    private void endGame(boolean cancel) {
        gameRunning = false;
        Music.allowPlay();

        // Cancel if requested
        if (cancel) {
            return;
        }

        // Edit currentLevel in shared preferences
        if (score > 0) {
            // Add bonus score based on this level (TODO)
            //bonusScoreForLevel();

            // Continue to next level
            SharedPreferences pref = getSharedPreferences("Sizzle", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("currentLevel", nextLevel);
            editor.apply();
        }

        // Transfer to GameOver activity
        Intent intent = new Intent(Game.this, GameOver.class);
        intent.putExtra("score", score);
        startActivity(intent);
        finish();
    }

    /**
     * Updates game score.
     * @param add How much score to add (can be negative)
     */
    protected void updateScore(int add) {
        score += add;

        Runnable r = new Runnable() {
            @Override
            public void run() {
                scoreView.setText(Integer.toString(score));
            }
        };
        runOnUiThread(r);
    }

    /**
     * Adds bonus score based on the current level.
     */
    protected void bonusScoreForLevel() {
        updateScore(nextLevel*10);
    }

    /**
     * Manages time-related game items.
     * @param gameTime Time (in seconds) until game is over
     */
    private void gameTicker(final int gameTime) {
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    Runnable spawnR = new Runnable() { // spawn runnable, run in UI thread via this one
                        @Override
                        public void run() {
                            //spawn(Dot.DotType.NORMAL);
                            spawn(Dot.DotType.EMOTICON);
                        }
                    };
                    Runnable timeR = new Runnable() {
                        @Override
                        public void run() {
                            int deltaTime = gameTime - time;
                            timeView.setText(deltaTime + "s");
                        }
                    };

                    while (time < gameTime && gameRunning) { // loop
                        if (numDotsTotal < maxDots) { // if maxDots has not been reached yet, spawn more.
                            runOnUiThread(spawnR);
                        }
                        if (numDotsLeft <= 0 && numDotsTotal > 0) { // game over!
                            endGame(false);
                        }

                        Thread.sleep(1000); // wait 1 second before next execution in this thread
                        time++;
                        runOnUiThread(timeR);
                    }

                    if (gameRunning) {
                        endGame(false);
                    }
                }
                catch (InterruptedException e) {}
            }
        };
        new Thread(r).start();
    }

    private void spawn(Dot.DotType type) {
        //add more here.. (different types)

        Dot dot = new Dot(this);
        dot.setDotId(type);
        dot.spawn();
    }

    /**
     * Prints "ready set go" to screen before starting game.
     */
    private void readySetGo() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                // TextView targets
                final TextView t1 = (TextView) findViewById(R.id.t_ready);
                final TextView t2 = (TextView) findViewById(R.id.t_set);
                final TextView t3 = (TextView) findViewById(R.id.t_go);
                final TextView[] views = { t1, t2, t3 };

                // Wait a little...
                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException e) {}

                // Animate each TextView, with a 0.5s delay after each animation.
                for (final TextView tv : views) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Fade in anim
                            final Animation fadeIn = AnimationUtils.loadAnimation(Game.this, R.anim.fade_in);
                            fadeIn.setDuration(250);
                            fadeIn.setFillAfter(true);

                            tv.setVisibility(View.VISIBLE);
                            tv.startAnimation(fadeIn);
                        }
                    });

                    try {
                        Thread.sleep(500);
                    }
                    catch (InterruptedException e) {}
                }

                // Fade out completely
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (final TextView tv : views) {
                            final Animation fadeOut = AnimationUtils.loadAnimation(Game.this, R.anim.fade_out);
                            fadeOut.setDuration(250);
                            fadeOut.setFillAfter(true);
                            
                            tv.startAnimation(fadeOut);
                        }
                    }
                });

                // Start game & dots
                gameTicker(gameTime);

                // Wait 0.5s before completely removing the TextViews from the main view hierarchy
                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException e) {}

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (final TextView tv : views) {
                            tv.setVisibility(View.GONE);
                        }
                    }
                });
            }
        };
        new Thread(r).start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        Chartboost.onPause(this);

        // Stop playing music
        Music.stop();

        // Cancel & End the game if it's running
        if (gameRunning) {
            endGame(true);
        }

        // Close Game activity
        finish();
    }

}
