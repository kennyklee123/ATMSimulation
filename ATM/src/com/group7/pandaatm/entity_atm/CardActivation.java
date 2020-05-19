package com.group7.pandaatm.entity_atm;

import java.time.LocalDateTime;

import com.group7.pandaatm.database.Database;

public class CardActivation {
	
	private long cardNumber;
	private int accountNumber;
	private LocalDateTime dateTimeActivated;
	
	public CardActivation(long cN, int aN, LocalDateTime dTA) {
		this.cardNumber = cN;
		this.accountNumber = aN;
		this.dateTimeActivated = dTA;
	}

	public long getCardNumber() {
		return cardNumber;
	}

	public int getAccountNumber() {
		return accountNumber;
	}
	
	/*
	 * This function returns the date the card was activated.
	 * In other words, function is a getter for dateTimeActivated.
	 */
	public LocalDateTime viewActivationDetails() {
		return dateTimeActivated;
	}
	
	@Override
	public String toString() {
		String str = "Card Number: " + this.cardNumber
				   + "\nAccount Number: " + this.accountNumber
				   + "\nActivation Date: " + this.dateTimeActivated.format(Database.getTimeFormat()) + "\n";
		return str;
	}
}//end CardActivation
