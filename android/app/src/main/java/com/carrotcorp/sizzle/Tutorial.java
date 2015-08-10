package com.carrotcorp.sizzle;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Sizzle Game
 * by Thomas Suarez, Chief Engineer @ CarrotCorp.
 */
public class Tutorial extends Activity {
    int bgNum = 1; // counter for current background image
    ImageView bgView; // background image view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up view
        setContentView(R.layout.activity_tutorial);
        bgView = (ImageView) findViewById(R.id.bgView);
    }

    public void next(View v) {
        bgNum++; // increment counter

        // Return to main screen if at end
        if (bgNum > 4) {
            finish();
            return;
        }

        // Crossfade background images
        String drawableName = ("tut" + bgNum);
        Helper.setImageDrawableWithFade(this, bgView, drawableName, 250);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Stop playing music
        Music.stop();
    }

}
