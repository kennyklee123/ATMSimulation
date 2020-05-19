package com.group7.pandaatm.controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDateTime;

import com.group7.pandaatm.data.Message;
import com.group7.pandaatm.data_access_atm.ATMDA;
import com.group7.pandaatm.data_access_atm.ATMSessionDA;
import com.group7.pandaatm.data_access_atm.AccountDA;
import com.group7.pandaatm.data_access_atm.DebitCardDA;
import com.group7.pandaatm.database.Database;
import com.group7.pandaatm.entity_atm.ATM;
import com.group7.pandaatm.entity_atm.ATMSession;
import com.group7.pandaatm.entity_atm.DebitCard;

public class SessionHandler implements Runnable {

	private Database db;
	private ATMDA atmData;
	private ATMSessionDA sessionData;
	private DebitCardDA debitCardData;
	private DebitCard debitCard;
	private AccountDA accountData;
	private SessionTimer sessionTimer;
	private ObjectInputStream dataInput;
	private ObjectOutputStream dataOutput;
	private String clientName;

	public SessionHandler(Database db, Socket s) throws IOException {
		this.db = db;
		try {
			atmData = new ATMDA(db);
			sessionData = new ATMSessionDA(db);
			accountData = new AccountDA(db);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dataOutput = new ObjectOutputStream(s.getOutputStream());
		dataInput = new ObjectInputStream(s.getInputStream());
		clientName = s.getInetAddress().getCanonicalHostName();
		System.out.println("Recieved Client Connection: " + clientName);
	}

	@Override
	public void run() {
		Thread.currentThread().setName(clientName);
		try {
			ATM atm = requestATMAccess();
			// Access Granted, ATM is available for use
			if (atm != null) {

				// Timer to Terminate Session if User is Idle
				sessionTimer = new SessionTimer("timerThread", dataOutput, true, (long) (atm.getSessionTimeOut() * 1000L));
				sessionTimer.startTimer();

				ATMSession atmSession = verifyLogin(atm);
				// Valid PIN Provided, Session Access Granted
				if (atmSession != null) {

					try {
						boolean sessionActive = true;
						TransactionHandler transHandler = new TransactionHandler(db, accountData, atmData, 
								atmSession, sessionTimer, dataInput, dataOutput, debitCard, atm);
						
						while (sessionActive) {
							Message sessionAction = (Message)dataInput.readObject();
							if (!sessionTimer.getSessionThreadActive()) {
								System.out.println(Thread.currentThread() + ": Timer triggered");
								throw new InterruptedException();
							}
							// Resets Timer
							sessionTimer.refreshTimer();

							System.out.println(Thread.currentThread() + ": Executing Action: " + sessionAction.flag());
							switch (sessionAction.flag()) {

							case 0:// Logout Request (Cancel Button)
								sessionActive = false;
								break;

							case 9:// Initiate Withdrawal Transaction
								transHandler.initiateWithdrawal();
								break;

							case 10:// Initiate Deposit Transaction
								transHandler.initiateDeposit();
								break;

							case 11:// Initiate Transfer Transaction
								transHandler.initiateTransfer();
								break;

							case 14:// View Balance (Account Inquiry)
								transHandler.initiateBalanceInquiry();
								break;
								
							default:// Communication Error
								Message unexpectedRequest = new Message(7);
								unexpectedRequest.addStringM("Expected Transaction Request. Received Unexpected Request Type.");
								dataOutput.writeObject(unexpectedRequest);
								sessionActive = false;
								break;
							}// end switch
						} // end while
							// Terminates Session on User Cancel (Cancel Button)
						sessionData.terminateSession(atmSession.getSessionID());
						
						System.out.println(Thread.currentThread() + ": ATM Session Closed");

					} catch (Exception e) {
						// Terminates Session on Session Timeout
						System.out.println(Thread.currentThread() + ": Session Timed Out");
						sessionData.terminateSession(atmSession.getSessionID());
						Message sessionTimedOut = new Message(1);
						dataOutput.writeObject(sessionTimedOut);
					} // end if(verifyLogin())
				} // end if(accessGranted)
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		try {
			dataOutput.close();
			System.out.println(Thread.currentThread() + ": Terminated Socket");
		}
		catch (IOException e) {
			
		}
	}// end run

	public ATMSession verifyLogin(ATM atm) throws InterruptedException {
		try {
			boolean verificationComplete = false;
			while (!verificationComplete) {
				Message m = (Message)dataInput.readObject();
				if (!sessionTimer.getSessionThreadActive()) 
				{
					throw new InterruptedException();
				}
				sessionTimer.refreshTimer();
				if (m.flag() == 2) {// Login Request
	
					System.out.println(Thread.currentThread() + ": Recieved Card Details");
					long cardNumber = m.getCardNumber();
					int pin = m.getIntegerMessages().get(0);
					int pinAttemptCount = 1;
	
					debitCardData = new DebitCardDA(db);
					debitCard = debitCardData.getDebitCardInfo(cardNumber);
	
					// Checks to see if debit card with provided card number exists
					if (debitCard == null) {
						Message cardDNE = new Message(3);
						cardDNE.addStringM("Entered Card Number Not Found.");
						dataOutput.writeObject(cardDNE);
						System.out.println(Thread.currentThread() + ": Card number not found, try new Card");
					}
					// Card exists but has been previously locked
					else if (debitCard.isLocked()) {
						Message cardLocked = new Message(4);
						cardLocked.addStringM("Card is Locked. Access Denied.");
						dataOutput.writeObject(cardLocked);
						System.out.println(Thread.currentThread() + ": Card is Locked, closing session");
						verificationComplete = true;
					}
					//Card Expiration Date has passed (Expiration is before current time of access)
					else if (debitCard.getCardExpDate().isBefore(LocalDateTime.now())) {
						Message cardExpired = new Message(23);
						cardExpired.addStringM("Card is Expired. Access Denied.");
						dataOutput.writeObject(cardExpired);
						System.out.println(Thread.currentThread() + ": Card is Expired, try new Card");
					}
					// Card Number does exist in Database and is not locked
					else {
						while (pinAttemptCount < atm.getMaxPinEntryAttempts()) {
							System.out.println(Thread.currentThread() + ": Pin verification attempt: " + pinAttemptCount);
							/*
							 * Main Menu Info Message Consists of the following: 
							 * 1. ATM's Physical Location(Address) 
							 * 2. Card Holder Name (for a welcome message)
							 * 3. Timer Limit to time out session (milliseconds)
							 */
							if (debitCard.getPinNumber() == pin) {
								Message mainMenuInfo = new Message(5);
								mainMenuInfo.addStringM(atm.getMachineAddress());
								mainMenuInfo.addStringM(debitCard.getCardHolderName());
								mainMenuInfo.addIntegerM(atm.getSessionTimeOut());
								dataOutput.writeObject(mainMenuInfo);
	
								// Creates Session for Valid User Login and returns it to run()
								int sessionID = sessionData.insertATMSession(atm.getMachineID(), cardNumber);
								ATMSession atmSession = sessionData.getATMSessionInfo(sessionID);
								System.out.println(Thread.currentThread() + ": Pin correct, starting session");
								return atmSession;
							} else {
								Message invalidPin = new Message(24);
								if (!sessionTimer.getSessionThreadActive())
									throw new InterruptedException();
								sessionTimer.refreshTimer();
								invalidPin.addStringM("Invalid Pin Entered. Try Again.");
								dataOutput.writeObject(invalidPin);
								pinAttemptCount += 1;
								System.out.println(Thread.currentThread() + ": Invalid pin entered");
								m = (Message) dataInput.readObject();
								pin = m.getIntegerMessages().get(0);
							}
						} // end while
							// This code is reached if user ran out of attempts
						debitCardData.lockDebitCard(cardNumber);
						Message cardLocked = new Message(4);
						cardLocked.addStringM("Maximum Attempts Reached. Card is now locked.");
						dataOutput.writeObject(cardLocked);
						System.out.println(Thread.currentThread() + ": Maximum attempts reached, card is now locked, terminating Session");
					}
				} else if(m.flag() == 0) {
					System.out.println(Thread.currentThread() + ": Client requested backing out to Map");
					verificationComplete = true;
				}
				else {// Communication Error
					Message unexpectedRequest = new Message(7);
					unexpectedRequest.addStringM("Expected Login Request. " + "Received Unexpected Request Type.");
					dataOutput.writeObject(unexpectedRequest);
					System.out.println(Thread.currentThread() + ": Unexpected message recieved, dropping client");
					verificationComplete = true;
				}
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}// end verifyLogin

	public ATM requestATMAccess() {
		try {
			Message m = (Message) dataInput.readObject();
			if (m.flag() == 6) {// ATM Access Request
				System.out.println(Thread.currentThread() + ": Received msgATMRequest");
				try {
					atmData = new ATMDA(db);
					ATM atm = atmData.getATMInfo(m.getIntegerMessages().get(0));
					System.out.println(Thread.currentThread() + ": Requested ATM: " + atm.getMachineID());
					// Restrict access to ATM if it is already in use
					if (atm.isSessionActive()) {
						Message accessDenied = new Message(8);
						accessDenied.addStringM("Access Denied. ATM is currently in use.");
						dataOutput.writeObject(accessDenied);
						System.out.println(Thread.currentThread() + ": ATM Denied");
					} else {// Grants access to atm (meaning the given ATM is not currently in use)
						Message accessDenied = new Message(22);
						accessDenied.addStringM(atm.getMachineAddress());
						dataOutput.writeObject(accessDenied);
						System.out.println(Thread.currentThread() + ": ATM Allocated");
						return atm;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {// Communication Error
				Message unexpectedRequest = new Message(7);
				unexpectedRequest.addStringM("Expected ATM Access Request. " + "Received Unexpected Request Type.");
				dataOutput.writeObject(unexpectedRequest);
				System.out.println(Thread.currentThread() + ": Unexpected Message recieved " + unexpectedRequest.flag());
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}// end requestATMAccess

}// end SessionHandler