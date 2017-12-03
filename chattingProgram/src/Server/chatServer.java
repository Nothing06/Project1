package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class chatServer {
	public DB_Person db;
	
	public void serverLoop()
	{
		ServerSocket serverSocket = null;
		Socket socket = null;
		db = new DB_Person();
		try
		{
			serverSocket = new ServerSocket(8000);
			while(true)
			{
				socket = serverSocket.accept();
			//	System.out.println("[" + socket.getInetAddress() + "]"
			//			+  " " + socket.getPort() + "에서 접속했습니다.");
				ClientThreadManager thread = new ClientThreadManager(socket,db);
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
		new chatServer().serverLoop();
	}
	
	
}
