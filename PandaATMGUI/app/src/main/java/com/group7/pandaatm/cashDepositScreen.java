package com.group7.pandaatm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.group7.pandaatm.data.Message;
import com.group7.pandaatm.data.SessionController;

import java.io.IOException;

public class cashDepositScreen extends AppCompatActivity {

    TextView oneValue;
    TextView fiveValue;
    TextView tenValue;
    TextView twentyValue;
    TextView fiftyValue;
    TextView hundredValue;

    //initialize counters to zero
    int oneCounter = 0;
    int fiveCounter = 0;
    int tenCounter = 0;
    int twentyCounter = 0;
    int fiftyCounter = 0;
    int hundredCounter = 0;

    int greyColor = 0x65111111;
    int redColor = 0xffb12525;
    int greenColor = 0xff33cc33;

    int maxBillCount;
    String accountName;
    double amount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_deposit_screen);

        Thread worker30 = new Thread(() -> {
            try {
                SessionController.getInstance().setCurrentContext(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Intent pastIntent = getIntent();
        maxBillCount = pastIntent.getIntExtra("maxBillCount", -1);
        maxBillCount = Math.min(maxBillCount, 50);
        accountName = pastIntent.getStringExtra("accountName");
        amount = pastIntent.getDoubleExtra("amount", -1);

        oneValue = findViewById(R.id.oneCounter);
        fiveValue = findViewById(R.id.fiveCounter);
        tenValue = findViewById(R.id.tenCounter);
        twentyValue = findViewById(R.id.twentyCounter);
        fiftyValue = findViewById(R.id.fiftyCounter);
        hundredValue = findViewById(R.id.hundredCounter);

        //Init subButtons to be disabled
        findViewById(R.id.subOne).setEnabled(false);
        findViewById(R.id.subFive).setEnabled(false);
        findViewById(R.id.subTen).setEnabled(false);
        findViewById(R.id.subTwenty).setEnabled(false);
        findViewById(R.id.subFifty).setEnabled(false);
        findViewById(R.id.subHundred).setEnabled(false);
        findViewById(R.id.subOne).setBackgroundColor(greyColor);
        findViewById(R.id.subFive).setBackgroundColor(greyColor);
        findViewById(R.id.subTen).setBackgroundColor(greyColor);
        findViewById(R.id.subTwenty).setBackgroundColor(greyColor);
        findViewById(R.id.subFifty).setBackgroundColor(greyColor);
        findViewById(R.id.subHundred).setBackgroundColor(greyColor);

        //initialize click listeners
        findViewById(R.id.enterButton).setOnClickListener(buttonClickListener);
        findViewById(R.id.cancelButton).setOnClickListener(buttonClickListener);

        findViewById(R.id.addOne).setOnClickListener(buttonClickListener);
        findViewById(R.id.subOne).setOnClickListener(buttonClickListener);
        findViewById(R.id.addFive).setOnClickListener(buttonClickListener);
        findViewById(R.id.subFive).setOnClickListener(buttonClickListener);
        findViewById(R.id.addTen).setOnClickListener(buttonClickListener);
        findViewById(R.id.subTen).setOnClickListener(buttonClickListener);
        findViewById(R.id.addTen).setOnClickListener(buttonClickListener);
        findViewById(R.id.addTwenty).setOnClickListener(buttonClickListener);
        findViewById(R.id.subTwenty).setOnClickListener(buttonClickListener);
        findViewById(R.id.addFifty).setOnClickListener(buttonClickListener);
        findViewById(R.id.subFifty).setOnClickListener(buttonClickListener);
        findViewById(R.id.addHundred).setOnClickListener(buttonClickListener);
        findViewById(R.id.subHundred).setOnClickListener(buttonClickListener);
    }


    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View v) {
            int totalBills = oneCounter + fiveCounter + tenCounter + twentyCounter + fiftyCounter + hundredCounter;
            switch (v.getId()) {
                case R.id.addOne:
                    if (totalBills < maxBillCount) {                        //total bill limit TBD
                        oneCounter++;
                        oneValue.setText("(" + oneCounter + ")");
                        if(totalBills + 1 == maxBillCount)
                            updateAdders(false);
                        if(oneCounter == 1) {
                            findViewById(R.id.subOne).setEnabled(true);
                            findViewById(R.id.subOne).setBackgroundColor(redColor);
                        }
                    }
                    break;
                case R.id.subOne:
                    if (oneCounter > 0) {                           //cannot have negative amount of bills
                        oneCounter--;
                        oneValue.setText("("+ oneCounter +")");
                        if(totalBills == maxBillCount)
                            updateAdders(true);
                        if(oneCounter == 0) {
                            findViewById(R.id.subOne).setEnabled(false);
                            findViewById(R.id.subOne).setBackgroundColor(greyColor);
                        }
                    }
                    break;
                case R.id.addFive:
                    if (totalBills < maxBillCount) {
                        fiveCounter++;
                        fiveValue.setText("(" + fiveCounter + ")");
                        if(totalBills + 1 == maxBillCount)
                            updateAdders(false);
                        if(fiveCounter == 1) {
                            findViewById(R.id.subFive).setEnabled(true);
                            findViewById(R.id.subFive).setBackgroundColor(redColor);
                        }
                    }
                    break;
                case R.id.subFive:
                    if (fiveCounter > 0) {
                        fiveCounter--;
                        fiveValue.setText("("+ fiveCounter +")");
                        if(totalBills == maxBillCount)
                            updateAdders(true);
                        if(fiveCounter == 0) {
                            findViewById(R.id.subFive).setEnabled(false);
                            findViewById(R.id.subFive).setBackgroundColor(greyColor);
                        }
                    }
                    break;
                case R.id.addTen:
                    if (totalBills < maxBillCount) {
                        tenCounter++;
                        tenValue.setText("(" + tenCounter + ")");
                        if(totalBills + 1 == maxBillCount)
                            updateAdders(false);
                        if(tenCounter == 1) {
                            findViewById(R.id.subTen).setEnabled(true);
                            findViewById(R.id.subTen).setBackgroundColor(redColor);
                        }
                    }
                    break;
                case R.id.subTen:
                    if (tenCounter > 0) {
                        tenCounter--;
                        tenValue.setText("("+ tenCounter +")");
                        if(totalBills == maxBillCount)
                            updateAdders(true);
                        if(tenCounter == 0) {
                            findViewById(R.id.subTen).setEnabled(false);
                            findViewById(R.id.subTen).setBackgroundColor(greyColor);
                        }
                    }
                    break;
                case R.id.addTwenty:
                    if (totalBills < maxBillCount) {
                        twentyCounter++;
                        twentyValue.setText("(" + twentyCounter + ")");
                        if(totalBills + 1 == maxBillCount)
                            updateAdders(false);
                        if(twentyCounter == 1) {
                            findViewById(R.id.subTwenty).setEnabled(true);
                            findViewById(R.id.subTwenty).setBackgroundColor(redColor);
                        }
                    }
                    break;
                case R.id.subTwenty:
                    if (twentyCounter> 0) {
                        twentyCounter--;
                        twentyValue.setText("("+ twentyCounter +")");
                        if(totalBills == maxBillCount)
                            updateAdders(true);
                        if(twentyCounter == 0) {
                            findViewById(R.id.subTwenty).setEnabled(false);
                            findViewById(R.id.subTwenty).setBackgroundColor(greyColor);
                        }
                        break;
                    }
                case R.id.addFifty:
                    if (totalBills < maxBillCount) {
                        fiftyCounter++;
                        fiftyValue.setText("(" + fiftyCounter + ")");
                        if(totalBills + 1 == maxBillCount)
                            updateAdders(false);
                        if(fiftyCounter == 1) {
                            findViewById(R.id.subFifty).setEnabled(true);
                            findViewById(R.id.subFifty).setBackgroundColor(redColor);
                        }
                    }
                    break;
                case R.id.subFifty:
                    if (fiftyCounter > 0) {
                        fiftyCounter--;
                        fiftyValue.setText("("+ fiftyCounter +")");
                        if(totalBills == maxBillCount)
                            updateAdders(true);
                        if(fiftyCounter == 0) {
                            findViewById(R.id.subFifty).setEnabled(false);
                            findViewById(R.id.subFifty).setBackgroundColor(greyColor);
                        }
                    }
                    break;
                case R.id.addHundred:
                    if (totalBills < maxBillCount) {
                        hundredCounter++;
                        hundredValue.setText("(" + hundredCounter + ")");
                        if(totalBills + 1 == maxBillCount)
                            updateAdders(false);
                        if(hundredCounter == 1) {
                            findViewById(R.id.subHundred).setEnabled(true);
                            findViewById(R.id.subHundred).setBackgroundColor(redColor);
                        }
                    }
                    break;
                case R.id.subHundred:
                    if (hundredCounter > 0) {
                        hundredCounter--;
                        hundredValue.setText("("+ hundredCounter +")");
                        if(totalBills == maxBillCount)
                            updateAdders(true);
                        if(hundredCounter == 0) {
                            findViewById(R.id.subHundred).setEnabled(false);
                            findViewById(R.id.subHundred).setBackgroundColor(greyColor);
                        }
                    }
                    break;
                case R.id.enterButton:
                    Thread worker9 = new Thread(() -> {
                        try {
                            SessionController c = SessionController.getInstance();
                            Message msgBillDeposit = new Message(18);
                            msgBillDeposit.addIntegerM(oneCounter);
                            msgBillDeposit.addIntegerM(fiveCounter);
                            msgBillDeposit.addIntegerM(tenCounter);
                            msgBillDeposit.addIntegerM(twentyCounter);
                            msgBillDeposit.addIntegerM(fiftyCounter);
                            msgBillDeposit.addIntegerM(hundredCounter);
                            c.sendMessage(msgBillDeposit);
                            double totalAmount = oneCounter + fiveCounter * 5 + tenCounter * 10 + twentyCounter * 20 + fiftyCounter * 50 + hundredCounter * 100;
                            runOnUiThread(() -> {
                                Intent confirmTransaction = new Intent(cashDepositScreen.this, ConfirmationScreen.class);
                                confirmTransaction.putExtra("accountName", accountName);
                                confirmTransaction.putExtra("billCount", totalBills);
                                confirmTransaction.putExtra("amount", totalAmount);
                                confirmTransaction.putExtra("accountAmount" , amount);
                                confirmTransaction.putExtra("type", 0);
                                startActivity(confirmTransaction);
                                finish();
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    worker9.start();
                    break;
                case R.id.cancelButton:   //if previous button is clicked, go to deposit choice screen
                    Thread worker10 = new Thread(() -> {
                        try {
                            SessionController c = SessionController.getInstance();
                            Message msgCancelDeposit = new Message(17);
                            c.sendMessage(msgCancelDeposit);
                            runOnUiThread(() -> {
                                Intent cancel = new Intent(cashDepositScreen.this, MenuScreen.class);
                                startActivity(cancel);
                                finish();
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    worker10.start();
                    break;
                default:
                    break;
            }
        }
    };

    public void updateAdders(boolean value) {
        System.out.println("Updating Deposit Buttons: " + value);
        findViewById(R.id.addOne).setEnabled(value);
        findViewById(R.id.addFive).setEnabled(value);
        findViewById(R.id.addTen).setEnabled(value);
        findViewById(R.id.addTwenty).setEnabled(value);
        findViewById(R.id.addFifty).setEnabled(value);
        findViewById(R.id.addHundred).setEnabled(value);
        int color = value ? greenColor : greyColor;
        findViewById(R.id.addOne).setBackgroundColor(color);
        findViewById(R.id.addFive).setBackgroundColor(color);
        findViewById(R.id.addTen).setBackgroundColor(color);
        findViewById(R.id.addTwenty).setBackgroundColor(color);
        findViewById(R.id.addFifty).setBackgroundColor(color);
        findViewById(R.id.addHundred).setBackgroundColor(color);
    }
    @Override
    public void onBackPressed() {}//Disables Android's Back Button
}