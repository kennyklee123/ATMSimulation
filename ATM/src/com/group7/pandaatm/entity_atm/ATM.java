package com.group7.pandaatm.entity_atm;

public class ATM {
	
	private int machineID;
	private String machineAddress;
	private int sessionTimeOut;
	private boolean sessionActive;
	private int maxPinEntryAttempts;
	private int withdrawalBillsRemaining;	//The number of bills currently in withdrawal bill box
	private int depositBillCount;			//The number of bills currently in deposit bill box
	private int minBillThreshold;			//Notifies bank when withdrawal bill box is low
	private int maxBillThreshold;			//Notifies bank when deposit bill boxcapacity is high	
	private int maxWithdrawalCapacity;		//How much space ATM has to refill ATM
	private int maxDepositCapacity;			//How much space ATM has to store deposited bills before needing to be emptied
	private int branchNumber;
	
	public ATM(int mID, String mA, int sTO, boolean sA, int mPEA, int wBR, int dBC,
				int minBT, int maxBT, int mWC, int mDC, int bN) {
		this.machineID = mID;
		this.machineAddress = mA;
		this.sessionTimeOut = sTO;
		this.sessionActive = sA;
		this.maxPinEntryAttempts = mPEA;
		this.withdrawalBillsRemaining = wBR;
		this.depositBillCount = dBC;
		this.minBillThreshold = minBT;
		this.maxBillThreshold = maxBT;
		this.maxWithdrawalCapacity = mWC;
		this.maxDepositCapacity = mDC;
		this.branchNumber = bN;
	}
	
	public int getMachineID() {
		return machineID;
	}

	public String getMachineAddress() {
		return machineAddress;
	}

	public int getSessionTimeOut() {
		return sessionTimeOut;
	}

	public boolean isSessionActive() {
		return sessionActive;
	}

	public int getMaxPinEntryAttempts() {
		return maxPinEntryAttempts;
	}

	public int getWithdrawalBillsRemaining() {
		return withdrawalBillsRemaining;
	}

	public int getDepositBillCount() {
		return depositBillCount;
	}

	public int getMinBillThreshold() {
		return minBillThreshold;
	}

	public int getMaxBillThreshold() {
		return maxBillThreshold;
	}

	public int getMaxWithdrawalCapacity() {
		return maxWithdrawalCapacity;
	}

	public int getMaxDepositCapacity() {
		return maxDepositCapacity;
	}

	public int getBranchNumber() {
		return branchNumber;
	}

	@Override
	public String toString() {
		String str = "Machine ID: " + this.machineID
				   + "\nMachine Address: " + this.machineAddress
				   + "\nCurrent Withdrawal Bills Available: " + this.withdrawalBillsRemaining
				   + "\nDeposit Bill Count: " + this.depositBillCount
				   + "\nSession Active: " + this.sessionActive + "\n";
		return str;
	}
}//end ATM
