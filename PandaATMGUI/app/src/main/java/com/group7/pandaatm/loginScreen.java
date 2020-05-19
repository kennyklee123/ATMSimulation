package com.group7.pandaatm;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.group7.pandaatm.data.Message;
import com.group7.pandaatm.data.SessionController;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class loginScreen extends AppCompatActivity {
    private EditText cardNumber;
    private EditText pinNumber;
    private Button buttonConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atm_login);
        Thread worker30 = new Thread(() -> {
            try {
                SessionController.getInstance().setCurrentContext(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //initialize click listeners
        findViewById(R.id.loginButton).setOnClickListener(buttonClickListener);
        findViewById(R.id.clearButton).setOnClickListener(buttonClickListener);
        findViewById(R.id.backToMapButton).setOnClickListener(buttonClickListener);
        TextView addressText  = findViewById(R.id.buildingLocation);
        Intent intent = getIntent();
        addressText.setText(intent.getStringExtra("ATMAddress"));
        cardNumber = findViewById(R.id.cardNumberText);
        pinNumber = findViewById(R.id.pinText);
        buttonConfirm = findViewById(R.id.loginButton);

        cardNumber.addTextChangedListener(loginWatcher);
        pinNumber.addTextChangedListener(loginWatcher);

    }
    private TextWatcher loginWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String cardNumberr = cardNumber.getText().toString().trim();
            String pinNumberr = pinNumber.getText().toString().trim();

            buttonConfirm.setEnabled(!cardNumberr.isEmpty() && !pinNumberr.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private final View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText cardNum = findViewById(R.id.cardNumberText);
            EditText pinNum = findViewById(R.id.pinText);
            switch (v.getId()) {
                case R.id.loginButton:      //if login button is clicked, go to transaction screen
                    System.out.println("Preparing Card Details");
                    String cardNumber = cardNum.getText().toString();
                    String pin = pinNum.getText().toString();
                    long cardNumberLong = 0L;
                    int pinNumberInt = 0;
                    try {
                        cardNumberLong = Long.parseLong(cardNumber);
                        pinNumberInt = Integer.parseInt(pin);
                    } catch (NumberFormatException e) {
                        AlertDialog.Builder stringAlert = new AlertDialog.Builder(loginScreen.this);
                        stringAlert.setMessage("Try again with proper input.");
                        stringAlert.setTitle("Bad Login...");
                        stringAlert.setPositiveButton("OK", null);
                        stringAlert.setCancelable(false);
                        stringAlert.create().show();
                    }
                    System.out.println("Card Number: " + cardNumberLong);
                    System.out.println("Pin: " + pinNumberInt);
                    Message msgLoginDetails = new Message(2);
                    msgLoginDetails.setCardNumber(cardNumberLong);
                    msgLoginDetails.addIntegerM(pinNumberInt);
                    long finalCardNumberLong = cardNumberLong;
                    Thread worker2 = new Thread(() -> {
                        try {
                            SessionController c = SessionController.getInstance();
                            c.sendMessage(msgLoginDetails);
                            Message msgLoginVerification = c.readMessage();
                            System.out.println("ATM Session Response Flag: " + msgLoginVerification.flag());
                            if (msgLoginVerification.flag() == 3) {//Card Number not found
                                //TODO Show alert card number not found, show alert, allow new card
                                runOnUiThread(() -> {
                                    AlertDialog.Builder errorAlert = new AlertDialog.Builder(loginScreen.this);
                                    errorAlert.setMessage("Wrong card number.");
                                    errorAlert.setTitle("Bad Login...");
                                    errorAlert.setPositiveButton("OK", null);
                                    errorAlert.setCancelable(false);
                                    errorAlert.create().show();
                                });
                            } else if (msgLoginVerification.flag() == 4) {
                                //TODO Show Card is locked, show alert, kick user out back to MainActivity
                                c.terminateSession();
                                runOnUiThread(() -> {
                                    AlertDialog.Builder lockedAlert = new AlertDialog.Builder(loginScreen.this);
                                    lockedAlert.setMessage("Card is locked. Try again later.");
                                    lockedAlert.setTitle("Bad Login...");
                                    lockedAlert.setPositiveButton("OK", null);
                                    lockedAlert.setCancelable(false);
                                    lockedAlert.create().show();
                                    Intent map = new Intent(loginScreen.this, MainActivity.class);
                                    startActivity(map);
                                    finish();
                                });
                            } else if (msgLoginVerification.flag() == 5) {
                                //Successful, go to transactionScreen
                                c.setCardName(msgLoginVerification.getTextMessages().get(1));
                                c.setCardNumber(String.valueOf(finalCardNumberLong));
                                runOnUiThread(() -> {
                                    Intent mainMenu = new Intent(loginScreen.this, MenuScreen.class);
                                    startActivity(mainMenu);
                                    finish();
                                });
                            } else if (msgLoginVerification.flag() == 23) {
                                //Card is Expired, show Alert, allow new card
                                runOnUiThread(() -> {
                                    AlertDialog.Builder expiredAlert = new AlertDialog.Builder(loginScreen.this);
                                    expiredAlert.setMessage("Card is expired.");
                                    expiredAlert.setTitle("Bad Login...");
                                    expiredAlert.setPositiveButton("OK", null);
                                    expiredAlert.setCancelable(false);
                                    expiredAlert.create().show();
                                });
                            } else if (msgLoginVerification.flag() == 24) {
                                //Pin Number invalid, show Alert, only allow changing pin, or cancel
                                runOnUiThread(() -> {
                                    AlertDialog.Builder pinAlert = new AlertDialog.Builder(loginScreen.this);
                                    pinAlert.setMessage("PIN Number invalid");
                                    pinAlert.setTitle("Bad Login...");
                                    pinAlert.setPositiveButton("OK", null);
                                    pinAlert.setNegativeButton("Cancel", null);
                                    pinAlert.setCancelable(false);
                                    pinAlert.create().show();
                                });
                            }
                        } catch (IOException e) {
                            //Should not happen
                        }
                    });
                    worker2.start();
                    break;
                case R.id.clearButton:      //if clean button is clicked, clear text fields
                    cardNum.getText().clear();
                    pinNum.getText().clear();
                    break;
                case R.id.backToMapButton:
                    System.out.println("Cancelling ATM Request");
                    Message msgCancelATMRequest = new Message(0);
                    Thread worker3 = new Thread(() -> {
                        SessionController c = null;
                        try {
                            c = SessionController.getInstance();
                            c.sendMessage(msgCancelATMRequest);
                            c.terminateSession();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(() -> {
                            Intent mainActivity = new Intent(loginScreen.this, MainActivity.class);
                            startActivity(mainActivity);
                            finish();
                        });
                    });
                    worker3.start();
                    break;
                default:
                    break;
            }

        }

    };
    @Override
    public void onBackPressed() {}
}
