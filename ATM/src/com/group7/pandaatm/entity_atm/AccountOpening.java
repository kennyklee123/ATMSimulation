package com.group7.pandaatm.entity_atm;

import java.time.LocalDateTime;

import com.group7.pandaatm.database.Database;

public class AccountOpening {

	private int customerID;
	private int accountNumber;
	private LocalDateTime dateTimeOpened;
	
	public AccountOpening(int cID, int aN, LocalDateTime dTO) {
		this.customerID = cID;
		this.accountNumber = aN;
		this.dateTimeOpened = dTO;
	}

	public int getCustomerID() {
		return customerID;
	}

	public int getAccountNumber() {
		return accountNumber;
	}

	public LocalDateTime getDateTimeOpened() {
		return dateTimeOpened;
	}
	
	@Override
	public String toString() {
		String str = "Account Opening Date: " + this.dateTimeOpened.format(Database.getTimeFormat())
				   + "\nAccount Number: " + this.accountNumber 
				   + "\nCustomer ID: " + this.customerID + "\n";
		return str;
	}
}//end AccountOpening
