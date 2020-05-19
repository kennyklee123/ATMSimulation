package com.group7.pandaatm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.group7.pandaatm.data.Message;
import com.group7.pandaatm.data.SessionController;

import java.io.IOException;
import java.util.ArrayList;

public class MenuScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_screen);

        Thread worker8 = new Thread(() -> {
            try {
                SessionController c = SessionController.getInstance();
                c.setCurrentContext(this);
                runOnUiThread(() -> {
                    TextView welcomeText = findViewById(R.id.welcomeTxt);
                    TextView atmAddress = findViewById(R.id.buildingLocation2);
                    welcomeText.setText("Welcome, " + c.getCardName());
                    atmAddress.setText(c.getAddress());
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        worker8.start();
        //initialize click listeners
        findViewById(R.id.withdrawBtn).setOnClickListener(buttonClickListener);
        findViewById(R.id.depositBtn).setOnClickListener(buttonClickListener);
        findViewById(R.id.transferBtn).setOnClickListener(buttonClickListener);
        findViewById(R.id.balanceBtn).setOnClickListener(buttonClickListener);
        findViewById(R.id.logoutBtn).setOnClickListener(buttonClickListener);

    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.withdrawBtn:      //if withdraw button is clicked, go to withdraw screen
                    Thread worker5a = new Thread(() -> {
                        try {
                            SessionController c = SessionController.getInstance();
                            c.sendMessage(new Message(9));
                            Message msgAccountList = c.readMessage();
                            ArrayList<String> accountNames = msgAccountList.getTextMessages();
                            ArrayList<Integer> accountIDs = msgAccountList.getIntegerMessages();
                            runOnUiThread(() -> {
                                Intent withdraw = new Intent(MenuScreen.this, selectAccount.class);
                                withdraw.putExtra("accountNames", accountNames);
                                withdraw.putExtra("accountIds", accountIDs);
                                withdraw.putExtra("nextIntent", 2);
                                withdraw.putExtra("prompt", "Please Select an Account to Withdraw From");
                                startActivity(withdraw);
                                finish();
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    worker5a.start();
                    break;
                case R.id.transferBtn:      //if transaction button is clicked, go to transfer screen
                    Thread worker5b = new Thread(() -> {
                        try {
                            SessionController c = SessionController.getInstance();
                            c.sendMessage(new Message(11));
                            Message msgAccountList = c.readMessage();
                            ArrayList<String> accountNames = msgAccountList.getTextMessages();
                            ArrayList<Integer> accountIDs = msgAccountList.getIntegerMessages();
                            runOnUiThread(() -> {
                                Intent transfer = new Intent(MenuScreen.this, selectAccount.class);
                                transfer.putExtra("accountNames", accountNames);
                                transfer.putExtra("accountIds", accountIDs);
                                transfer.putExtra("nextIntent", 3);
                                transfer.putExtra("prompt", "Please select a Source Account");
                                startActivity(transfer);
                                finish();
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    worker5b.start();
                    break;
                case R.id.depositBtn:       //if deposit button is clicked, go to deposit screen
                    Thread worker5c = new Thread(() -> {
                        try {
                            SessionController c = SessionController.getInstance();
                            c.sendMessage(new Message(10));
                            Message msgAccountList = c.readMessage();
                            ArrayList<String> accountNames = msgAccountList.getTextMessages();
                            ArrayList<Integer> accountIDs = msgAccountList.getIntegerMessages();
                            runOnUiThread(() -> {
                                Intent deposit = new Intent(MenuScreen.this, selectAccount.class);
                                deposit.putExtra("accountNames", accountNames);
                                deposit.putExtra("accountIds", accountIDs);
                                deposit.putExtra("nextIntent", 1);
                                deposit.putExtra("prompt", "Please Select a Destination Account");
                                startActivity(deposit);
                                finish();
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    worker5c.start();
                    break;
                case R.id.balanceBtn:       //if balance button is clicked, go to balance button
                    Thread worker5d = new Thread(() -> {
                        try {
                            SessionController c = SessionController.getInstance();
                            c.sendMessage(new Message(14));
                            Message msgAccountList = c.readMessage();
                            ArrayList<String> accountNames = msgAccountList.getTextMessages();
                            ArrayList<Integer> accountIDs = msgAccountList.getIntegerMessages();
                            runOnUiThread(() -> {
                                Intent balance = new Intent(MenuScreen.this, selectAccount.class);
                                balance.putExtra("accountNames", accountNames);
                                balance.putExtra("accountIds", accountIDs);
                                balance.putExtra("nextIntent", 5);
                                balance.putExtra("prompt", "Please Select an Account to View");
                                startActivity(balance);
                                finish();
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    worker5d.start();
                    break;
                case R.id.logoutBtn:        //if cancel button is clicked, go to main screen
                    Thread worker4 = new Thread(() -> {
                        try {
                            SessionController c = SessionController.getInstance();
                            c.sendMessage(new Message(0));
                            if(c.getRecord().size() == 0) {
                                c.terminateSession();
                                runOnUiThread(() -> {
                                    Intent map = new Intent(MenuScreen.this, MainActivity.class);
                                    startActivity(map);
                                    finish();
                                });
                            }
                            else {
                                runOnUiThread(() -> {
                                    Intent map = new Intent(MenuScreen.this, receiptScreen.class);
                                    startActivity(map);
                                    finish();
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    worker4.start();
                    break;
                default:
                    break;

            }
        }
    };
    @Override
    public void onBackPressed() {}//Disables Android's Back Button
}


