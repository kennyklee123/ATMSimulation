package com.group7.pandaatm.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReentrantLock;

import org.sqlite.SQLiteConfig;

public class Database {

	private Connection connection;
	private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("MM/dd/YYYY hh:mm:ss a") ;
	private ReentrantLock lock;
	
	public Database() {
		
		SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);
        
		String connectionPath = "jdbc:sqlite:" + System.getProperty("user.home") 
							  + File.separator + "ATMSubsystem.db";  
		this.lock = new ReentrantLock();
		
		try
		{
		    connection = DriverManager.getConnection(connectionPath, config.toProperties());
		    System.out.println("Opened Local Database");
		    connection.setAutoCommit(false);
		}
		catch (Exception e)
		{
			System.out.println(e);
		    System.exit(20);
		}
		
		this.lock.lock();
		
		try (Statement stmt = connection.createStatement();)
		{
		    generateTables(stmt);
		    connection.commit();
		    generateTriggers(stmt);
		    connection.commit();
		    System.out.println("Database Initalized");
		}
		catch (SQLException e)
		{
			System.out.println(e);
		    try
		    {
		        connection.rollback();
		    }
		    catch (SQLException e1)
		    {
		    	System.out.println(e1);
		    }
		}
		finally
		{
		    try
		    {
		        connection.setAutoCommit(true);
		    }
		    catch (SQLException e)
		    {
		    	System.out.println(e);
		    }
		}
		
