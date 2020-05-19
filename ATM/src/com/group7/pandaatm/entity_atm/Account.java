package com.group7.pandaatm.entity_atm;

public class Account {

	private int accountNumber;
	private String accountName;
	private boolean accountStatus;
	private double accountBal;
	private int accountType;
	private double interestRate;
	private double minReqBalance;
	
	public Account(int aN, String aName, boolean aS, double aB, int aT,
					double iR, double mRB) {
		this.accountNumber = aN;
		this.accountName = aName;
		this.accountStatus = aS;
		this.accountBal = aB;
		this.accountType = aT;
		this.interestRate = iR;
		this.minReqBalance = mRB;
	}

	public String getAccountName() {
		return accountName;
	}

	public int getAccountNumber() {
		return accountNumber;
	}

	public boolean getAccountStatus() {
		return accountStatus;
	}

	public int getAccountType() {
		return accountType;
	}

	public double getInterestRate() {
		return interestRate;
	}

	public double getMinReqBalance() {
		return minReqBalance;
	}
	
	public double getAccountBal() {
		return accountBal;
	}
	
	@Override
	public String toString() {
		String str = new String();
		str = "Account Number: " + this.accountNumber
			+ "\nAccount Name: " + this.accountName
		    + "\nAccount Status: " + this.accountStatus
		    + "\nAccount Balance: $" + this.accountBal
		    + "\nAccount Type: " + this.accountType;
		
		if(this.accountType  == 0) {
			str += "\nInterest Rate: " + this.interestRate + "%" + "\n";
		}
		else {
			str += "\nMinimum Required Balance: $" + this.minReqBalance + "\n";
		}
		return str;
	}
}//end Account