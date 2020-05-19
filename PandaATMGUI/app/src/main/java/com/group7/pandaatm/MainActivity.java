package com.group7.pandaatm;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.group7.pandaatm.data.Message;
import com.group7.pandaatm.data.SessionController;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private SessionController c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        if(getIntent().getBooleanExtra("timeout", false)) {

            AlertDialog.Builder timeoutAlert = new AlertDialog.Builder(this);
            timeoutAlert.setMessage("Session has been terminated due extended period of inactivity");
            timeoutAlert.setTitle("Session Timeout...");
            timeoutAlert.setPositiveButton("OK", null);
            timeoutAlert.setCancelable(false);
            timeoutAlert.create().show();
        }
        //initialize click listeners
        findViewById(R.id.bldg1ATM).setOnClickListener(buttonClickListener);
        findViewById(R.id.bldg17ATM).setOnClickListener(buttonClickListener);
        findViewById(R.id.bscATM).setOnClickListener(buttonClickListener);
        findViewById(R.id.libATM).setOnClickListener(buttonClickListener);
        findViewById(R.id.mktplaceATM).setOnClickListener(buttonClickListener);
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int atmID = 0;
            switch (v.getId()) {
                case R.id.bldg1ATM: //if ATM on map is clicked, goes to login screen
                    atmID = 1;
                    break;
                case R.id.bldg17ATM:
                    atmID = 2;
                    break;
                case R.id.mktplaceATM:
                    atmID = 3;
                    break;
                case R.id.bscATM:
                    atmID = 4;
                    break;
                case R.id.libATM:
                    atmID = 5;
                    break;
            }
            if (atmID != 0) {
                int finalAtmID = atmID;
                Thread worker1 = new Thread(() -> {
                    try {
                        c = SessionController.getInstance();
                        Message msgRequestATM = new Message(6);
                        msgRequestATM.addIntegerM(finalAtmID);
                        c.sendMessage(msgRequestATM);
                        System.out.println("Sent ATM Request Message");
                        Message msgATMRequestResponse = c.readMessage();
                        System.out.println("ATM Request Response Message Flag: " + msgATMRequestResponse.flag());
                        if (msgATMRequestResponse.flag() == 8) {
                            runOnUiThread(() -> {
                                AlertDialog.Builder beingUsedAlert = new AlertDialog.Builder(MainActivity.this);
                                beingUsedAlert.setMessage("ATM is currently in use.");
                                beingUsedAlert.setTitle("Bad Login...");
                                beingUsedAlert.setPositiveButton("OK", null);
                                beingUsedAlert.setCancelable(false);
                                beingUsedAlert.create().show();
                            });
                            c.terminateSession();
                        } else if (msgATMRequestResponse.flag() == 22) {
                            //Access Granted
                            c.setAddress(msgATMRequestResponse.getTextMessages().get(0));
                            runOnUiThread(() -> {
                                Intent login = new Intent(MainActivity.this, loginScreen.class);
                                login.putExtra("ATMAddress", msgATMRequestResponse.getTextMessages().get(0));
                                startActivity(login);
                                finish();
                            });
                        } else if (msgATMRequestResponse.flag() == 7) {
                            //TODO maybe display error before crashing program
                            c.terminateSession();
                            System.exit(1);//Communication error, quit program
                        } else {
                            c.terminateSession();
                            //TODO bad communication, display crashing program
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            AlertDialog.Builder beingUsedAlert = new AlertDialog.Builder(MainActivity.this);
                            beingUsedAlert.setMessage("Could not connect to ATM Database");
                            beingUsedAlert.setTitle("Server Offline");
                            beingUsedAlert.setPositiveButton("OK", null);
                            beingUsedAlert.setCancelable(false);
                            beingUsedAlert.create().show();
                        });
                    }
                });
                worker1.start();
            }
        }
    };

    @Override
    public void onBackPressed() {}//Disables Android's Back Button
}
