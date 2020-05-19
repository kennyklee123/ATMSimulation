package com.group7.pandaatm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.group7.pandaatm.data.Message;
import com.group7.pandaatm.data.SessionController;

import java.io.IOException;

public class withdrawCustomAmt extends AppCompatActivity {

    private String accountName;
    private double accountMax;
    private boolean isChecking;
    private double min;
    private int billAvailableCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_amt_transfer_screen);
        Thread worker30 = new Thread(() -> {
            try {
                SessionController.getInstance().setCurrentContext(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Intent pastIntent = getIntent();
        accountName = pastIntent.getStringExtra("accountName");
        accountMax = pastIntent.getDoubleExtra("accountMax", -1);
        isChecking = pastIntent.getBooleanExtra("isChecking", false);
        billAvailableCount = pastIntent.getIntExtra("billAvailableCount", -1);
        if(isChecking)
            min = pastIntent.getDoubleExtra("min", -1);
        //initialize click listeners
        findViewById(R.id.clearButton1).setOnClickListener(buttonClickListener);
        findViewById(R.id.enterButton1).setOnClickListener(buttonClickListener);
        findViewById(R.id.cancelButton2).setOnClickListener(buttonClickListener);

    }
    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText withdrawAmt = findViewById(R.id.withdrawAmt);
            switch (v.getId()) {
                case R.id.clearButton1:     //if clear button is clicked, clear text fields
                    withdrawAmt.getText().clear();
                    break;
                case R.id.enterButton1:
                    String amountText = withdrawAmt.getText().toString();
                    try{
                        double amount = Double.parseDouble(amountText);
                        if(amount < accountMax) {
                            if (amount % 20 == 0) {
                                if (amount / 20 < billAvailableCount) {
                                    //Alerts user that the amount will in going below min required balance
                                    if(accountMax - amount < min)
                                    {
                                        AlertDialog.Builder belowMinReqBal = new AlertDialog.Builder(withdrawCustomAmt.this);
                                        belowMinReqBal.setMessage("Transaction will result in going below account's minimum required balance.");
                                        belowMinReqBal.setTitle("Warning: Required Minimum Balance...");
                                        belowMinReqBal.setPositiveButton("OK", null);
                                        belowMinReqBal.setCancelable(false);
                                        belowMinReqBal.create().show();
                                    }
                                    Thread worker13 = new Thread(() -> {
                                        try {
                                            SessionController c = SessionController.getInstance();
                                            Message msgSendAmount = new Message(18);
                                            msgSendAmount.addDoubleM(amount);
                                            c.sendMessage(msgSendAmount);
                                            runOnUiThread(() -> {
                                                Intent confirmation = new Intent(withdrawCustomAmt.this, ConfirmationScreen.class);
                                                confirmation.putExtra("accountName", accountName);
                                                confirmation.putExtra("type", 1);
                                                confirmation.putExtra("srcOldBalance" , accountMax);
                                                confirmation.putExtra("amount", amount);
                                                startActivity(confirmation);
                                                finish();
                                            });
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    worker13.start();
                                } else {
                                    //TODO show error, request can not be supplied by ATM, do nothing
                                    AlertDialog.Builder atmEmpty = new AlertDialog.Builder(withdrawCustomAmt.this);
                                    atmEmpty.setMessage("ATM needs to be refilled.");
                                    atmEmpty.setTitle("Transaction failed...");
                                    atmEmpty.setPositiveButton("OK", null);
                                    atmEmpty.setCancelable(false);
                                    atmEmpty.create().show();
                                }
                            }
                            else {
                                //TODO show error, value is not divisible by twenty, and do nothing
                                AlertDialog.Builder notDivisible = new AlertDialog.Builder(withdrawCustomAmt.this);
                                notDivisible.setMessage("Amount Must be a multiple of twenty.");
                                notDivisible.setTitle("Transaction failed...");
                                notDivisible.setPositiveButton("OK", null);
                                notDivisible.setCancelable(false);
                                notDivisible.create().show();
                            }
                        }
                        else {
                            //TODO show error, Not enough in your bank account, and do nothing
                            AlertDialog.Builder notEnough = new AlertDialog.Builder(withdrawCustomAmt.this);
                            notEnough.setMessage("Insufficient Funds in Account.");
                            notEnough.setTitle("Transaction failed...");
                            notEnough.setPositiveButton("OK", null);
                            notEnough.setCancelable(false);
                            notEnough.create().show();
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.cancelButton2://if previous button is clicked, go to Main Menu screen
                    Thread worker14 = new Thread(() -> {
                        try {
                            SessionController c = SessionController.getInstance();
                            Message msgCancelRequest = new Message(17);
                            c.sendMessage(msgCancelRequest);
                            runOnUiThread(() -> {
                                Intent exit = new Intent(withdrawCustomAmt.this, MenuScreen.class);
                                startActivity(exit);
                                finish();
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    worker14.start();
                    break;
                default://Shouldn't get here
                    break;
            }
        }
    };
}
