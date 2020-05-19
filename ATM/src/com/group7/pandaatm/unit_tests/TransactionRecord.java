package com.group7.pandaatm.unit_tests;

import java.util.ArrayList;

public class TransactionRecord {
    private int type;
    private int id;
    private String srcAccount;
    private double srcNewBalance;
    private double srcOldBalance;
    private String tarAccount;
    private double tarOldBalance;
    private double tarNewBalance;
    private double amount;

    public TransactionRecord(double amount, int id) {
        this.amount = amount;
        this.id = id;
    }

    public void setDeposit(String tarAccount, double tarOldBalance, double tarNewBalance) {
        this.tarAccount = tarAccount;
        this.tarOldBalance = tarOldBalance;
        this.tarNewBalance = tarNewBalance;
        this.type = 1;
    }

    public void setWithdrawal(String srcAccount, double srcOldBalance, double srcNewBalance) {
        this.srcAccount = srcAccount;
        this.srcOldBalance = srcOldBalance;
        this.srcNewBalance = srcNewBalance;
        this.type = 0;
    }

    public void setTransfer(String srcAccount, String tarAccount, double srcOldBalance, double srcNewBalance, double tarOldBalance, double tarNewBalance) {
        this.srcAccount = srcAccount;
        this.tarAccount = tarAccount;
        this.srcOldBalance = srcOldBalance;
        this.srcNewBalance = srcNewBalance;
        this.tarOldBalance = tarOldBalance;
        this.tarNewBalance = tarNewBalance;
        this.type = 2;
    }
    
    public int findLeftSize() {
    	int largest = 0;
    	double comparison = 0;
    	if(srcOldBalance > -1 && srcOldBalance > comparison) {
    		largest = String.format("$%,.2f", srcOldBalance).length();
    		comparison = srcOldBalance;
    	}
    	if(tarOldBalance > -1 && tarOldBalance > comparison) {
    		largest = String.format("$%,.2f", tarOldBalance).length();
    		comparison = tarOldBalance;
    	}
    	return largest;
    }
    
    public int findRightSize() {
    	int largest = 0;
    	double comparison = 0;
    	if(tarNewBalance > -1 && tarNewBalance > comparison) {
    		largest = String.format("$%,.2f", tarNewBalance).length();
    		comparison = tarNewBalance;
    	}
    	if(srcNewBalance > -1 && srcNewBalance > comparison) {
    		largest = String.format("$%,.2f", srcNewBalance).length();
    		comparison = srcNewBalance;
    	}
    	return largest;
    }

    public String toString(int size) {
        StringBuilder s = new StringBuilder();
    	switch (type) {
            case 0://Withdraw
            	return s.toString();
            case 1://Deposit
            	return s.toString();
            case 2://Transfer
            	return s.toString();
            default:
                throw new IllegalArgumentException("Unexpected Transaction Type");
        }
    }
    
    public String typeString() {
    	switch (type) {
	    	case 0:
	    		return "Withdrawl";
	    	case 1:
	    		return "Deposit";
	    	case 2:
	    		return "Transfer";
	    	default:
	    		throw new IllegalArgumentException("Unexpected Type provided");
    	}
    }
    
    public static void main(String[] args) {
    	ArrayList<TransactionRecord> records = new ArrayList<TransactionRecord>();
    	TransactionRecord r = new TransactionRecord(60.0, 1);
    	r.setDeposit("Christian's Checking Account", 940, 1000);
    	records.add(r);
    	r = new TransactionRecord(200, 2);
    	r.setWithdrawal("Kenny's Savings Account", 200, 0);
    	records.add(r);
    	r = new TransactionRecord(10000, 3);
    	r.setTransfer("Nick's Bank of the United States", "Jonathan's VBucks Account", 1000, 999, 10, 11);
    	records.add(r);
    	
    	
    	int maxLeftAmountSize = 0;
    	int maxRightAmountSize = 0;
    	int maxAccountNameSize = 25;
    	for(int i = 0; i < records.size(); i++) {
    		int size = records.get(i).findLeftSize();
    		if(maxLeftAmountSize < size) {
    			maxLeftAmountSize = size;
    		}
    		size = records.get(i).findRightSize();
    		if(maxRightAmountSize < size) {
    			maxRightAmountSize = size;
    		}
    	}
    	StringBuilder s = new StringBuilder();
    	int width = 60;
    	for(int i = 0; i < records.size(); i++) {
    		r = records.get(i);
    		String firstLine = new String();
    		String firstLineL = new String();
    		String firstLineR = new String();
    		firstLineL = "#" + r.id + " - " + r.typeString();
    		firstLineR = "Amount: [" + String.format("$%,.2f", r.amount) + "]";
    		firstLine = String.format("%-" + (width - firstLineR.length()) + "s", firstLineL) + firstLineR + '\n';
    		s.append(firstLine);
    		String nextLine;
    		String nextLineL;
    		String nextLineR;
    		String amount1;
    		String amount2;
    		switch(r.type) {
    			case 0://Withdrawal
    				nextLineL = " - " + String.format("%." + maxAccountNameSize + "s", r.srcAccount);
    				amount1 = String.format("%" + maxLeftAmountSize + "s", "$" + String.format("%,.2f", r.srcOldBalance));
    				amount2 = String.format("%" + maxRightAmountSize + "s", "$" + String.format("%,.2f", r.srcNewBalance));
    				nextLineR = amount1 + " -> " + amount2;
    				nextLine = String.format("%-" + (width - nextLineR.length()) + "s", nextLineL) + nextLineR + '\n';
    				s.append(nextLine);
    				break;
    			case 1://Deposit
    				nextLineL = " - " + String.format("%." + maxAccountNameSize + "s", r.tarAccount);
    				amount1 = String.format("%" + maxLeftAmountSize + "s", "$" + String.format("%,.2f", r.tarOldBalance));
    				amount2 = String.format("%" + maxRightAmountSize + "s", "$" + String.format("%,.2f", r.tarNewBalance));
    				nextLineR = amount1 + " -> " + amount2;
    				nextLine = String.format("%-" + (width - nextLineR.length()) + "s", nextLineL) + nextLineR + '\n';
    				s.append(nextLine);
    				break;
    			case 2://Transfer
    				nextLineL = " - " + String.format("%." + maxAccountNameSize + "s", r.srcAccount);
    				amount1 = String.format("%" + maxLeftAmountSize + "s", "$" + String.format("%,.2f", r.srcOldBalance));
    				amount2 = String.format("%" + maxRightAmountSize + "s", "$" + String.format("%,.2f", r.srcNewBalance));
    				nextLineR = amount1 + " -> " + amount2;
    				nextLine = String.format("%-" + (width - nextLineR.length()) + "s", nextLineL) + nextLineR + '\n';
    				s.append(nextLine);
    				nextLineL = " - " + String.format("%." + maxAccountNameSize + "s", r.tarAccount);
    				amount1 = String.format("%" + maxLeftAmountSize + "s", "$" + String.format("%,.2f", r.tarOldBalance));
    				amount2 = String.format("%" + maxRightAmountSize + "s", "$" + String.format("%,.2f", r.tarNewBalance));
    				nextLineR = amount1 + " -> " + amount2;
    				nextLine = String.format("%-" + (width - nextLineR.length()) + "s", nextLineL) + nextLineR + '\n';
    				s.append(nextLine);
    				break;
    			default: 
    				throw new IllegalArgumentException("Unexpected Type");
    		}
    		s.append('\n');
    	}
    	System.out.println(s.toString());
    }
}
