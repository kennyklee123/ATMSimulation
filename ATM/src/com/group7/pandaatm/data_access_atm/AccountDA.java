package com.group7.pandaatm.data_access_atm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.group7.pandaatm.database.Database;
import com.group7.pandaatm.entity_atm.Account;
import com.group7.pandaatm.entity_atm.DebitCard;

public class AccountDA {
	
	private Database db;
	private PreparedStatement psGetAccountInfo;
	private PreparedStatement psInsertSavings;
	private PreparedStatement psInsertChecking;
	private PreparedStatement psGetAvailableAccounts;
	
	public AccountDA(Database database) throws SQLException {
		this.db = database;
		generateStatements();
	}
	
	private void generateStatements() throws SQLException {
		
		 psGetAccountInfo = db.getDatabase().prepareStatement("SELECT `accountNumber`, `accountName`, `accountStatus`, "
		 				  										+ "`accountBal`, `accountType`, `interestRate`, `minReqBalance` "
		 				  										+ "FROM Account WHERE `accountNumber` = ? LIMIT 1;");
		 psInsertSavings = db.getDatabase().prepareStatement("INSERT INTO `Account` (`accountName`, `accountStatus`, `accountBal`, `accountType`, `interestRate`)"
		 														+ "VALUES(?, 1, ?, 0, 0.5);");
		 psInsertChecking = db.getDatabase().prepareStatement("INSERT INTO `Account` (`accountName`, `accountStatus`,`accountBal`,`accountType`,`minReqBalance`) "
		 														+ "VALUES(?, 1, ?, 1, 100.0);");
		 psGetAvailableAccounts = db.getDatabase().prepareStatement("SELECT * FROM `CardActivation` WHERE `cardNumber` = ?;");
	}
	
	public Account getAccountInfo(int accountNumber) {
		
		//Returns null if Account with given accountNumber DNE
		Account acc = null;
		try {
			psGetAccountInfo.setInt(1, accountNumber);
			ResultSet set = db.executeQuery(psGetAccountInfo, true);
			
			if(set.next()) {
				acc = new Account(set.getInt(1), set.getString(2), set.getBoolean(3), set.getDouble(4), set.getInt(5),
									set.getDouble(6), set.getDouble(7));
			}
			set.close();
		}
		catch(SQLException e) {
			System.out.println(e);
		}
		return acc;
	}//end getAccountInfo
	
	public int insertCheckingAcc(String name, double balance) {
		
		ResultSet set;
		int primaryKey = 0;
		db.lock();
		
		try {
			psInsertChecking.setString(1, name);
			psInsertChecking.setDouble(2, balance);
			db.executeStatement(psInsertChecking, false);
			set = psInsertChecking.getGeneratedKeys();
			if(set.next()) {
				primaryKey = set.getInt(1);
			}
			else {
				System.out.println("ERROR: Failed to Retrieve Primary Key.");
			}
			set.close();
		}
		catch(SQLException e)
		{
			System.out.println(e);
		}
		db.unlock();
		return primaryKey;
	}//end insertCheckingAcc
	
	public int insertSavingsAcc(String name, double balance) {
		
		ResultSet set;
		int primaryKey = 0;
		db.lock();
		
		try {
			psInsertSavings.setString(1, name);
			psInsertSavings.setDouble(2, balance);
			db.executeStatement(psInsertSavings, false);
			set = psInsertSavings.getGeneratedKeys();
			if(set.next()) {
				primaryKey = set.getInt(1);
			}
			else {
				System.out.println("ERROR: Failed to Retrieve Primary Key.");
			}
			set.close();
		}
		catch(SQLException e)
		{
			System.out.println(e);
		}
		db.unlock();
		return primaryKey;
	}//end insertCheckingAcc
	
	public ArrayList<Account> getAvailableAccounts(DebitCard debitCard) {
		
		ArrayList<Account> availableAccounts = new ArrayList<Account>();
		ResultSet set;
		
		try {
			psGetAvailableAccounts.setLong(1, debitCard.getCardNumber());
			set = db.executeQuery(psGetAvailableAccounts, true);
			db.getDatabase().setAutoCommit(false);
			while(set.next()) {
				availableAccounts.add(this.getAccountInfo(set.getInt(2)));
			}//end
			db.getDatabase().commit();
			db.getDatabase().setAutoCommit(true);
		}
		catch(SQLException e) {
			System.out.println(e);
		}
		return availableAccounts;
	}//end getAvailableAccounts
}//end AccountDA
