package com.group7.pandaatm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.group7.pandaatm.data.SessionController;

import java.io.IOException;

public class balanceScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_screen);
        Thread worker30 = new Thread(() -> {
            try {
                SessionController.getInstance().setCurrentContext(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Intent pastIntent = getIntent();
        String accountName = pastIntent.getStringExtra("accountName");
        double amount = pastIntent.getDoubleExtra("amount", -1);
        boolean isChecking = pastIntent.getBooleanExtra("isChecking", true);
        EditText accountNameText = findViewById(R.id.accountTypeTxt);
        accountNameText.setText(accountName);
        EditText accountAmountText = findViewById(R.id.balanceTxt);
        accountAmountText.setText("$" + amount);
        //initialize click listeners
        findViewById(R.id.cancelButton4).setOnClickListener(buttonClickListener);
        System.out.println("Balance Screen Displaying - AccountName: " + accountName + " - Amount: " + amount);
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cancelButton4:    //if cancel button is clicked, go back to main screen
                    Intent cancel = new Intent(balanceScreen.this, MenuScreen.class);
                    startActivity(cancel);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    public void onBackPressed() {}//Disables Android's Back Button
}