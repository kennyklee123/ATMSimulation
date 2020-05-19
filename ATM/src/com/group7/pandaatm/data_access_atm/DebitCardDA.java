package com.group7.pandaatm.data_access_atm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Random;

import com.group7.pandaatm.database.Database;
import com.group7.pandaatm.entity_atm.DebitCard;

public class DebitCardDA {
	
	private Database db;
	private Random r = new Random();
	private PreparedStatement psGetDebitCardInfo;
	private PreparedStatement psInsertDebitCard;
	private PreparedStatement psLockDebitCard;

	public DebitCardDA(Database database) throws SQLException {
		this.db = database;
		generateStatements();
	}

	private void generateStatements() throws SQLException {
		psGetDebitCardInfo = db.getDatabase().prepareStatement("SELECT `cardNumber`, `cardHolderName`, `cardExpDate`, `pinNumber`,"
						   + "`customerID`, `locked`, `branchNumber` FROM `DebitCard` WHERE `cardNumber` = ? LIMIT 1;");
		psInsertDebitCard = db.getDatabase().prepareStatement("INSERT INTO `DebitCard` (`cardNumber`, `cardHolderName`, `cardExpDate`, `pinNumber`, `customerID`, `locked`, `branchNumber`) "
						  + "VALUES (?, ?, ?, ?, ?, 0, ?);");
		psLockDebitCard = db.getDatabase().prepareStatement("UPDATE `DebitCard` SET `locked` = 1 WHERE `cardNumber` = ?;");
	}

	public DebitCard getDebitCardInfo(long cardNumber) {
		DebitCard d = null;
		try {
			psGetDebitCardInfo.setLong(1, cardNumber);
			ResultSet set = db.executeQuery(psGetDebitCardInfo, true);
			if (set.next()) {
				d = new DebitCard(set.getLong(1), set.getString(2), set.getTimestamp(3).toLocalDateTime(), set.getInt(4),
						set.getInt(5), set.getBoolean(6), set.getInt(7));
			}
			set.close();
		} catch (SQLException e) {
			System.out.println(e);
		}
		return d;
	}// end getDebitCardInfo

	public long insertDebitCard(String cardHolderName, LocalDateTime cardExpDate, int pin, int customerID,
			int branchNumber) {
		long primaryKey = 0;
		boolean unique = false;
		
		//Loops to ensure there are no duplicate card numbers
		while (!unique) {
			primaryKey = generateCardNumber();
			try {
				psInsertDebitCard.setLong(1, primaryKey);
				psInsertDebitCard.setString(2, cardHolderName);
				psInsertDebitCard.setTimestamp(3, Timestamp.valueOf(cardExpDate));
				psInsertDebitCard.setInt(4, pin);
				psInsertDebitCard.setInt(5, customerID);
				psInsertDebitCard.setInt(6, branchNumber);
				db.executeStatement(psInsertDebitCard, true);

				unique = true;
			} catch (SQLException e) {
				System.out.println(e);
				//Error code for duplicate primary key (Code 19)
				if (e.getErrorCode() == 19) {
					System.out.println("Duplicate Card Number Generated. Trying Again...");
				} else {
					System.out.println("Fatal Exception Occurred.");
					System.exit(20);
				}
			}
		} // end while
		return primaryKey;
	}// end insertDebitCard

	public void lockDebitCard(long cardNumber) {
		try {
    		psLockDebitCard.setLong(1, cardNumber);
    		db.executeStatement(psLockDebitCard, true);
    	}
    	catch(SQLException e) {
    		System.out.println(e);
    	}
	}//end lockDebitCard
	
	private long generateCardNumber() {
		int counter = 2;
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("4");// First digit of card is same (since it is one bank)
		// 4 is the digit for VISA
		while (counter <= 16) {
			int generate = r.nextInt(9);
			stringBuffer.append(generate);
			counter++;
		}
		String card_digits = stringBuffer.toString();
		return Long.parseLong(card_digits);
	}// end generateCardNumber
}// end DebitCardDA
