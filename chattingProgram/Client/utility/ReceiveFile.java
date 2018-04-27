package utility;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JOptionPane;

import loginMenu.LoginWindow;
import mainMenu.MainMenu;

public class ReceiveFile extends Thread{
	int port=0;
	ServerSocket serverSocket;
	Socket sock = null;
	FileThread client = null;
	String ip;
	static Vector<Socket> socketVector = new Vector<>();
	static Vector<FileThread> fileThreadVector = new Vector<>();
	DataOutputStream out = null;
	DataInputStream in = null;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	MainMenu mainMenu;
	String senderID;
	void sendPortPacket(int port) {
		StringBuilder s = new StringBuilder();
		s.append("P");
		s.append(senderID+"#");
		s.append(LoginWindow.getLoginID()+"#");
		s.append(ip + "#");
		s.append(String.valueOf(port));
		
		try {
		//	JOptionPane.showInputDialog("In ReceiveFile: " + s.toString());
			oos.writeObject(s.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public ReceiveFile(String senderID,MainMenu mainMenu,ObjectOutputStream oos, ObjectInputStream ois) {
		this.senderID = senderID;
		this.oos = oos;
		this.ois = ois;
		this.mainMenu = mainMenu;
		try {
			serverSocket = new ServerSocket(0);
			ip = InetAddress.getLocalHost().getHostAddress();
			port = serverSocket.getLocalPort();
		//	System.out.println("serverSocket: " +serverSocket.getInetAddress().getHostAddress());
			sock = null;
			client = null;
			
			sendPortPacket(port);
//			try {
//sleep			//	sleep(50);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
				sock = serverSocket.accept();
			//	out = new DataOutputStream(sock.getOutputStream());
			//	in = new DataInputStream(sock.getInputStream());
				
				client = new FileThread(mainMenu,this, sock);
				client.start();
				
				
				socketVector.add(sock);
				fileThreadVector.add(client);
				
		//		JOptionPane.showInputDialog("ReceiveFile() called // FileThread started.");
			}
			catch(IOException e) {
				e.printStackTrace();
				try {
					if(sock!=null) sock.close();
				}catch(IOException e1)
				{e1.printStackTrace();}
				finally {sock = null;}
				
				try {
					if(!serverSocket.isClosed())
						serverSocket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} 
	}
	

