package com.group7.pandaatm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.group7.pandaatm.data.Message;
import com.group7.pandaatm.data.SessionController;
import com.group7.pandaatm.data.TransactionRecord;

import java.io.IOException;

public class ConfirmationScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_screen);
        Thread worker30 = new Thread(() -> {
            try {
                SessionController.getInstance().setCurrentContext(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.confirmNo).setOnClickListener(buttonClickListener);
        findViewById(R.id.confirmYes).setOnClickListener(buttonClickListener);;
    }

    /*
    Sending Message IDs
    19 = confirmTransaction
    17 = transactionCancelled
    ------------------------
    20 - Insufficient Balance
    12 - Transaction Successful
    21 - Non enough bills available in ATM
     */

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.confirmNo:
                    Thread worker20 = new Thread(() -> {
                        try {
                            SessionController c = SessionController.getInstance();
                            c.sendMessage(new Message(17));
                            runOnUiThread(() -> {
                                Intent dep = new Intent(ConfirmationScreen.this, MenuScreen.class);
                                startActivity(dep);
                                finish();
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    worker20.start();
                    break;
                case R.id.confirmYes:
                    Thread worker21 = new Thread(() -> {
                        try {
                            SessionController c = SessionController.getInstance();
                            c.sendMessage(new Message(19));
                            Message m = c.readMessage();

                             if (m.flag() == 12) {//Transaction Successful
                                TransactionRecord record = readData(getIntent(), m);
                                c.insertRecord(record);
                                runOnUiThread(() -> {
                                    Intent dep = new Intent(ConfirmationScreen.this, transactionSuccessful.class);
                                    startActivity(dep);
                                    finish();
                                });
                            }
                            else if (m.flag() == 20 || m.flag() == 21) {//Transaction Failed
                                runOnUiThread(() -> {
                                    Intent dep = new Intent(ConfirmationScreen.this, transactionUnsuccessful.class);
                                    dep.putExtra("Error Message", m.getTextMessages().get(0));
                                    startActivity(dep);
                                    finish();
                                });
                            }
                            else{//shouldn't happen
                                System.exit(1);
                            }
                        } catch(IOException e) {
                            e.printStackTrace();
                        }
                    });
                    worker21.start();
                    break;
            }//end switch
        }//end onClick()
    };

    public TransactionRecord readData(Intent caller, Message confirmation) {
        int type = caller.getIntExtra("type", -1);
        double amount = caller.getDoubleExtra("amount", -1);
        int id = confirmation.getIntegerMessages().get(0);
        switch (type) {
            case 0://Deposit
                String accountName1 = caller.getStringExtra("accountName");
                double tarOldAmount1 = caller.getDoubleExtra("accountAmount", -1);
                double tarNewAmount1 = tarOldAmount1 + amount;
                TransactionRecord record1 = new TransactionRecord(amount, id);
                record1.setDeposit(accountName1, tarOldAmount1, tarNewAmount1);
                return record1;
            case 1://Withdrawal
                String accountName2 = caller.getStringExtra("accountName");
                double srcOldBalance2 = caller.getDoubleExtra("srcOldBalance", -1);
                double srcNewBalance2 = srcOldBalance2 - amount;
                TransactionRecord record2 = new TransactionRecord(amount, id);
                record2.setWithdrawal(accountName2, srcOldBalance2, srcNewBalance2);
                return record2;
            case 2://Transfer
                String accountName3A = caller.getStringExtra("srcAccountName");
                double srcOldBalance3 = caller.getDoubleExtra("srcAccountMax", -1);
                double srcNewBalance3 = srcOldBalance3 - amount;
                String accountName3B = caller.getStringExtra("tarAccountName");
                double tarOldBalance3 = caller.getDoubleExtra("tarAccountMax", -1);
                double tarNewBalance3 = tarOldBalance3 + amount;
                TransactionRecord record3 = new TransactionRecord(amount, id);
                record3.setTransfer(accountName3A, accountName3B, srcOldBalance3, srcNewBalance3, tarOldBalance3, tarNewBalance3);
                return record3;
            default:
                throw new IllegalArgumentException("Unexpected Account Type");
        }
    }
    @Override
    public void onBackPressed() {}//Disables Android's Back Button
}//end ConfirmationScreen