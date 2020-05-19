package com.group7.pandaatm.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.group7.pandaatm.controllers.SessionHandler;
import com.group7.pandaatm.database.Database;

public class Server {
	
	private ServerSocket mainSocket;
	private Database db;
	
	public Server(Database db, int port) {
		this.db = db;
		try {
			mainSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(30);
		}
	}//end Server
	
	public void runServer() {
		System.out.println("Server Started");
		try {
			while(true) {
				Socket clientConnection = mainSocket.accept();
				Thread clientWorker = new Thread(new SessionHandler(db, clientConnection));
				clientWorker.start();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Server s = new Server(new Database(), 6924);
		s.runServer();
	}
}//end Class Server
