package com.carrotcorp.sizzle;

import android.graphics.Point;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Sizzle Game
 * by Thomas Suarez, Chief Engineer @ CarrotCorp.
 */
public class Dot {
    private Game game;
    int dotId = 1;

    public enum DotType {
        NORMAL, EMOTICON
    }

    public Dot(Game game) {
        this.game = game;
    }

    public void setDotId(DotType type) {
        this.dotId = type.ordinal()+1;
    }

    /**
     * Generates random coordinates for the Dot in the layout.
     * @return Point object with random x / y coordinates
     */
    public Point randomCoords() {
        int x = game.r.nextInt(game.maxX);
        int y = game.r.nextInt(game.maxY);

        //Log.i("Sizzle", "x: " + x + ", y: " + y);

        Point p = new Point(x,y);
        return p;
    }

    /**
     * Generates a random drift amount for the Dot.
     * @param range Range of the random generation (-range to 0 to range). Can be negative.
     * @return The resulting random integer
     */
    public int randomDrift(int range) {
        int result = game.r.nextInt(range); // generate random integer
        boolean neg = game.r.nextBoolean(); // should it be a negative result?
        result = neg ? -result : result; // change to negative if it should be (based on above boolean)

        return result;
    }

    /**
     * Spawns a Dot with a Random position.
     */
    public void spawn() {
        // Generate random coordinate
        Point p = randomCoords();

        spawn(p.x, p.y);
    }

    /**
     * Spawns a Dot at the specified position.
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void spawn(final int x, final int y) {
        // Create ImageView from drawable resource
        int resId = Helper.getDrawableId(game, "dot"+dotId+"norm");
        ImageView dot = new ImageView(game);
        dot.setImageResource(resId);
        dot.setLayoutParams(game.lp);

        // Set to specified coordinates
        dot.setX(x);
        dot.setY(y);

        // Set Dot state tags
        dot.setTag(R.id.dotTouchState, false);
        dot.setTag(R.id.dotBurnState, false);

        // Init gravity on this dot
        gravity(dot);

        // Init touch listener on this dot
        dot.setTag(false); // user is not currently touching the dot
        touchy(dot);

        // Add to dot array
        game.dots.add(dot);

        // Add to main layout
        game.layout.addView(dot);

        // Gradually fade in
        Animation fadeIn = AnimationUtils.loadAnimation(game, R.anim.fade_in);
        fadeIn.setFillAfter(true);
        dot.startAnimation(fadeIn);

        // Update number of dots
        game.numDotsLeft++;
        game.numDotsTotal++;
    }

    /**
     * Same as above, but with a time delay before spawning.
     * @param delay Time (in ms) to wait before spawning the Dot.
     */
    public void spawn(final int x, final int y, final int delay) {
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                spawn(x, y);
            }
        };
        new Handler().postDelayed(r, delay);
    }

    /**
     * Checks whether the Dot is near fire. Acts accordingly.
     */
    public void checkFire(final ImageView dot) {
        boolean burning = (Boolean) dot.getTag(R.id.dotBurnState);
        if (!burning && dot.getY() > game.fireLimit) {
            burn(dot);
        }
    }

    /**
     * Burns up the Dot.
     */
    public void burn(final ImageView dot) {
        // Prevent more burn calls, which WOULD mess things up!
        dot.setTag(R.id.dotBurnState, true);

        Runnable r = new Runnable() {
            @Override
            public void run() {
                int millis = 250; // crossfade duration

                // Crossfade white-to-red
                Helper.setImageDrawableWithFade(game, dot, "dot"+dotId+"burn", millis);

                // Wait for crossfade
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException e) {}

                // Vibrate device
                Vibrator v = (Vibrator) game.getSystemService(game.VIBRATOR_SERVICE);
                v.vibrate(100);

                // Fade out completely
                final Animation fadeOut = AnimationUtils.loadAnimation(game, R.anim.fade_out);
                fadeOut.setFillAfter(true);

                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        dot.setAlpha(0f);
                        dot.startAnimation(fadeOut);
                    }
                };
                game.runOnUiThread(r);

                // Decrement numDots variable
                game.numDotsLeft--;
                game.updateScore(-10);
                //Log.d("Sizzle", "numDotsLeft: " + game.numDotsLeft + ", score: " + game.score);

                // Remove Dot view entirely
                game.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        game.layout.removeView(dot);
                    }
                });
            }
        };
        new Thread(r).start();
    }

    /**
     * Initializes and renders gravity on the given Dot.
     */
    public void gravity(final ImageView dot) {
        Runnable r = new Runnable() {
            @Override
            public void run() {

                Runnable ui = new Runnable() {
                    @Override
                    public void run() {
                        int driftX = randomDrift(5); // px drift
                        float x = dot.getX() + driftX;
                        float y = dot.getY();
                        y += game.time * 0.5; // update y value

                        //dot.setX(x);
                        dot.setY(y);

                        checkFire(dot); // check if close to fire
                    }
                };

                while (game.gameRunning) { // loop until game over
                    try {
                        // Not being touched by user or being burned up, proceed...
                        if (((Boolean) dot.getTag(R.id.dotTouchState)) == false || ((Boolean) dot.getTag(R.id.dotBurnState)) == false) {
                            game.runOnUiThread(ui);
                        }

                        Thread.sleep(10); // 10ms wait
                    }
                    catch (InterruptedException e) {}
                }

            }
        };
        new Thread(r).start();
    }

    /**
     * Generates upward inertia on the given Dot.
     */
    public void inertia(final ImageView dot) {
        Runnable r = new Runnable() {
            float inertiaTime = 0; // how many 10ms intervals inertia has been going for

            @Override
            public void run() {
                final float destY = dot.getY() - 200; // destination Y value

                Runnable ui = new Runnable() {
                    @Override
                    public void run() {
                        float y = dot.getY();
                        y -= 10 * (inertiaTime / 10);
                        dot.setY(y);
                    }
                };

                try {
                    while (dot.getY() > destY) {
                        game.runOnUiThread(ui);

                        Thread.sleep(10);
                        inertiaTime++;
                    }
                }
                catch (InterruptedException e ) {}
            }
        };
        new Thread(r).start();

        dot.setTag(false); // release for gravity
    }

    /**
     * Listens & responds to touches and touch movement on the dot.
     */
    public void touchy(final ImageView dot) {
        dot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                boolean burning = (Boolean) dot.getTag(R.id.dotBurnState);
                if (burning) {
                    // Dot is being burned up, so disable touches.
                    return false;
                }

                int a = e.getAction(); // get movement action

                if (a == MotionEvent.ACTION_DOWN) { // down
                    dot.setTag(true);
                }
                else if (a == MotionEvent.ACTION_UP) { // release
                    //dot.setTag(false);
                    inertia(dot);
                }
                else if (a == MotionEvent.ACTION_MOVE) {
                    // New X/Y position, based on new anchor points
                    float newX = e.getRawX();
                    float newY = e.getRawY();
                    newX -= game.dotSize2;
                    newY -= game.dotSize2;

                    // Set new position
                    dot.setX(newX);
                    dot.setY(newY);
                }

                return true;
            }
        });
    }
    
}
