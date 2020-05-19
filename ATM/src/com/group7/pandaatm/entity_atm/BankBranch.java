package com.group7.pandaatm.entity_atm;

public class BankBranch {
	
	private int branchNumber;
	private String branchAddress;
	
	public BankBranch(int bN, String bA) {
		this.branchNumber = bN;
		this.branchAddress = bA;
	}

	public int getBranchNumber() {
		return branchNumber;
	}

	public String getBranchAddress() {
		return branchAddress;
	}
	
	@Override
	public String toString() {
		String str = "Branch Number: " + this.branchNumber
				   + "\nBranch Address: " + this.branchAddress + "\n";
		return str;
	}
}//end BankBranch
