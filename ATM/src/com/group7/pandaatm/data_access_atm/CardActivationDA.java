package com.group7.pandaatm.data_access_atm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.group7.pandaatm.database.Database;
import com.group7.pandaatm.entity_atm.CardActivation;

public class CardActivationDA { 

    private Database db;
    private PreparedStatement psGetCardActivationInfo;
    private PreparedStatement psInsertCardActivation;
    
    public CardActivationDA(Database database) throws SQLException{
        this.db = database;
        generateStatements();
    }
    
    private void generateStatements() throws SQLException {
        psGetCardActivationInfo = db.getDatabase().prepareStatement("SELECT `cardNumber`, `accountNumber`, `dateTimeActivated` "
        						+ "FROM `CardActivation` WHERE `cardNumber` = ? AND `accountNumber` = ? LIMIT 1;");
        psInsertCardActivation = db.getDatabase().prepareStatement("INSERT INTO `CardActivation` (`cardNumber`, "
        					   + "`accountNumber`, `dateTimeActivated`) VALUES (?, ?, ?);");
    }
    
    public CardActivation getCardActivationInfo(long cardNumber, int accountNumber){
        CardActivation cardAct = null;
        try {
            psGetCardActivationInfo.setLong(1, cardNumber);
            psGetCardActivationInfo.setInt(2, accountNumber);
            ResultSet set = db.executeQuery(psGetCardActivationInfo, true);
            if(set.next())
            {
                cardAct = new CardActivation(set.getLong(1), set.getInt(2), set.getTimestamp(3).toLocalDateTime());
            }
            set.close();
        }
        catch(SQLException e)
        {
            System.out.println(e);            
        }
        return cardAct;
    }//end getCardActivationInfo
	
	public int insertCardActivation(long cardNumber, int accountNumber) {
		ResultSet set;
		int primaryKey = 0;
		db.lock();
		
		try {
			psInsertCardActivation.setLong(1, cardNumber);
			psInsertCardActivation.setInt(2, accountNumber);
			psInsertCardActivation.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
			db.executeStatement(psInsertCardActivation, false);
			set = psInsertCardActivation.getGeneratedKeys();
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
	}//end insertCardActivation
}//end CardActivationDA
