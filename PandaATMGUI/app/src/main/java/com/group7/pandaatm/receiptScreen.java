package com.group7.pandaatm;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.group7.pandaatm.data.SessionController;
import com.group7.pandaatm.data.TransactionRecord;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class receiptScreen extends AppCompatActivity {
    int width = 60;
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/YYYY");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_screen);

        try {
            SessionController c = SessionController.getInstance();
            ArrayList<TransactionRecord> records = c.getRecord();
            int maxLeftAmountSize = 0;
            int maxRightAmountSize = 0;
            int maxAccountNameSize = 25;
            for(int i = 0; i < records.size(); i++) {
                int size = records.get(i).findLeftSize();
                if(maxLeftAmountSize < size) {
                    maxLeftAmountSize = size;
                }
                size = records.get(i).findRightSize();
                if(maxRightAmountSize < size) {
                    maxRightAmountSize = size;
                }
            }

            String location = c.getAddress();
            String card = c.getCardNumber();
            StringBuilder finalReceipt = new StringBuilder();
            finalReceipt.append("Panda ATM Service\n");
            LocalDateTime dateTime = LocalDateTime.now();
            String line = "Date: " + String.format("%" + (width - 6) + "s", dateFormatter.format(dateTime));
            finalReceipt.append(line);
            finalReceipt.append("\n");
            line = "Time: " + String.format("%" + (width - 6) + "s", timeFormatter.format(dateTime));
            finalReceipt.append(line);
            finalReceipt.append('\n');
            line = "Location: " + String.format("%" + (width - 10) + "s", location);
            finalReceipt.append(line);
            finalReceipt.append('\n');
            line = "Card Number: " + String.format("%" + (width - 13) + "s", "XXXX XXXX XXXX " + card.substring(12, 16));
            finalReceipt.append(line);
            finalReceipt.append('\n');
            finalReceipt.append('\n');
            for(int i = 0; i < records.size(); i++) {
                finalReceipt.append(records.get(i).toString(width, maxAccountNameSize, maxLeftAmountSize, maxRightAmountSize));
            }
            finalReceipt.append('\n');
            line = "Thank you for using Panda ATM Services!";
            finalReceipt.append(line);
            finalReceipt.append('\n');

            EditText textBox = findViewById(R.id.receiptInfo);
            textBox.setText(finalReceipt.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        findViewById(R.id.finishButton).setOnClickListener(buttonClickListener);
        Thread worker25 = new Thread(() -> {
            try {
                SessionController c = SessionController.getInstance();
                c.terminateSession();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        worker25.start();
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.finishButton:
                    Intent done = new Intent(receiptScreen.this, MainActivity.class);
                    startActivity(done);
                    finish();

            }
        }


    };

    @Override
    public void onBackPressed() {}//Disables Android's Back Button
}
