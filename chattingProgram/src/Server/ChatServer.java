package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import db.PersonTable;
// Login�� �н����� Ȯ���Ҷ�  db���� �ٷ� Ȯ�ξ��ϰ�  �޸𸮿��ִ°ɷ� Ȯ���ϴ� ����?..
public class ChatServer {
	public PersonTable db;
	HashMap client_oos;
	HashMap client_ois;
	static ArrayList<String> pPacketList;
	
	public void serverLoop()
	{
		ServerSocket serverSocket = null;
		Socket socket = null;
		db = new PersonTable();	
		client_oos = new HashMap();
		client_ois = new HashMap();
		Collections.synchronizedMap(client_oos);
		Collections.synchronizedMap(client_ois);
		try
		{
			serverSocket = new ServerSocket(8000);
			while(true)
			{
				socket = serverSocket.accept();
			//	System.out.println("[" + socket.getInetAddress() + "]"
			//			+  " " + socket.getPort() + "���� �����߽��ϴ�.");
				ClientThreadManager thread = new ClientThreadManager(socket,db,client_oos,client_ois);
				
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
