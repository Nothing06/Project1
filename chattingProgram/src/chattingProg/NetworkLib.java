package chattingProg;

import java.awt.event.WindowAdapter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
enum EnumPerson{id,password,emp_no,name,age,tel};
interface talkListener{
	boolean listenAndPrint();
}
/*
class TalkingListener implements Runnable{
	public DataOutputStream out;
	public DataInputStream in;
	String packet=null;
	
	
	TalkingListener(Socket socket)
	{
		//this.out = out;
		//this.in = in;
		try {
			this.out = new DataOutputStream(socket.getOutputStream());
			this.in = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(in!=null)
		{
			try {
				packet = in.readUTF();
				receive(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	int findDotIdx(String content, int dot_seq)
	{
		int dot_cnt=0;
		for(int i=0;i<content.length();i+=1)
		{
			if(content.charAt(i)=='.')
			{
				dot_cnt+=1;
				if(dot_cnt== dot_seq)
					return i;
			}
		}
		return -1;
	}
	public void receive(String packet)
	{
		StringTokenizer tokenizer = new StringTokenizer(packet,".");
		String from = tokenizer.nextToken();
		String me = tokenizer.nextToken();
		int	messageFirstIdx = findDotIdx(packet, 2)+1;
		String message = packet.substring(messageFirstIdx, packet.length());
		
		
	}
}*/
class chatInfo
{
	ArrayList<String> chatMessages = new ArrayList<String>();
	int recv_cnt;
	
}
public  class NetworkLib{
	
	//private TalkingListener talkingListener;
	public String serverIp =  "192.168.25.2";//"10.0.27.215";
	public Socket socket;
	public DataOutputStream out;
	public DataInputStream in;
	public String loginID;
	List<Thread> threadArr = new ArrayList<>();
	String content;
	HashMap<String,chatInfo> chatMessageInfo;
	
