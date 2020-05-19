package com.group7.pandaatm.data_access_atm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.group7.pandaatm.database.Database;
import com.group7.pandaatm.entity_atm.BankBranch;

public class BankBranchDA {

	private Database db;
	private PreparedStatement psGetBankBranchInfo;
	private PreparedStatement psInsertBankBranch;

	public BankBranchDA(Database database) throws SQLException {
		this.db = database;
		generateStatements();
	}

	private void generateStatements() throws SQLException {
		psGetBankBranchInfo = db.getDatabase().prepareStatement("SELECT `branchNumber`, `branchAddress` " 
							+ "FROM BankBranch WHERE `branchNumber` = ? LIMIT 1;");
		psInsertBankBranch = db.getDatabase().prepareStatement("INSERT INTO `BankBranch` (`branchAddress`) VALUES(?);");
	}

	public BankBranch getBankBranchInfo(int branchNumber) {

		BankBranch bb = null;

		try {
			psGetBankBranchInfo.setInt(1, branchNumber);
			ResultSet set = db.executeQuery(psGetBankBranchInfo, true);
			if (set.next()) {
				bb = new BankBranch(set.getInt(1), set.getString(2));
			}
			set.close();
		} catch (SQLException e) {
			System.out.println(e);
		}
		return bb;
	}// end getBankBranchInfo

	public int insertBankBranch(String address) {

		ResultSet set;
		int primaryKey = 0;
		db.lock();

		try {
			// Assigns Parameters Values to "?" for Database Table
			psInsertBankBranch.setString(1, address);

			db.executeStatement(psInsertBankBranch, false);
			set = psInsertBankBranch.getGeneratedKeys();
			if (set.next()) {
				primaryKey = set.getInt(1);
			} else {
				System.out.println("ERROR: Failed to Retrieve Primary Key.");
			}
			set.close();
		} catch (SQLException e) {
			System.out.println(e);
		}
		db.unlock();
		return primaryKey;
	}//end insertBankBranch
}// end BankBranchDA
