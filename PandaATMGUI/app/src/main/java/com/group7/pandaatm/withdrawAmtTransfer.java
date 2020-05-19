package com.group7.pandaatm;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.group7.pandaatm.data.Message;
import com.group7.pandaatm.data.SessionController;

import java.io.IOException;

public class withdrawAmtTransfer extends AppCompatActivity {

    private String accountNameSrc;
    private String accountNameTar;
    private double srcAccountMax;
    private double tarAccountMax;
    private double minSrc;

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

        accountNameSrc = pastIntent.getStringExtra("accountNameSrc");
        srcAccountMax = pastIntent.getDoubleExtra("amountSrc", -1);
        boolean isSrcChecking = pastIntent.getBooleanExtra("isCheckingSrc", false);

        //Gets minReqBalance required if source account was a checking account
        if (isSrcChecking) {
            minSrc = pastIntent.getDoubleExtra("minSrc", -1);
        }
        //Target Accounts to Deposit the transfer money into (from account select intent)
        accountNameTar = pastIntent.getStringExtra("accountNameTar");
        tarAccountMax = pastIntent.getDoubleExtra("amountTar", -1);

        //initialize click listeners
        findViewById(R.id.clearButton1).setOnClickListener(buttonClickListener);
        findViewById(R.id.enterButton1).setOnClickListener(buttonClickListener);
        findViewById(R.id.cancelButton2).setOnClickListener(buttonClickListener);
        EditText amountBox = findViewById(R.id.withdrawAmt);
        amountBox.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        System.out.println("IN WITHDRAW AMT TRANSFER");
        System.out.println("Source Account Name: " + accountNameSrc);
        System.out.println("Source Account Amount: " + srcAccountMax);
        System.out.println("Source Min Required Balance: " + minSrc);
        System.out.println("Target Account Name: " + accountNameTar);
        System.out.println("Target Account Amount: " + tarAccountMax);
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText withdrawAmt = findViewById(R.id.withdrawAmt);
            switch(v.getId()){
                case R.id.clearButton1:
                    withdrawAmt.getText().clear();
                    break;
                case R.id.enterButton1:
                    String amountText = withdrawAmt.getText().toString();
                    try {
                        double amount = Double.parseDouble(amountText);
                        if (amount < srcAccountMax) {
                            //Alerts user that the amount will in going below min required balance
                            if(srcAccountMax - amount < minSrc)
                            {
                                runOnUiThread(() -> {
                                    AlertDialog.Builder belowMinReqBal = new AlertDialog.Builder(withdrawAmtTransfer.this);
                                    belowMinReqBal.setMessage("Transaction will result in going below account's minimum required balance.");
                                    belowMinReqBal.setTitle("Warning: Required Minimum Balance...");
                                    belowMinReqBal.setPositiveButton("OK", null);
                                    belowMinReqBal.setCancelable(false);
                                    belowMinReqBal.create().show();
                                });
                            }
                            Thread worker24 = new Thread(() -> {
                                try {
                                    SessionController c = SessionController.getInstance();
                                    Message msgSendAmount = new Message(18);
                                    msgSendAmount.addDoubleM(amount);
                                    c.sendMessage(msgSendAmount);
                                    runOnUiThread(() -> {
                                        Intent confirmation = new Intent(withdrawAmtTransfer.this, ConfirmationScreen.class);
                                        confirmation.putExtra("srcAccountName", accountNameSrc);
                                        confirmation.putExtra("srcAccountMax", srcAccountMax);
                                        confirmation.putExtra("tarAccountName", accountNameTar);
                                        confirmation.putExtra("tarAccountMax", tarAccountMax);
                                        confirmation.putExtra("amount", amount);
                                        confirmation.putExtra("type", 2);
                                        startActivity(confirmation);
                                        finish();
                                    });
                                } catch(IOException e) {
                                    e.printStackTrace();
                                }
                            });
                            worker24.start();
                        }
                        else {//Not enough money in source account to complete transaction
                            runOnUiThread(() -> {
                                AlertDialog.Builder insufficientFunds = new AlertDialog.Builder(withdrawAmtTransfer.this);
                                insufficientFunds.setMessage("Insufficient Funds in Source Account.");
                                insufficientFunds.setTitle("Transaction failed...");
                                insufficientFunds.setPositiveButton("OK", null);
                                insufficientFunds.setCancelable(false);
                                insufficientFunds.create().show();
                            });
                        }
                    } catch(NumberFormatException e){
                        e.printStackTrace();
                    }
                    break;
                case R.id.cancelButton2://if previous button is clicked, go to Main Menu screen
                    Thread worker25 = new Thread(() -> {
                        try {
                            SessionController c = SessionController.getInstance();
                            Message msgCancelRequest = new Message(17);
                            c.sendMessage(msgCancelRequest);
                            runOnUiThread(() -> {
                                Intent exit = new Intent(withdrawAmtTransfer.this, MenuScreen.class);
                                startActivity(exit);
                                finish();
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    worker25.start();
                    break;
                default://Shouldn't get here
                    break;
            }//end switch
        }//end onClick
    };//end View.OnClickListener
    @Override
    public void onBackPressed() {}//Disables Android's Back Button
}//end withdrawAmtTransfer

