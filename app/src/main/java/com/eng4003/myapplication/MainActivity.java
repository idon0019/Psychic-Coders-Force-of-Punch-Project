package com.eng4003.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static int FADE_DURATION_MS = 500;
    private static float IMAGE_ALPHA_MAX = 1.0f;
    private static float IMAGE_ALPHA_MIN = 0.2f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get Text View
        TextView tx = findViewById(R.id.textView4);

        ImageView img = findViewById(R.id.logo);
        //Set up tap functionality for the text.
        tx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignIn(v);
            }
        });

        Animation fadeIn = new AlphaAnimation(IMAGE_ALPHA_MIN,IMAGE_ALPHA_MAX);
        fadeIn.setDuration(FADE_DURATION_MS);

        Animation fadeOut = new AlphaAnimation(IMAGE_ALPHA_MAX,IMAGE_ALPHA_MIN);
        fadeOut.setDuration(FADE_DURATION_MS);

        Animation.AnimationListener listener;


        listener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //Log.d("My App", "Animation start!");
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                //Log.d("My App", "Animation done!");
                if (animation == fadeIn) {
                    img.startAnimation(fadeOut);
                } else {
                    img.startAnimation(fadeIn);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.d("My App", "Animation repeat!");
            }
        };
        fadeIn.setAnimationListener(listener);
        fadeOut.setAnimationListener(listener);
        //myAnimation.setRepeatMode(Animation.INFINITE);
        //myAnimation.setRepeatCount(10);
        img.startAnimation(fadeIn);

    }

    /*
     * With this button click, we can do so many actions with different methods of our own.
     */
    public void goToSignIn(View v) {

        Log.d("PunchApp", "button is clicked!");
        Intent i = new Intent(getApplicationContext(), AddNewUser.class);
        startActivity(i);
    }
}