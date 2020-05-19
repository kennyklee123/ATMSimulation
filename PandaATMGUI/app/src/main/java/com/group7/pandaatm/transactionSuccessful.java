package com.group7.pandaatm;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class transactionSuccessful extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_successful_screen);
        Thread worker23 = new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                Intent dep = new Intent(transactionSuccessful.this, MenuScreen.class);
                startActivity(dep);
                finish();
            });
        });
        worker23.start();
    }
    @Override
    public void onBackPressed() {}//Disables Android's Back Button
}
