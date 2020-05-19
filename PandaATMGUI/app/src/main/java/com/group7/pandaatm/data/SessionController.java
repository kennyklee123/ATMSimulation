package com.group7.pandaatm.data;

import android.content.Intent;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.group7.pandaatm.MainActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class SessionController {

    //Production Program IP: supercomp.servegame.com
    //private static final String url = "192.168.1.100";
    private static final String url = "supercomp.servegame.com";
    private static final int port = 6924;

    private ObjectInputStream dataInput;
    private ObjectOutputStream dataOutput;

    private String debitCardName;
    private String atmAddress;
    private String cardNumber;
    private ArrayList<TransactionRecord> record;
    private AppCompatActivity currentContext;
    private LinkedBlockingQueue<Message> queue;

    private static SessionController c;
    private boolean isListening;

    public static SessionController getInstance() throws IOException {
        if(c == null) {
            c = new SessionController();
        }
        return c;
    }

    //Throws Exception, must be handled with GUi to show alert that Internet is not working (server is down)
    private SessionController() throws IOException {
        Socket serverConnection = new Socket(url, port);
        dataOutput = new ObjectOutputStream(serverConnection.getOutputStream());
        dataInput = new ObjectInputStream(serverConnection.getInputStream());
        record = new ArrayList<TransactionRecord>();
        queue = new LinkedBlockingQueue<Message>();
        isListening = true;
        Thread ioWorker = new Thread(() -> {
            Message m = null;
            try {
                while(isListening) {
                    m = (Message) dataInput.readObject();
                    if(m.flag() == 1) {
                        isListening = false;
                        Handler handler = new Handler(currentContext.getMainLooper());
                        handler.post(() -> {
                            System.out.println("Sending Exit Alert");
                            terminateSession();
                            Intent timeout = new Intent(currentContext, MainActivity.class);
                            timeout.putExtra("timeout", true);
                            currentContext.startActivity(timeout);
                            currentContext.finish();
                        });
                    }
                    else {
                        queue.offer(m);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        ioWorker.start();
    }

    public void setCurrentContext(AppCompatActivity c) { this.currentContext = c; }

    public void setCardName(String name) {
        this.debitCardName = name;
    }

    public void setAddress(String address) {
        this.atmAddress = address;
    }

    public void setCardNumber(String number) { this.cardNumber = number; }

    public String getCardName() {
        return debitCardName;
    }

    public String getAddress() {
        return atmAddress;
    }

    public String getCardNumber() { return cardNumber; }

    public void sendMessage(Message m) throws IOException {
        dataOutput.writeObject(m);
    }

    public Message readMessage() throws IOException {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void terminateSession(){
        try {
            dataOutput.close();
            c = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void insertRecord(TransactionRecord r) {
        record.add(r);
    }

    public ArrayList<TransactionRecord> getRecord() {
        return record;
    }
}
