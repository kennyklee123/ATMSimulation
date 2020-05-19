package com.group7.pandaatm.entity_atm;

import java.time.LocalDateTime;

import com.group7.pandaatm.database.Database;

public class Transaction {

	private int transactionID;
	private LocalDateTime timeDateOfTrans;
	private int transactionType;
	private double amount;
	private int targetAccNumber;
	private int sessionID;
	private int accountNumber; //accountNumber is the Source Account Number
	
	public Transaction(int tID, LocalDateTime tDOT, int tT, double a,
						int tAN, int sID, int aN) {
		this.transactionID = tID;
		this.timeDateOfTrans = tDOT;
		this.transactionType = tT;
		this.amount = a;
		this.targetAccNumber = tAN;
		this.sessionID = sID;
		this.accountNumber = aN;
	}

	public int getTransactionID() {
		return transactionID;
	}

	public LocalDateTime getTimeDateOfTrans() {
		return timeDateOfTrans;
	}

	public int getTransactionType() {
		return transactionType;
	}

	public double getAmount() {
		return amount;
	}

	public int getTargetAccNumber() {
		return targetAccNumber;
	}

	public int getSessionID() {
		return sessionID;
	}

	public int getAccountNumber() {
		return accountNumber;
	}
	
	@Override
	public String toString() {
		String str = new String();
		str = "Transaction ID: " + this.transactionID
		    + "\nTransaction Date/Time: " + this.timeDateOfTrans.format(Database.getTimeFormat())
		    + "\nAmount: " + this.amount
		    + "\nSession ID: " + this.sessionID
		    + "\nSource Account Number: " + this.accountNumber;
		
		if(this.transactionType == 0) {
			str += "\nTransaction Type: Withdraw\n";
		}
		else if(this.transactionType == 1) {
			str += "\nTransaction Type: Deposit\n";
		}
		else {
			str += "\nTransaction Type: Transfer" 
				+  "\nTarget Account Number: " + this.targetAccNumber + "\n";
		}
		return str;
	}
}//end Transaction
