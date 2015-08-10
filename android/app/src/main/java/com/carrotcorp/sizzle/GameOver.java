package com.carrotcorp.sizzle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Sizzle Game
 * by Thomas Suarez, Chief Engineer @ CarrotCorp.
 */
public class GameOver extends Activity {
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        // Set score on screen
        this.score = getIntent().getIntExtra("score", 0);
        TextView scoreView = (TextView) findViewById(R.id.scoreView);
        scoreView.setText(score + "");
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Stop playing music
        Music.stop();

        // Close GameOver activity
        finish();
    }

    public void play(View v) {
        Music.allowPlay();

        // Launch level picker
        Intent intent = new Intent(GameOver.this, LevelPicker.class);
        startActivity(intent);
        finish();
    }

    public void share(View v) {
        int size = 1000;

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(size, size, conf); // this creates a mutable bitmap
        Canvas canvas = new Canvas(bmp);
        Drawable d = getResources().getDrawable(R.drawable.share);
        d.setBounds(0,0,size,size);
        d.draw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);

        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(350);

        String text = ("" + score);

        // Center vertically
        int yPos = size/2 - 10;
        Rect r = new Rect();
        paint.getTextBounds(text, 0, text.length(), r);
        yPos += (Math.abs(r.height()))/2;

        canvas.drawText(text, size/2, yPos, paint);

        // Send to Instagram app
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
        if (intent != null)
        {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage("com.instagram.android");

            saveBitmap(bmp);
            try {
                String msg = "I just scored " + score + " on Sizzle! Get Sizzle on iOS or Android!";
                String imagePath = Uri.parse(Environment.getExternalStorageDirectory() + File.separator + "sizzle.jpg").toString();
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), imagePath, "Sizzle", msg)));
                shareIntent.putExtra(Intent.EXTRA_TITLE, msg);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            shareIntent.setType("image/jpeg");

            startActivity(shareIntent);
            finish();
        }
        else
        {
            // bring user to the market to download the app.
            // or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id=" + "com.instagram.android"));
            startActivity(intent);
        }
    }

    private void saveBitmap(Bitmap bmp) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "sizzle.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
