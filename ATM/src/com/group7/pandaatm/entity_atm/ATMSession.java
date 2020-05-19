package com.group7.pandaatm.entity_atm;

import java.time.LocalDateTime;

import com.group7.pandaatm.database.Database;

public class ATMSession {

	private int sessionID;
	private LocalDateTime sessionStartTime;
	private LocalDateTime sessionEndTime;
	private boolean sessionActive;
	private int machineID;
	private long cardNumber;
	
	public ATMSession(int sID, LocalDateTime sST, LocalDateTime sET,
						boolean sA, int mID, long cN) {
		this.sessionID = sID;
		this.sessionStartTime = sST;
		this.sessionEndTime = sET;
		this.sessionActive = sA;
		this.machineID = mID;
		this.cardNumber = cN;
	}

	public int getSessionID() {
		return sessionID;
	}

	public LocalDateTime getSessionStartTime() {
		return sessionStartTime;
	}

	public LocalDateTime getSessionEndTime() {
		return sessionEndTime;
	}

	public boolean isSessionActive() {
		return sessionActive;
	}

	public int getMachineID() {
		return machineID;
	}

	public long getCardNumber() {
		return cardNumber;
	}
	
	@Override
	public String toString() {
		String endTime = this.sessionEndTime == null ? null: this.sessionEndTime.format(Database.getTimeFormat());
		String str = "Session ID: " + this.sessionID
				   + "\nStart Time: " + this.sessionStartTime.format(Database.getTimeFormat())
				   + "\nEnd Time: " + endTime
				   + "\nSession Active: " + this.sessionActive
				   + "\nCard Number: " + this.cardNumber + "\n";
		return str;
	}
}//end ATMSession
