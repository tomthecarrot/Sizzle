package com.carrotcorp.sizzle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Sizzle Game
 * by Thomas Suarez, Chief Engineer @ CarrotCorp.
 */
public class LevelPicker extends Activity {
    private final int totalLevels = 6; // total number of levels in Sizzle
    private int currentLevel = 1;
    private int maxDots = 0;
    private int gameTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_picker);

        // Lock future levels
        SharedPreferences pref = getSharedPreferences("Sizzle", Context.MODE_PRIVATE);
        currentLevel = pref.getInt("currentLevel", 1);
        lockLevels();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Stop playing music
        Music.stop();

        // Close LevelPicker activity
        finish();
    }

    private void lockLevels() {
        for (int i = totalLevels; i > currentLevel; i--) {
            int id = getResources().getIdentifier("b" + i, "id", getPackageName()); // get button ID
            ImageView button = (ImageView) findViewById(id); // get imageview object
            button.setColorFilter(Color.parseColor("#333333")); // tint dark
        }
    }

    public void click(View v) {
        Music.allowPlay();

        // Get button tag (1, 2, etc.)
        int tag = Integer.parseInt((String) v.getTag());

        // Check if not allowed
        if (tag > currentLevel) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Woah there!");
            alert.setMessage("Make sure you complete the previous levels first!");
            alert.setPositiveButton("OK", null);
            alert.show();
            return;
        }

        // Configure & Launch Game activity
        getLevelParams(tag);
        Intent intent = new Intent(LevelPicker.this, Game.class);
        intent.putExtra("nextLevel", currentLevel+1);
        intent.putExtra("maxDots", maxDots);
        intent.putExtra("gameTime", gameTime);
        startActivity(intent);
    }

    private void getLevelParams(int tag) {
        // Get raw txt file
        InputStream inputStream = getResources().openRawResource(R.raw.levels);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        int index = 0;
        int target = tag - 1;
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (index == target) {
                    String[] arr = line.split("/");
                    this.maxDots = Integer.parseInt(arr[0]);
                    this.gameTime = Integer.parseInt(arr[1]);
                }

                index++;
            }
        }
        catch (IOException e) {}
    }
}