	@SuppressWarnings("unchecked")
	void extractContent(String packet)
	 {
		 char packetType = packet.charAt(0);
		
		 String content=null;// String content 내용 패턴: "sender.chatMessage"
		 content = packet.substring(1, packet.length());
		 StringTokenizer st = new StringTokenizer(content, ".");
		 String friendID = st.nextToken();
		 String message = st.nextToken();
		 
		
		
	 }
	void savePacketToList(String packet)
	{
		
	}
	 class clientReceiver extends Thread{
		 @SuppressWarnings("unchecked")
		clientReceiver(Socket socket)
		 {
			 chatMessageInfo = new HashMap();
			 Collections.synchronizedMap(chatMessageInfo);
				try {
					in = new DataInputStream(socket.getInputStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		 }
		
		public void run()
		{
			String id=null;
			String password=null;
			String packet =null;
			
			try {
					while(in!=null)
					{
						try {
						packet = in.readUTF();
						}
						catch(SocketException e)
						{
							System.out.println("클라이언트와의 연결을 종료합니다.");
							break;
						}
						
					//	System.out.println("Message: " + message);
						extractContent(packet);
					}
			}
			catch(Exception e) { e.printStackTrace();}
			finally {
				
			
			}
			
		}
	}
	/*class clientSender extends Thread{
		clientSender(Socket socket)
		{
			try {
				out = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		public void run()
		{
			String id=null;
			String password=null;
			String message=null;
			
			try {
					while(out!=null)
					{
						try {
						message = out.writeUTF();
						}
						catch(SocketException e)
						{
							System.out.println("클라이언트와의 연결을 종료합니다.");
							break;
						}
						
					//	System.out.println("Message: " + message);
						decodePacket(message);
					}
			}
			catch(Exception e) { e.printStackTrace();}
			finally {
				
			
			}
			
		}
	}*/
	public NetworkLib(String loginID)
	{
		try
		{
			socket = new Socket(serverIp, 8000);
			out = new DataOutputStream(socket.getOutputStream());
//			in = new DataInputStream(socket.getInputStream());
			
			clientReceiver recvThread = new clientReceiver(socket);
			
		//	clientSender sendThread = new clientSender(socket);
			}catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		WindowAdapter getAdapter() {
		return new java.awt.event.WindowAdapter() {
			public void windowClosed(java.awt.event.WindowEvent evt) {
				try {
					out.writeUTF("Q");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					in.close();
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		};
	}
	void loginProcess(JFrame loginWindow,  JTextField loginID_input, JPasswordField password_input) {
		boolean is_valid = try_login(loginID_input,password_input);
		//JOptionPane.showInputDialog("here1");
		if (is_valid == true) {
			loginWindow.dispose();
		
			mainMenu fl = new mainMenu(this, loginID);
		
		} else {
			JOptionPane.showInputDialog("Login failed.\n");
		}
	}
	boolean try_login(JTextField loginID_input, JPasswordField password_input) {
		boolean t = false;
		try {
			
			loginID = loginID_input.getText();
			if (loginID.equals("")) {
				JOptionPane.showInputDialog("아이디를 입력해주세요");
				return false;
			} 
		//JOptionPane.showInputDialog("here2");
			sendLoginPacket(loginID, new String(password_input.getPassword()));
	//	JOptionPane.showInputDialog("here3");
			String isvalid = in.readUTF(); // 0 or 1
		//	JOptionPane.showInputDialog("here4");
			if (isvalid.equals("0")) {
				// JOptionPane.showInputDialog(isvalid);
				t = false;
				in.close();
				out.close();
			} else if (isvalid.equals("1")) {
				t = true;
				// JOptionPane.showInputDialog(isvalid);
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return t;
	}
	public void sendLoginPacket(String ID, String password) throws IOException {
		String LoginPacket = "L";
		out.writeUTF("L");
		out.writeUTF(ID);
		out.writeUTF(password);
//		out.writeUTF("L");
//		out.writeUTF(ID);
//		out.writeUTF(new String(password.getPassword()));
	}
	public boolean getClientInfoFromServer(String loginID,String friendId,String[] tuple
										) throws IOException {

		boolean clientExist = true;
		EnumPerson passwordField =  EnumPerson.valueOf("password");
		String searchPacket = "A";
		searchPacket = searchPacket.concat(friendId);
		searchPacket = searchPacket.concat(".");
		searchPacket = searchPacket.concat(loginID);

		out.writeUTF(searchPacket); 
		
		for (int j = 0; j < 6; j += 1) {
			if(j==(int)(passwordField.ordinal())) continue; // 
			String searchIdInfo_attribute = in.readUTF();
			
			if (searchIdInfo_attribute.equals(".")) {
				clientExist = false;
			}
			else
			{
				tuple[j] = searchIdInfo_attribute;
			}
		}
		
		return clientExist;
	}
	boolean loadfriendInfoFromServer(ArrayList<String[]> friendInfo_list, DefaultListModel dlm) {
		boolean t = false;
		String friendId = null;
		String attribute;
		String[] tuple;
		String message = null;
		EnumPerson passwordField =  EnumPerson.valueOf("password");
		JOptionPane.showInputDialog("H0");
		try {
			message = "C";
			message = message.concat(loginID);
			out.writeUTF(message);
			JOptionPane.showInputDialog("H0");
			while (true) {
				//friendId = in.readUTF();
				tuple = new String[6];
				for(int j=0;j<6;j+=1)
				{
					if(j==passwordField.ordinal())
						continue;
				//	Scanner s = new Scanner(System.in);
				//	s.nextLine();
					tuple[j] = in.readUTF();
					
					if(tuple[j].equals("#"))
					{
						t=true;
						break;
					}
					if(j==0)
					JOptionPane.showInputDialog(tuple[j]);
				//	JOptionPane.showInputDialog("H1");
				}
				if(t==true)
					break;
				dlm.addElement(tuple[0]);
				friendInfo_list.add(tuple);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t;
	}
	
	void sendChatMessage(String text, String talkCompanion)
	{
		String packet = "M";
		packet = packet.concat(loginID+".");
		packet = packet.concat(talkCompanion+".");
		packet = packet.concat(text);
		try {
			out.writeUTF(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
/*	void openListeningService(String talkCompanion)
	{
		Thread t  = new Thread(new TalkingListener(socket),talkCompanion);//new TalkingListener(socket);
		threadArr.add(t);
		t.start();
	}*/
	
}
