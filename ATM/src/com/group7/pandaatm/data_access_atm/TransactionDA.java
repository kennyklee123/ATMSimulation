package com.group7.pandaatm.data_access_atm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.group7.pandaatm.database.Database;
import com.group7.pandaatm.entity_atm.Transaction;

public class TransactionDA {

	private Database db;
	private PreparedStatement psGetTransactionInfo;
	private PreparedStatement psInsertWithdrawalTransaction;// 0
	private PreparedStatement psInsertDepositTransaction;// 1
	private PreparedStatement psInsertTransferTransaction;// 2

	public TransactionDA(Database database) throws SQLException {
		this.db = database;
		generateStatements();
	}

	private void generateStatements() throws SQLException {
		psGetTransactionInfo = db.getDatabase().prepareStatement("SELECT `transactionID`, `timeDateOfTrans`, `transactionType`, "
							 + "`amount`, `targetAccNumber`, `sessionID`, `accountNumber` FROM `Transaction` WHERE `transactionID` = ? LIMIT 1;");

		psInsertWithdrawalTransaction = db.getDatabase().prepareStatement("INSERT INTO `Transaction` (`timeDateofTrans`, `transactionType`,"
									 + " `amount`, `targetAccNumber`, `sessionID`, `accountNumber`) VALUES (?,0, ?, null, ?, ? );");

		psInsertDepositTransaction = db.getDatabase().prepareStatement("INSERT INTO `Transaction` (`timeDateofTrans`, `transactionType`,"
								   + " `amount`, `targetAccNumber`, `sessionID`, `accountNumber`) VALUES (?,1, ?, null, ?, ? );");

		psInsertTransferTransaction = db.getDatabase().prepareStatement("INSERT INTO `Transaction` (`timeDateOfTrans`, `transactionType`,"
									+ " `amount`, `targetAccNumber`, `sessionID`, `accountNumber`) VALUES (?, 2, ?, ?, ?, ?);");

	}

	public Transaction getTransactionInfo(int transactionID) {
		
		Transaction trans = null;
		try {
			psGetTransactionInfo.setInt(1, transactionID);
			ResultSet set = db.executeQuery(psGetTransactionInfo, true);
			if (set.next()) { // i think time stamp is date time
				trans = new Transaction(set.getInt(1), set.getTimestamp(2).toLocalDateTime(), set.getInt(3),
						set.getDouble(4), set.getInt(5), set.getInt(6), set.getInt(7));
			}
			set.close();
		} catch (SQLException e) {
			System.out.println(e);
		}
		return trans;
	}

	public int insertWithdrawalTransaction(double amount, int sessionID, int accountNumber, boolean useLock) {
		
		ResultSet set;
		int primaryKey = 0;
		if(useLock) {db.lock();}
		try {
			psInsertWithdrawalTransaction.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
			psInsertWithdrawalTransaction.setDouble(2, amount);
			psInsertWithdrawalTransaction.setInt(3, sessionID);
			psInsertWithdrawalTransaction.setInt(4, accountNumber);
			db.executeStatement(psInsertWithdrawalTransaction, false);
			set = psInsertWithdrawalTransaction.getGeneratedKeys();
			if (set.next()) {
				primaryKey = set.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println("ERROR: Failed to Retrieve the Primary Key");
		}
		if(useLock) {db.unlock();}
		return primaryKey;
	}

	public int insertDepositTransaction(double amount, int sessionID, int accountNumber, boolean useLock) {
		
		ResultSet set;
		int primaryKey = 0;
		if(useLock) {db.lock();}
		try {
			psInsertDepositTransaction.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
			psInsertDepositTransaction.setDouble(2, amount);
			psInsertDepositTransaction.setInt(3, sessionID);
			psInsertDepositTransaction.setInt(4, accountNumber);
			db.executeStatement(psInsertDepositTransaction, false);
			set = psInsertDepositTransaction.getGeneratedKeys();
			if (set.next()) {
				primaryKey = set.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println("ERROR: Failed to Retrieve the Primary Key");
		}
		if(useLock) {db.unlock();}
		return primaryKey;
	}

	public int insertTransferTransaction(double amount, int targetAccNumber, int sessionID, int accountNumber, boolean useLock) {
		
		ResultSet set;
		int primaryKey = 0;
		if(useLock) {db.lock();}
		try {
			psInsertTransferTransaction.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
			psInsertTransferTransaction.setDouble(2, amount);
			psInsertTransferTransaction.setInt(3, targetAccNumber);
			psInsertTransferTransaction.setInt(4, sessionID);
			psInsertTransferTransaction.setInt(5, accountNumber);
			db.executeStatement(psInsertTransferTransaction, false);
			set = psInsertTransferTransaction.getGeneratedKeys();
			if (set.next()) {
				primaryKey = set.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println("ERROR: Failed to Retrieve the Primary Key");
		}
		if(useLock) {db.unlock();}
		return primaryKey;
	}
}//end TransactionDA