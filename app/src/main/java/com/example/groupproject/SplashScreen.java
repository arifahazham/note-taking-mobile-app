package com.example.groupproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Start MainActivity after a delay
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        startActivity(new Intent(SplashScreen.this, SignupActivity.class));
                        finish();
                    }
                },
                2000); // 2 seconds delay
    }
}
