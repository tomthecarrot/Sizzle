package com.carrotcorp.sizzle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.Chartboost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Sizzle Game
 * by Thomas Suarez, Chief Engineer @ CarrotCorp.
 */
public class MainActivity extends Activity {
    private Music music; // music player object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init & play music
        music = new Music(this);

        try {
            // Get raw txt file for chartboost info
            InputStream inputStream = getResources().openRawResource(R.raw.chartboost);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String appId = reader.readLine();
            String appSignature = reader.readLine();

            // Init Chartboost ads
            Chartboost.startWithAppId(this, appId, appSignature);
            Chartboost.onCreate(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play(View v) {
        music.allowPlay();

        // Launch level picker
        Intent intent = new Intent(MainActivity.this, LevelPicker.class);
        startActivity(intent);
    }

    public void tutorial(View v) {
        music.allowPlay();

        // Launch Tutorial activity
        Intent intent = new Intent(MainActivity.this, Tutorial.class);
        startActivity(intent);
    }

    public void opensource(View v) {
        // Get raw txt file
        InputStream inputStream = getResources().openRawResource(R.raw.opensource);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        String msg = new String();
        try {
            while ((line = reader.readLine()) != null) {
                msg += line;
                msg += "\n";
            }
        }
        catch (IOException e) {}

        // Show alert
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Open Source");
        alert.setMessage(msg);
        alert.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        Chartboost.onStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Chartboost.onStop(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Chartboost.onDestroy(this);
    }

    @Override
    public void onBackPressed() {
        // If an interstitial is on screen, close it.
        if (Chartboost.onBackPressed())
            return;
        else
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Chartboost.onResume(this);

        // Cache ad for game end
        Chartboost.onCreate(this);
        Chartboost.cacheInterstitial(CBLocation.LOCATION_GAMEOVER);

        // Play music again
        music.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        Chartboost.onPause(this);

        // Stop playing music
        music.stop();
    }

}
