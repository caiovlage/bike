package com.vaidebike;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Handler handle = new Handler();
        handle.postDelayed(
                new Runnable()
                {
                    @Override public void run()
                    {
                        showMaps();
                    }
                },
                3000
        );
    }

    private void showMaps()
    {
        Intent intent = new Intent(this, MapsActivity.class); startActivity(intent);
    }
}