package com.datechnologies.androidtest.animation;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import com.datechnologies.androidtest.MainActivity;
import com.datechnologies.androidtest.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Screen that displays the D & A Technologies logo.
 * The icon can be moved around on the screen as well as animated.
 * */

public class AnimationActivity extends AppCompatActivity {

    //==============================================================================================
    // Class Properties
    //==============================================================================================
    private ImageView  image;

    private int xDelta;
    private int yDelta;
    private MediaPlayer music;

    //==============================================================================================
    // Static Class Methods
    //==============================================================================================

    public static void start(Context context)
    {
        Intent starter = new Intent(context, AnimationActivity.class);
        context.startActivity(starter);
    }

    //==============================================================================================
    // Lifecycle Methods
    //==============================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        image = (ImageView) findViewById(R.id.daLogo);
        image.setOnTouchListener(onTouchListener());

    }

    @Override
    public void onResume() {
        super.onResume();
        music = MediaPlayer.create(this, R.raw.friend);
        music.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        music.stop();
        music.release();
        music = null;

    }

    @Override
    public void onPause() {
        super.onPause();
        music.pause();

    }

    //==============================================================================================
    // Process User Logo Interaction
    //==============================================================================================

    private OnTouchListener onTouchListener() {
        return new OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
                                view.getLayoutParams();

                        xDelta = x - lParams.leftMargin;
                        yDelta = y - lParams.topMargin;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                                .getLayoutParams();

                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        double z = image.getWidth();
                        layoutParams.rightMargin = 0;
                        if(x-xDelta < displayMetrics.widthPixels - 850 && x-xDelta > (displayMetrics.widthPixels*-1)+840)
                            layoutParams.leftMargin = x - xDelta;
                        if(y-yDelta > 0 && y-yDelta < displayMetrics.heightPixels - 270)
                            layoutParams.topMargin = y - yDelta;
                        layoutParams.bottomMargin = 0;
                        view.setLayoutParams(layoutParams);

                        break;
                }
                return true;
            }
        };
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    //==============================================================================================
    // Fade Animation
    //==============================================================================================

    public void onClickFadeButton(final View view) {
        final ImageView iv = (ImageView) findViewById(R.id.daLogo);


        AlphaAnimation fadeout = new AlphaAnimation(1.0f, 0f);
        fadeout.setDuration(1500);
        fadeout.setFillAfter(true);
        iv.startAnimation(fadeout);

        fadeout.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationStart(Animation animation){}

            @Override
            public void onAnimationRepeat(Animation animation){}

            @Override
            public void onAnimationEnd(Animation animation){
                AlphaAnimation fadein = new AlphaAnimation(0f, 1.0f);
                fadein.setDuration(1500);
                fadein.setFillAfter(true);
                iv.startAnimation(fadein);
            }
        });
        iv.startAnimation(fadeout);

    }
}
