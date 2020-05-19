package com.group7.pandaatm.data_access_atm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.group7.pandaatm.database.Database;
import com.group7.pandaatm.entity_atm.ATM;

public class ATMDA {

	private Database db;
	private PreparedStatement psGetATMInfo;
	private PreparedStatement psInsertATM;
	private PreparedStatement psUpdateDepositBillCount;

	public ATMDA(Database database) throws SQLException {
		this.db = database;
		generateStatements();
	}

	private void generateStatements() throws SQLException {
		psGetATMInfo = db.getDatabase().prepareStatement("SELECT `machineID`,`machineAddress`,`sessionTimeOut`,`sessionActive`,`maxPinEntryAttempts`,"
					 + "`withdrawalBillsRemaining`,`depositBillCount`,`minBillThreshold`,`maxBillThreshold`,`maxWithdrawalCapacity`,`maxDepositCapacity`,"
					 + "`branchNumber` FROM ATM WHERE `machineID` = ? LIMIT 1;");
		//sessionTimeOut saved as seconds
		psInsertATM = db.getDatabase().prepareStatement("INSERT INTO `ATM` (`machineAddress`,`sessionTimeOut`,`sessionActive`,`maxPinEntryAttempts`,"
					+ "`withdrawalBillsRemaining`,`depositBillCount`,`minBillThreshold`,`maxBillThreshold`,`maxWithdrawalCapacity`,`maxDepositCapacity`,"
			        + "`branchNumber`) VALUES(?, 45, 0, 5, 500, 0, 250, 750, 1000, 1000, ?);");
		psUpdateDepositBillCount = db.getDatabase().prepareStatement("UPDATE `ATM` SET `depositBillCount` = (? + `depositBillCount`) WHERE `machineID` = ?;");
	}
	
	public ATM getATMInfo(int machineID) {

		ATM atm = null;
		try {
			psGetATMInfo.setInt(1, machineID);
			ResultSet set = db.executeQuery(psGetATMInfo, true);
			if (set.next()) {
				atm = new ATM(set.getInt(1), set.getString(2), set.getInt(3), set.getBoolean(4), set.getInt(5), set.getInt(6), set.getInt(7),
								set.getInt(8), set.getInt(9), set.getInt(10), set.getInt(11), set.getInt(12));
			}
			set.close();
		} 
		catch (SQLException e) {
			System.out.println(e);
		}
		return atm;
	}//end getATMInfo
	
	public int insertATM(String address, int branchNumber) {
		
		ResultSet set;
		int primaryKey = 0;
		db.lock();
		
		try {
			//Assigns Parameters Values to "?'s" for Database Table
			psInsertATM.setString(1, address);
			psInsertATM.setInt(2, branchNumber);
			
			db.executeStatement(psInsertATM, false);
			set = psInsertATM.getGeneratedKeys();
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
	}//end insertATM
	
	public void updateDepositBillCount(int depositBillCount, int machineID) {		
		try {
    		psUpdateDepositBillCount.setInt(1, depositBillCount);
    		psUpdateDepositBillCount.setInt(2, machineID);
    		db.executeStatement(psUpdateDepositBillCount, true);
    	}
    	catch(SQLException e) {
    		System.out.println(e);
    	}
	}//end updateDepositBillCount
}//end ATMDA
