package com.group7.pandaatm.controllers;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import com.group7.pandaatm.data.Message;

public class SessionTimer {

	private volatile boolean sessionThreadActive;
	private ObjectOutputStream dataOutput;
	private long sessionTimeOut;
	private Timer sessionTimer;
	
	public SessionTimer(String name, ObjectOutputStream dO, boolean isDaemon, long sTO) {
		this.sessionThreadActive = true;
		this.dataOutput = dO;
		this.sessionTimeOut = sTO;
		this.sessionTimer = new Timer(name, isDaemon);
	}//Constructor
	
	public void startTimer() {
		//Start the Timer
		this.sessionTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				System.out.println("SESSION TIMEOUT ALARM");
				sessionThreadActive = false;
				try {
					dataOutput.writeObject(new Message(1));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}//end run
			
		}, this.sessionTimeOut);
	}//end startTimer	
	
	public boolean getSessionThreadActive() {
		return this.sessionThreadActive;
	}
	
	public void refreshTimer() {
		this.sessionTimer.cancel();
		this.sessionTimer = new Timer("timerThread", true);
		
		//Start the Timer
		sessionTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				System.out.println("SESSION TIMEOUT ALARM");
				sessionThreadActive = false;
				try {
					dataOutput.writeObject(new Message(1));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
			
		}, this.sessionTimeOut );
	}//end refreshTimer
	
	public void stopTimer() {
		this.sessionTimer.cancel();
		this.sessionTimer = null;
	}
}//end Timer
