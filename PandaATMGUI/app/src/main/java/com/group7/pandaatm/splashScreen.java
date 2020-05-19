package com.group7.pandaatm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class splashScreen extends AppCompatActivity {

    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //activates splash screen for 2.5 seconds, then goes to main class
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent splash = new Intent(splashScreen.this, MainActivity.class);
                startActivity(splash);
                finish();
            }
        },2500);



    }
    @Override
    public void onBackPressed() {}//Disables Android's Back Button
}



