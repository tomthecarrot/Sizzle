package com.carrotcorp.sizzle;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;

/**
 * Sizzle Game
 * by Thomas Suarez, Chief Engineer @ CarrotCorp.
 */
public class Helper {
    public static int getDrawableId(final Activity context, final String drawableName) {
        final int resId = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
        return resId;
    }

    /**
     * @see "http://blog.peterkuterna.net/2011/09/simple-crossfade-on-imageview.html"
     * with modifications by Thomas Suarez.
     */
    public static void setImageDrawableWithFade(final Activity context, final ImageView imageView, final String drawableName, final int durationMillis) {
        int resId = getDrawableId(context, drawableName); // get new drawable resource
        Drawable drawable;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            drawable = context.getResources().getDrawable(resId, context.getTheme());
        } else {
            drawable = context.getResources().getDrawable(resId); // this is deprecated starting in Android 5.0 Lollipop
        }

        final Drawable currentDrawable = imageView.getDrawable();
        final Drawable newDrawable = drawable;

        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (currentDrawable != null) {
                    Drawable [] arrayDrawable = new Drawable[2];
                    arrayDrawable[0] = currentDrawable;
                    arrayDrawable[1] = newDrawable;
                    TransitionDrawable transitionDrawable = new TransitionDrawable(arrayDrawable);
                    transitionDrawable.setCrossFadeEnabled(true);
                    imageView.setImageDrawable(transitionDrawable);
                    transitionDrawable.startTransition(durationMillis);
                } else {
                    imageView.setImageDrawable(newDrawable);
                }
            }
        };
        context.runOnUiThread(r);
    }
}
