package com.group7.pandaatm.entity_atm;

import java.time.LocalDateTime;

public class Client {
	
	private int customerID;
	private String customerName;
	private String customerAddress;
	private String customerTel;
	private LocalDateTime customerDob;
	private int branchNumber;
	
	public Client(int cID, String cN, String cA, String cT, 
					LocalDateTime cD, int bN) {
		this.customerID = cID;
		this.customerName = cN;
		this.customerAddress = cA;
		this.customerTel = cT;
		this.customerDob = cD;
		this.branchNumber = bN;
	}

	public int getCustomerID() {
		return customerID;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public String getCustomerTel() {
		return customerTel;
	}

	public LocalDateTime getCustomerDob() {
		return customerDob;
	}

	public int getBranchNumber() {
		return branchNumber;
	}
	
	@Override
	public String toString() {
		String str = "Customer ID: " + this.customerID
				   + "\nCustomer Name: " + this.customerName + "\n";
		return str;
	}
}//end Client
