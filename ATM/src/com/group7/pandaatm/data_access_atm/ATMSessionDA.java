package com.group7.pandaatm.data_access_atm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.group7.pandaatm.database.Database;
import com.group7.pandaatm.entity_atm.ATMSession;

public class ATMSessionDA
{
    private Database db;
    private PreparedStatement psGetATMSessionInfo;
    private PreparedStatement psInsertATMSession;
    private PreparedStatement psTerminateSession;
    
    public ATMSessionDA(Database database) throws SQLException{
        this.db = database;
        generateStatements();
        
    }
    private void generateStatements() throws SQLException{
        psGetATMSessionInfo = db.getDatabase().prepareStatement("Select `sessionID`, `sessionStartTime`, `sessionEndTime`,"
        				    + "`sessionActive`, `machineID`, `cardNumber` FROM ATMSession WHERE `sessionID` = ? LIMIT 1;");
        psInsertATMSession = db.getDatabase().prepareStatement("INSERT INTO `ATMSession` (`sessionStartTime`, `sessionEndTime`, " 
        				   + "`sessionActive`, `machineID`, `cardNumber`) VALUES (?, null, 1, ?, ?);");
        psTerminateSession = db.getDatabase().prepareStatement("UPDATE `ATMSession` SET `sessionEndTime` = ?, `sessionActive` = 0 WHERE"
        				   + " `sessionID` = ?;");
    }
    
    public ATMSession getATMSessionInfo(int sessionID)
    {
        ATMSession session = null;
        try {
            psGetATMSessionInfo.setInt(1,  sessionID);
            ResultSet set = db.executeQuery(psGetATMSessionInfo, true);
            if(set.next())
            {
            	LocalDateTime sessionEndTime = set.getTimestamp(3) == null ? null:set.getTimestamp(3).toLocalDateTime(); 
                session = new ATMSession(set.getInt(1), set.getTimestamp(2).toLocalDateTime(), sessionEndTime,
                        				 set.getBoolean(4), set.getInt(5), set.getLong(6));
            }
            set.close();
        }
        catch(SQLException e)
        {
            System.out.println(e);            
        }
        return session;
    }//end getATMSessionInfo
    
    public int insertATMSession(int machineID, long cardNumber) {
    	ResultSet set;
    	int primaryKey = 0;
    	db.lock();//Lock other threads out of Database until unlock called
    	try {
    		//Plug in all Data for ? in Statement
    		psInsertATMSession.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
    		psInsertATMSession.setInt(2, machineID);
    		psInsertATMSession.setLong(3, cardNumber);
    		db.executeStatement(psInsertATMSession, false);
    		set = psInsertATMSession.getGeneratedKeys();
    		//If set.next Returns True, then there is a Primary Key to retrieve
    		if(set.next()) {
    			primaryKey = set.getInt(1);
    		}
    		else {
    			System.out.println("ERROR: Failed to Retrieve Primary Key.");
    		}
    		set.close();
    	}
    	catch (SQLException e) {
    		System.out.println(e);
    	}
    	db.unlock();//Release previous Lock
    	return primaryKey;
    }//end InsertATMSession
    
    //GUI "Pressing Cancel" will call this function
    public void terminateSession(int sessionID) {
    	
    	try {
    		psTerminateSession.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
    		psTerminateSession.setInt(2, sessionID);
    		db.executeStatement(psTerminateSession, true);
    	}
    	catch(SQLException e) {
    		System.out.println(e);
    	}
    }//end terminateSession
}//end ATMSessionDA
