package com.group7.pandaatm.data_access_atm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.group7.pandaatm.database.Database;
import com.group7.pandaatm.entity_atm.Client;

public class ClientDA { 

    private Database db;
    private PreparedStatement psGetClientInfo;
    private PreparedStatement psInsertClient;
    
    public ClientDA(Database database) throws SQLException{
        this.db = database;
        generateStatements();
    }
    
    private void generateStatements() throws SQLException {
        psGetClientInfo = db.getDatabase().prepareStatement("SELECT `customerID`, `customerName`, `customerAddress`, `customerTel`,"
                         + "`customerDob`, `branchNumber` FROM `Client` WHERE `customerID` = ? LIMIT 1;");
        psInsertClient = db.getDatabase().prepareStatement("INSERT INTO `Client` (`customerName`, `customerAddress`, `customerTel`, `customerDob`, `branchNumber`)"
        											+ " VALUES (?, ?, ?, ?, ?);");
    }
    
    public Client getClientInfo(int customerID){
        Client c = null;
        try {
            psGetClientInfo.setInt(1, customerID);
            ResultSet set = db.executeQuery(psGetClientInfo, true);
            if(set.next())
            {
                c = new Client(set.getInt(1), set.getString(2), set.getString(3), set.getString(4), set.getTimestamp(5).toLocalDateTime(), set.getInt(6));
            }
            set.close();
        }
        catch(SQLException e)
        {
            System.out.println(e);            
        }
        return c;
    }
    
    public int insertClient(String customerName, String customerAddress, String customerTel, LocalDateTime customerDob, int branchNumber) {
    	ResultSet set;
		int primaryKey = 0;
		
		db.lock();
		try {
			psInsertClient.setString(1, customerName);
			psInsertClient.setString(2, customerAddress);
			psInsertClient.setString(3, customerTel);
			psInsertClient.setTimestamp(4, Timestamp.valueOf(customerDob));
			psInsertClient.setInt(5, branchNumber);
			db.executeStatement(psInsertClient, false);
			set = psInsertClient.getGeneratedKeys();
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
    }//end insertClient  
}//end ClientDA