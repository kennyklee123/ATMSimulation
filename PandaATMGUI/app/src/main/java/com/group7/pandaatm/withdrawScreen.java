package com.group7.pandaatm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.group7.pandaatm.data.Message;
import com.group7.pandaatm.data.SessionController;

import java.io.IOException;

public class withdrawScreen extends AppCompatActivity {

    String accountName;
    double accountMax;
    boolean isChecking;
    double min;
    int billAvailableCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_screen);
        Thread worker30 = new Thread(() -> {
            try {
                SessionController.getInstance().setCurrentContext(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Intent pastIntent = getIntent();
        accountName = pastIntent.getStringExtra("accountName");
        accountMax = pastIntent.getDoubleExtra("amount", -1);
        isChecking = pastIntent.getBooleanExtra("isChecking", false);
        billAvailableCount = pastIntent.getIntExtra("billAvailableCount", -1);
        if(isChecking)
            min = pastIntent.getDoubleExtra("min", -1);
        //initialize click listeners
        findViewById(R.id.cancelButton1).setOnClickListener(buttonClickListener);
        findViewById(R.id.diffAmt).setOnClickListener(buttonClickListener);
        findViewById(R.id.twentybutton).setOnClickListener(buttonClickListener);
        findViewById(R.id.fortybutton).setOnClickListener(buttonClickListener);
        findViewById(R.id.sixtybutton).setOnClickListener(buttonClickListener);
        findViewById(R.id.eightybutton).setOnClickListener(buttonClickListener);
        findViewById(R.id.onehundredbutton).setOnClickListener(buttonClickListener);
        findViewById(R.id.twohundredbutton).setOnClickListener(buttonClickListener);
    }

    private View.OnClickListener buttonClickListener = v -> {
        double amount = -1;
        switch (v.getId()) {
            case R.id.cancelButton1:        //if cancel button is clicked, go to main screen
                Thread worker11 = new Thread(() -> {
                    try {
                        SessionController c = SessionController.getInstance();
                        Message msgCancelWithdrawal = new Message(17);
                        c.sendMessage(msgCancelWithdrawal);
                        runOnUiThread(() -> {
                            Intent cancel = new Intent(withdrawScreen.this, MenuScreen.class);
                            startActivity(cancel);
                            finish();
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                worker11.start();
                break;
            case R.id.diffAmt:              //if different amount is clicked, go to withdraw diffAmt class
                Intent diffAmt = new Intent(withdrawScreen.this, withdrawCustomAmt.class);
                diffAmt.putExtra("accountName", accountName);
                diffAmt.putExtra("accountMax", accountMax);
                diffAmt.putExtra("isChecking", isChecking);
                if(isChecking) {
                    diffAmt.putExtra("min", min);
                }
                diffAmt.putExtra("billAvailableCount", billAvailableCount);
                startActivity(diffAmt);
                break;
            case R.id.twentybutton:
                amount = 20;
                break;
            case R.id.fortybutton:
                amount = 40;
                break;
            case R.id.sixtybutton:
                amount = 60;
                break;
            case R.id.eightybutton:
                amount = 80;
                break;
            case R.id.onehundredbutton:
                amount = 100;
                break;
            case R.id.twohundredbutton:
                amount = 200;
                break;
            default:
                throw new IllegalArgumentException("Unknown Button Pressed");
        }
        if(amount != -1) {
            if(amount < accountMax) {
                if(( amount / 20) < billAvailableCount) {
                    if(isChecking && (accountMax - amount) < min) {
                        //TODO Let user know they would withdraw past agreed min,
                        AlertDialog.Builder minBal = new AlertDialog.Builder(withdrawScreen.this);
                        minBal.setMessage("Going below minimum balance.");
                        minBal.setPositiveButton("OK", null);
                        minBal.setCancelable(false);
                        minBal.create().show();
                        // return true/false yes/no if they wish to continue or edit their amount
                        if(true) {
                            double finalAmount = amount;
                            Thread worker12 = new Thread(() -> {
                                try {
                                    SessionController c = SessionController.getInstance();
                                    Message msgSendAmount = new Message(18);
                                    msgSendAmount.addDoubleM(finalAmount);
                                    c.sendMessage(msgSendAmount);
                                    runOnUiThread(() -> {
                                        Intent confirmation = new Intent(withdrawScreen.this, ConfirmationScreen.class);
                                        confirmation.putExtra("accountName", accountName);
                                        confirmation.putExtra("type", 1);
                                        confirmation.putExtra("srcOldBalance" , accountMax);
                                        confirmation.putExtra("amount", finalAmount);
                                        startActivity(confirmation);
                                        finish();
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                            worker12.start();
                        }
                    }
                    else {
                        double finalAmount = amount;
                        Thread worker12 = new Thread(() -> {
                            try {
                                SessionController c = SessionController.getInstance();
                                Message msgSendAmount = new Message(18);
                                msgSendAmount.addDoubleM(finalAmount);
                                c.sendMessage(msgSendAmount);
                                runOnUiThread(() -> {
                                    Intent confirmation = new Intent(withdrawScreen.this, ConfirmationScreen.class);
                                    confirmation.putExtra("accountName", accountName);
                                    confirmation.putExtra("type", 1);
                                    confirmation.putExtra("srcOldBalance" , accountMax);
                                    confirmation.putExtra("amount", finalAmount);
                                    startActivity(confirmation);
                                    finish();
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        worker12.start();
                    }
                }
                else {
                    //TODO Alert user ATM can not fulfill request
                    AlertDialog.Builder cannotFulfill = new AlertDialog.Builder(withdrawScreen.this);
                    cannotFulfill.setMessage("ATM was unable to fulfill transaction request.");
                    cannotFulfill.setTitle("Transaction failed...");
                    cannotFulfill.setPositiveButton("OK", null);
                    cannotFulfill.setCancelable(false);
                    cannotFulfill.create().show();
                }
            }
            else {
                //TODO Alert user they do not have enough funds to withdraw that amount
                AlertDialog.Builder notEnough = new AlertDialog.Builder(withdrawScreen.this);
                notEnough.setMessage("Not enough in your bank account.");
                notEnough.setTitle("Transaction failed...");
                notEnough.setPositiveButton("OK", null);
                notEnough.setCancelable(false);
                notEnough.create().show();
            }
        }
    };
    @Override
    public void onBackPressed() {}//Disables Android's Back Button
}