		this.lock.unlock();
		
	}//end Constructor
	
	private static void generateTables(Statement stmt) throws SQLException {
		//ATM Table Generation
		stmt.executeUpdate( "CREATE TABLE IF NOT EXISTS `ATM` (" + 
							"  `machineID` INTEGER NOT NULL," + 
							"  `machineAddress` VARCHAR(45) NULL," + 
							"  `sessionTimeOut` INTEGER NOT NULL," + 
							"  `sessionActive` TINYINT NOT NULL," + 
							"  `maxPinEntryAttempts` INTEGER NOT NULL," + 
							"  `withdrawalBillsRemaining` INTEGER NOT NULL," + 
							"  `depositBillCount` INTEGER NOT NULL," + 
							"  `minBillThreshold` INTEGER NOT NULL," + 
							"  `maxBillThreshold` INTEGER NOT NULL," + 
							"  `maxWithdrawalCapacity` INTEGER NOT NULL," + 
							"  `maxDepositCapacity` INTEGER NOT NULL," + 
							"  `branchNumber` INTEGER NOT NULL," + 
							"  PRIMARY KEY (`machineID`)," + 
							"  CONSTRAINT `ATM_BankBranch`" + 
							"    FOREIGN KEY (`branchNumber`)" + 
							"    REFERENCES `BankBranch` (`branchNumber`)" + 
							"    ON DELETE RESTRICT" + 
							"    ON UPDATE CASCADE)");

		//AccountOpening Table Generation
		stmt.executeUpdate( "CREATE TABLE IF NOT EXISTS `AccountOpening` (" + 
							"  `customerID` INTEGER NOT NULL," + 
							"  `accountNumber` INTEGER NOT NULL," + 
							"  `dateTimeOpening` TIMESTAMP NOT NULL," + 
							"  PRIMARY KEY (`customerID`, `accountNumber`)," +  
							"  CONSTRAINT `AccountOpening_Client`" + 
							"    FOREIGN KEY (`customerID`)" + 
							"    REFERENCES `Client` (`customerID`)" + 
							"    ON DELETE RESTRICT" + 
							"    ON UPDATE CASCADE," + 
							"  CONSTRAINT `AccountOpening_Account`" + 
							"    FOREIGN KEY (`accountNumber`)" + 
							"    REFERENCES `Account` (`accountNumber`)" + 
							"    ON DELETE RESTRICT" + 
							"    ON UPDATE CASCADE);");
		
		//Account Table Generation
		stmt.executeUpdate( "CREATE TABLE IF NOT EXISTS `Account` (" + 
							"  `accountNumber` INTEGER NOT NULL," + 
							"  `accountName` VARCHAR(45) NOT NULL," +
							"  `accountStatus` TINYINT NULL," + 
							"  `accountBal` DOUBLE NOT NULL," + 
							"  `accountType` INTEGER NOT NULL," + 
							"  `interestRate` DOUBLE NULL," + 
							"  `minReqBalance` DOUBLE NULL," + 
							"  PRIMARY KEY (`accountNumber`));");
		
		//ATMSession Table Generation
		stmt.executeUpdate( "CREATE TABLE IF NOT EXISTS `ATMSession` (" + 
							"  `sessionID` INTEGER NOT NULL," + 
							"  `sessionStartTime` TIMESTAMP NOT NULL," + 
							"  `sessionEndTime` TIMESTAMP NULL," + 
							"  `sessionActive` TINYINT NOT NULL," + 
							"  `machineID` INTEGER NOT NULL," + 
							"  `cardNumber` BIGINT NULL," + 
							"  PRIMARY KEY (`sessionID`)," + 
							"  CONSTRAINT `ATMSession_ATM`" + 
							"    FOREIGN KEY (`machineID`)" + 
							"    REFERENCES `ATM` (`machineID`)" + 
							"    ON DELETE CASCADE" + 
							"    ON UPDATE CASCADE," + 
							"  CONSTRAINT `ATMSession_DebitCard`" + 
							"    FOREIGN KEY (`cardNumber`)" + 
							"    REFERENCES `DebitCard` (`cardNumber`)" + 
							"    ON DELETE SET NULL" + 
							"    ON UPDATE CASCADE);");
		
		//BankBranch Table Generation
		stmt.executeUpdate( "CREATE TABLE IF NOT EXISTS `BankBranch` (" + 
							"  `branchNumber` INTEGER NOT NULL," + 
							"  `branchAddress` VARCHAR(45) NOT NULL," + 
							"  PRIMARY KEY (`branchNumber`));");
		
		//CardActivation Table Generation
		stmt.executeUpdate( "CREATE TABLE IF NOT EXISTS `CardActivation` (" + 
							"  `cardNumber` BIGINT NULL," + 
							"  `accountNumber` INTEGER NULL," + 
							"  `dateTimeActivated` TIMESTAMP NOT NULL," + 
							"  PRIMARY KEY (`cardNumber`, `accountNumber`)," + 
							"  CONSTRAINT `CardActivation_DebitCard`" + 
							"    FOREIGN KEY (`cardNumber`)" + 
							"    REFERENCES `DebitCard` (`cardNumber`)" + 
							"    ON DELETE SET NULL" + 
							"    ON UPDATE RESTRICT," + 
							"  CONSTRAINT `CardActivation_Account`" + 
							"    FOREIGN KEY (`accountNumber`)" + 
							"    REFERENCES `Account` (`accountNumber`)" + 
							"    ON DELETE RESTRICT" + 
							"    ON UPDATE CASCADE);");
		
		//Client Table Generation
		stmt.executeUpdate( "CREATE TABLE IF NOT EXISTS `Client` (" + 
							"  `customerID` INTEGER NOT NULL," + 
							"  `customerName` VARCHAR(45) NOT NULL," + 
							"  `customerAddress` VARCHAR(45) NOT NULL," + 
							"  `customerTel` VARCHAR(45) NOT NULL," + 
							"  `customerDob` TIMESTAMP NOT NULL," + 
							"  `branchNumber` INTEGER NOT NULL," + 
							"  PRIMARY KEY (`customerID`)," + 
							"  CONSTRAINT `Client_BankBranch`" + 
							"    FOREIGN KEY (`branchNumber`)" + 
							"    REFERENCES `BankBranch` (`branchNumber`)" + 
							"    ON DELETE RESTRICT" + 
							"    ON UPDATE CASCADE);");
		
		//DebitCard Table Generation
		stmt.executeUpdate( "CREATE TABLE IF NOT EXISTS `DebitCard` (" + 
							"  `cardNumber` BIGINT NOT NULL," + 
							"  `cardHolderName` VARCHAR(45) NOT NULL," + 
							"  `cardExpDate` TIMESTAMP NOT NULL," + 
							"  `pinNumber` INTEGER NOT NULL," + 
							"  `customerID` INTEGER NOT NULL," + 
							"  `locked` TINYINT NOT NULL," + 
							"  `branchNumber` INTEGER NOT NULL," + 
							"  PRIMARY KEY (`cardNumber`)," + 
							"  CONSTRAINT `DebitCard_Client`" + 
							"    FOREIGN KEY (`customerID`)" + 
							"    REFERENCES `Client` (`customerID`)" + 
							"    ON DELETE RESTRICT" + 
							"    ON UPDATE CASCADE," + 
							"  CONSTRAINT `DebitCard_BankBranch`" + 
							"    FOREIGN KEY (`branchNumber`)" + 
							"    REFERENCES `BankBranch` (`branchNumber`)" + 
							"    ON DELETE RESTRICT" + 
							"    ON UPDATE CASCADE);");
		
		//Transaction Table Generation
		stmt.executeUpdate( "CREATE TABLE IF NOT EXISTS `Transaction` (" + 
							"  `transactionID` INTEGER NOT NULL," + 
							"  `timeDateOfTrans` TIMESTAMP NOT NULL," + 
							"  `transactionType` INTEGER NOT NULL," + 
							"  `amount` DOUBLE NOT NULL," + 
							"  `targetAccNumber` INTEGER NULL," + 
							"  `sessionID` INTEGER NOT NULL," + 
							"  `accountNumber` INTEGER NOT NULL," + 
							"  PRIMARY KEY (`transactionID`)," + 
							"  CONSTRAINT `Transaction_ATMSession`" + 
							"    FOREIGN KEY (`sessionID`)" + 
							"    REFERENCES `ATMSession` (`sessionID`)" + 
							"    ON DELETE RESTRICT" + 
							"    ON UPDATE CASCADE," + 
							"  CONSTRAINT `Transaction_Account`" + 
							"    FOREIGN KEY (`accountNumber`)" + 
							"    REFERENCES `Account` (`accountNumber`)" + 
							"    ON DELETE RESTRICT" + 
							"    ON UPDATE CASCADE);");
	}//end generateTablesAndTriggers
	
	//Automatically performs updates on tables when executing certain statements on certain tables
	private static void generateTriggers(Statement stmt) throws SQLException {
		
		//Trigger to keep ATM Session Status in Sync with ATMSession Table (Part 1)
		stmt.executeUpdate( "CREATE TRIGGER IF NOT EXISTS sessionActiveTrigger AFTER INSERT ON `ATMSession` BEGIN " +
							"UPDATE `ATM` SET `sessionActive` = 1 WHERE `machineID` = new.machineID; END");
		//Trigger to keep ATM Session Status in Sync with ATMSession Table (Part 2)
		stmt.executeUpdate( "CREATE TRIGGER IF NOT EXISTS sessionInactiveTrigger AFTER UPDATE ON `ATMSession` WHEN " +
							"new.sessionActive = 0 BEGIN UPDATE `ATM` SET `sessionActive` = 0 WHERE `machineID` " +
							"= new.machineID; END");
		//Trigger to keep Account Balance & Bill Count in Sync with each Successful Transaction Made (Withdrawal)
		stmt.executeUpdate( "CREATE TRIGGER IF NOT EXISTS withdrawalSuccessfulTrigger AFTER INSERT ON `Transaction` WHEN " +
				            "new.transactionType = 0 BEGIN UPDATE `Account` SET `accountBal` = `accountBal` - new.amount WHERE " +
				            "`accountNumber` = new.accountNumber; UPDATE `ATM` SET `withdrawalBillsRemaining` = `withdrawalBillsRemaining` - " +
							"new.amount / 20 WHERE `machineID` = (SELECT `machineID` FROM `ATMSession` WHERE `sessionID` = new.sessionID); END");
		//Trigger to keep Account Balance in Sync with each Successful Transaction Made (Deposit)
		stmt.executeUpdate( "CREATE TRIGGER IF NOT EXISTS depositSuccessfulTrigger AFTER INSERT ON `Transaction` WHEN " +
	            			"new.transactionType = 1 BEGIN UPDATE `Account` SET `accountBal` = `accountBal` + new.amount WHERE " +
	            			"`accountNumber` = new.accountNumber; END");
		//Trigger to keep Account Balance in Sync with each Successful Transaction Made (Transfer)
		stmt.executeUpdate( "CREATE TRIGGER IF NOT EXISTS transferSuccessfulTrigger AFTER INSERT ON `Transaction` WHEN " +
			    			"new.transactionType = 2 BEGIN UPDATE `Account` SET `accountBal` = `accountBal` + new.amount WHERE " +
			    			"`accountNumber` = new.targetAccNumber; UPDATE `Account` SET `accountBal` = `accountBal` - new.amount WHERE " +
			    			"`accountNumber` = new.accountNumber; END");
	}//end generateTriggers
	
	public ResultSet executeQuery(PreparedStatement stmt, boolean useLock) throws SQLException {
		
		if(useLock) {
			this.lock.lock();	
		}
		ResultSet set;
		try {
			set = stmt.executeQuery();
		}
		catch (SQLException e) {
			this.lock.unlock();
			throw e;
		}
		if(useLock) {
			this.lock.unlock();	
		}
		return set;
	}//end executeQuery
	
	public void executeStatement(PreparedStatement stmt, boolean useLock) throws SQLException {
		
		if(useLock) {
			this.lock.lock();	
		}
		try {
			  stmt.executeUpdate();
			}
		catch (SQLException e) {
		  this.lock.unlock();
		  throw e;
		}
		if(useLock) {
			this.lock.unlock();	
		}
	}//end executeStatement
	
	public Connection getDatabase() {
		return this.connection;
	}
	
	public static DateTimeFormatter getTimeFormat() {
		return timeFormatter;
	}
	
	public void lock() {
		this.lock.lock();
	}
	
	public void unlock() {
		this.lock.unlock();
	}
}//end Database