package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;

import db.PersonTable;

public class ChatServer {
	public PersonTable db;
	HashMap clients;
	public void serverLoop()
	{
		ServerSocket serverSocket = null;
		Socket socket = null;
		db = new PersonTable();	
		clients = new HashMap();
		Collections.synchronizedMap(clients);
		try
		{
			serverSocket = new ServerSocket(8000);
			while(true)
			{
				socket = serverSocket.accept();
			//	System.out.println("[" + socket.getInetAddress() + "]"
			//			+  " " + socket.getPort() + "에서 접속했습니다.");
				ClientThreadManager thread = new ClientThreadManager(socket,db,clients);
				thread.start();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ChatServer().serverLoop();
	}
	
	
}
