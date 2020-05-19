package com.group7.pandaatm.database;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

import com.group7.pandaatm.data_access_atm.AccountDA;
import com.group7.pandaatm.data_access_atm.AccountOpeningDA;
import com.group7.pandaatm.data_access_atm.CardActivationDA;
import com.group7.pandaatm.data_access_atm.ClientDA;
import com.group7.pandaatm.data_access_atm.DebitCardDA;
import com.group7.pandaatm.entity_atm.Client;

public class DataInitialization {

	private ClientDA clientData;
	private AccountDA accountData;
	private AccountOpeningDA accountOpeningData;
	private DebitCardDA debitCardData;
	private CardActivationDA cardActivationData;
	
	private DataInitialization(Database db) throws SQLException {
		//Erases all data in Database
		Statement stmt = db.getDatabase().createStatement();
		stmt.executeUpdate("DELETE FROM `Transaction`");
		stmt.executeUpdate("DELETE FROM `ATMSession`");
		stmt.executeUpdate("DELETE FROM `CardActivation`");
		stmt.executeUpdate("DELETE FROM `DebitCard`");
		stmt.executeUpdate("DELETE FROM `AccountOpening`");
		stmt.executeUpdate("DELETE FROM `Account`");
		stmt.executeUpdate("DELETE FROM `Client`");
		stmt.executeUpdate("DELETE FROM `ATM`");
		stmt.executeUpdate("DELETE FROM `BankBranch`");
		
		//Initialize Bank Branch (APP ONLY HAS 1 BRANCH)
		stmt.execute("INSERT INTO `BankBranch` VALUES(1 , '3801 W Temple Ave, Pomona, CA 91768');");
		//Initialize ATM's (APP HAS EXACTLY 5 UNIQUE ATM LOCATIONS)
		stmt.execute("INSERT INTO `ATM` VALUES(1, '3801 W Temple Ave, Pomona, CA 91768 - Bldg. 1', 120, 0, 5, 500, 0, 250, 750, 1000, 1000, 1);");
		stmt.execute("INSERT INTO `ATM` VALUES(2, '3801 W Temple Ave, Pomona, CA 91768 - Bldg. 17', 120, 0, 5, 500, 0, 250, 750, 1000, 1000, 1);");
		stmt.execute("INSERT INTO `ATM` VALUES(3, '3801 W Temple Ave, Pomona, CA 91768 - Marketplace', 120, 0, 5, 500, 0, 250, 750, 1000, 1000, 1);");
		stmt.execute("INSERT INTO `ATM` VALUES(4, '3801 W Temple Ave, Pomona, CA 91768 - Bronco Student Center', 120, 0, 5, 500, 0, 250, 750, 1000, 1000, 1);");
		stmt.execute("INSERT INTO `ATM` VALUES(5, '3801 W Temple Ave, Pomona, CA 91768 - Library', 15, 0, 5, 500, 0, 250, 750, 1000, 1000, 1);");
		stmt.close();
		
		//Initialize all Data Access Classes
		clientData = new ClientDA(db);
		accountData = new AccountDA(db);
		accountOpeningData = new AccountOpeningDA(db);
		debitCardData = new DebitCardDA(db);
		cardActivationData = new CardActivationDA(db);
		
		//Initialize Clients - There are 5 (Kenny Lee, Ashley Yu, Christian Devile, Nicholas Stewart, Jonathan Halim) 
		int clientKenny = clientData.insertClient("Kenny Lee", "123 Main St.", "909-696-6969", LocalDateTime.of(1999, 10, 1, 12, 30), 1);
		int clientAshley = clientData.insertClient("Ashley Yu", "345 1st St.", "909-696-6420", LocalDateTime.of(2000, 11, 28, 12, 30), 1);
		int clientChristian = clientData.insertClient("Christian Devile", "456 Valley View", "606-222-0420", LocalDateTime.of(1999, 2, 10, 12, 30), 1);
		int clientNick = clientData.insertClient("Nicholas Stewart", "567 One Infinite Loop", "111-111-1111", LocalDateTime.of(1999, 3, 24, 12, 30), 1);
		int clientJonathan = clientData.insertClient("Jonathan Halim", "789 Ashbury Pkwy.", "545-949-8100", LocalDateTime.of(2000, 7, 30, 12, 30), 1);
		int clientTannaz = clientData.insertClient("Tannaz Rezaei Damavandi", "Bldg. 8 Office 45", "909-879-5519 ", LocalDateTime.of(1999, 5, 4, 12, 30), 1);
		
		//Get Info For each Client (Needed for Debit Card Creation)
		Client clientKennyInfo = clientData.getClientInfo(clientKenny);
		Client clientAshleyInfo = clientData.getClientInfo(clientAshley);
		Client clientChristianInfo = clientData.getClientInfo(clientChristian);
		Client clientNickInfo = clientData.getClientInfo(clientNick);
		Client clientJonathanInfo = clientData.getClientInfo(clientJonathan);
		Client clientTannazInfo = clientData.getClientInfo(clientTannaz);
		
		//Initiate Accounts - # of Accounts Varies from User to User
		int kennyChecking = accountData.insertCheckingAcc("Kenny's Checking SPENDY BOI", 420.69);
		int ashleyChecking = accountData.insertCheckingAcc("Ashley's Checking", 357);
		int ashleySavings = accountData.insertSavingsAcc("Ashley's Savings", 6700);
		int christianChecking = accountData.insertCheckingAcc("Christian's Checking", 0.01);
		int christianSavingsOne = accountData.insertSavingsAcc("Christian's Savings", 3250);
		int christianSavingsTwo = accountData.insertSavingsAcc("Christian's College Fund", 7400.42);
		int nickChecking = accountData.insertCheckingAcc("Nicholas's Checking", 1270);
		int nickSavings = accountData.insertSavingsAcc("Nicholas's Savings", 23567);
		int jonathanSavings = accountData.insertSavingsAcc("Jonathan's V-Bucks Allowance", 100);
		int tannazSavings = accountData.insertSavingsAcc("Vacation Fund", 1000000);
		
		//Initiate Account Openings - 1 for each Account
		accountOpeningData.insertAccountOpening(clientKenny, kennyChecking);
		accountOpeningData.insertAccountOpening(clientAshley, ashleyChecking);
		accountOpeningData.insertAccountOpening(clientAshley, ashleySavings);
		accountOpeningData.insertAccountOpening(clientChristian, christianChecking);
		accountOpeningData.insertAccountOpening(clientChristian, christianSavingsOne);
		accountOpeningData.insertAccountOpening(clientChristian, christianSavingsTwo);
		accountOpeningData.insertAccountOpening(clientNick, nickChecking);
		accountOpeningData.insertAccountOpening(clientNick, nickSavings);
		accountOpeningData.insertAccountOpening(clientJonathan, jonathanSavings);
		accountOpeningData.insertAccountOpening(clientTannaz, tannazSavings);
		
		//Initiate Debit Cards - 1 for each Client
		long kennyCard = debitCardData.insertDebitCard(clientKennyInfo.getCustomerName() ,LocalDateTime.now().plusYears(4L), 4444, clientKenny, 1);
		long ashleyCard = debitCardData.insertDebitCard(clientAshleyInfo.getCustomerName() ,LocalDateTime.now().plusYears(2L), 1895, clientAshley, 1);
		long christianCard = debitCardData.insertDebitCard(clientChristianInfo.getCustomerName() ,LocalDateTime.now().plusYears(5L), 8491, clientChristian, 1);
		long nickCard = debitCardData.insertDebitCard(clientNickInfo.getCustomerName() ,LocalDateTime.now().plusYears(3L), 6969, clientNick, 1);
		long jonathanCard = debitCardData.insertDebitCard(clientJonathanInfo.getCustomerName() ,LocalDateTime.now(), 1234, clientJonathan, 1);
		long tannazCard = debitCardData.insertDebitCard(clientTannazInfo.getCustomerName(), LocalDateTime.now().plusYears(4L), 3560, clientTannaz, 1);
		
		//Initiate Card Activations - (Each Card May be linked to multiple accounts)
		cardActivationData.insertCardActivation(kennyCard, kennyChecking);
		cardActivationData.insertCardActivation(ashleyCard, ashleyChecking);
		cardActivationData.insertCardActivation(ashleyCard, ashleySavings);
		cardActivationData.insertCardActivation(christianCard, christianChecking);
		cardActivationData.insertCardActivation(christianCard, christianSavingsOne);
		cardActivationData.insertCardActivation(christianCard, christianSavingsTwo);
		cardActivationData.insertCardActivation(nickCard, nickChecking);
		cardActivationData.insertCardActivation(nickCard, nickSavings);
		cardActivationData.insertCardActivation(nickCard, christianSavingsTwo);
		cardActivationData.insertCardActivation(jonathanCard, jonathanSavings);
		cardActivationData.insertCardActivation(tannazCard, tannazSavings);
	}//end Constructor
	
	public static void main(String[] args) throws SQLException {
		Database db = new Database();
		new DataInitialization(db);
		System.out.println("Data Initialization Successful");
	}//end main
}//end DataInitialization
