package com.harshchourasiya.movies.Splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.harshchourasiya.movies.MainActivity;
import com.harshchourasiya.movies.R;

import static com.harshchourasiya.movies.Data.Data.SPLASH_SCREEN_TIME_OUT;
import static com.harshchourasiya.movies.Data.Data.isDark;

public class Splash extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Getting if user select dark mode or not
        if (isDark(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        // making activity full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Setting Content
        setContentView(R.layout.splash_screen);
        // Setting Animation
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.movie_poster);
        animation.setStartOffset(500);
        ((LinearLayout) findViewById(R.id.parent)).setAnimation(animation);
        // Setting handler to open activity after some time
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                //Intent is used to switch from one activity to another.
                startActivity(i);
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }
}
