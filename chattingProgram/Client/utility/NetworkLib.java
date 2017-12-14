package utility;

import java.awt.event.WindowAdapter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import loginMenu.LoginWindow;
import loginMenu.RegContent;
import mainMenu.FriendTab;
import mainMenu.MainMenu;
import mainMenu.SettingTab;
import mainMenu.TalkWindow;
enum EnumPerson{id,password,emp_no,name,age,tel};


final class Packet {
	static final char addFriend = 'A';
	static final char getUserInfo = 'B';
	static final char getFriendInfo = 'C';
	static final char joinMember = 'D';
	static final char sendFile = 'F';
	static final char login = 'L';
	static final char sendChatMessage = 'M';
}
public  class NetworkLib extends Thread{
	
	//private TalkingListener talkingListener;
	public String serverIp =  "192.168.0.6";//"192.168.25.2";//"10.0.27.215";
	public Socket socket;
	public DataOutputStream out;
	public DataInputStream in;
	public String loginID;
	List<Thread> threadArr = new ArrayList<>();
	String content;
//	HashMap<String,chatInfo> chatMessageInfo;
	HashMap<String,TalkWindow> talkingMap = new HashMap();
	LoginWindow loginWindow;
	MainMenu mainMenu=null;
	@SuppressWarnings("unchecked")
	public void includeLoginWindow(LoginWindow loginWindow)
	{
		this.loginWindow = loginWindow;
	}
	void dispatchContent(String packet)
	 {
		 char packetType = packet.charAt(0);
		 String content=null;// String content 내용 패턴: "sender.chatMessage"
		 content = packet.substring(1, packet.length());

		 switch(packetType)
		 {
		 case Packet.sendChatMessage:
			 deliverMessageToTalkWindow(content);
			 break;
		 case Packet.addFriend:
			 String[] friendInfoTuple = new String[6];
			 boolean idFound = checkIfIDFound(content, friendInfoTuple);
			 if(idFound == true)
				 mainMenu.getFriendTab().addFriendToList(friendInfoTuple);
			 else
				 JOptionPane.showConfirmDialog(mainMenu, "검색하신 ID는 등록되지 않은 ID입니다.");
			 break;
		 case Packet.getUserInfo:
			 loadMyInfoFromServer(content, mainMenu.getSettingTab());
			 break;
		 case Packet.getFriendInfo:
			 loadFriendInfoFromServer(content,mainMenu.getFriendTab());
			 break;
		 case Packet.joinMember:
			 break;
		 case Packet.sendFile:
			// recvFile();
			 break;
		 case Packet.login:
			 boolean loginSuccess = LoginResult(content);
			 if(loginSuccess)sendLoginIDToGetFriendList();
			 break;
		 }
	 }
	boolean checkIfIDFound(String content, String[] friendInfoTuple)
	{
		StringTokenizer friendContent = new StringTokenizer(content, ".");
		String attribute=null;
		boolean idFound = true;
		for(int i=0;i<6;i+=1)
		{
			if(i==1)//passwordField skip
				continue;
			try {
				friendInfoTuple[i] = friendContent.nextToken();
				
			}
			catch(NoSuchElementException e)
			{
				idFound = false;
				break;
			}
			
		}
		return idFound;
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
	void deliverMessageToTalkWindow(String content)
	{
		StringTokenizer tokenizer = new StringTokenizer(content, ".");
		String talkTo = tokenizer.nextToken();
		int startIdx = findDotIdx(content,1);
		String message = content.substring(startIdx+1, content.length());
		TalkWindow talkWindow;
		talkWindow = talkingMap.get(talkTo);
		if(talkWindow==null)
		{
			
		}
		else
			talkWindow.deliverNewMessage(message);
	}
	void recvFile(String packet)
	{
		
	}
	public void sendFile(String filePath, String receiverID)
	{
		 FileInputStream fileInputStream = null;
	        StringBuilder filePacket = new StringBuilder();
	        byte[] fileContent;
	        
	        filePacket.append("F");
            filePacket.append(loginID+".");
            filePacket.append(receiverID + ".");
            
	        try {
	            File file = new File(filePath);    
	            fileContent = new byte[(int)file.length()];	
	            fileInputStream = new FileInputStream(file);
	            
	            fileInputStream.read(fileContent);
	            filePacket.append(fileContent.toString());
	            out.writeUTF(filePacket.toString());
	   //         out.write(fileContent, 0, bytesArray.length);
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (fileInputStream != null) {
	                try {
	                    fileInputStream.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }

	        }

	}
	
	public void addTalkWindow(String friendID,TalkWindow talkWindow)
	{
		talkingMap.put(friendID, talkWindow);
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
							e.printStackTrace();
							JOptionPane.showInputDialog("서버와의 연결이 끊겼습니다.");
							break;
						}
		
						dispatchContent(packet);
					}
			}
			catch(Exception e) { e.printStackTrace();}
			finally {
				
			
			}
			
		}
		public NetworkLib(LoginWindow loginWindow)
		{
			this();
			this.loginWindow = loginWindow;
		}
	public NetworkLib()
	{
		try
		{
			socket = new Socket(serverIp, 8000);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			}catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	boolean LoginResult(String content)
	{
		if(content.equals("1"))
		{
			MainMenu main = new MainMenu(this, loginID);
			loginWindow.dispose();
			
			main.revalidate();
			mainMenu = main;
			return true;
		}
		JOptionPane.showInputDialog("Login failed.\n"); // failed.  0을 받았을때.
		return false;
	}
	
	public WindowAdapter getAdapter() {
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
/*	boolean try_login(String  loginID,String password_input) {
		boolean t = false;
		try {
			
		//JOptionPane.showInputDialog("here2");
			sendLoginPacket(loginID, (password_input));
	//	JOptionPane.showInputDialog("here3");
	/*		String isvalid = in.readUTF(); // 0 or 1
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
	}*/
	void sendLoginIDToGetFriendList()
	{
		StringBuilder message = new StringBuilder();
		message.append("C");
		message.append(loginID);
	//	System.out.println(message);
		try {
			out.writeUTF(message.toString());
		//	System.out.println("E");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendLoginPacket(String ID, String password) throws IOException { //case L
		StringBuilder LoginPacket = new StringBuilder();
		this.loginID = ID;
		LoginPacket.append("L");
		LoginPacket.append(ID+".");
		LoginPacket.append(password);
		out.writeUTF(LoginPacket.toString());
	}
	public void sendAddFriendPacketToServer(String loginID,String friendId) {
		EnumPerson passwordField =  EnumPerson.valueOf("password");
		String searchPacket = "A";
		searchPacket = searchPacket.concat(friendId);
		searchPacket = searchPacket.concat(".");
		searchPacket = searchPacket.concat(loginID);

		try {
			out.writeUTF(searchPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	public void sendMyIDPacketToGetMyInfo(String loginID) {
		EnumPerson passwordField =  EnumPerson.valueOf("password");
		String searchPacket = "B";
		//searchPacket = searchPacket.concat(friendId);
		searchPacket = searchPacket.concat(".");
		searchPacket = searchPacket.concat(loginID);
		
		try {
		out.writeUTF(searchPacket);
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} 
	}
	
	void loadFriendInfoFromServer(String content, FriendTab fPanel//ArrayList<String[]> friendInfo_list, DefaultListModel dlm,
											) {
		
		String friendId = null;
		StringTokenizer friendListTokenizer = new StringTokenizer(content, ".");
		String attribute;
		boolean escape=false;
		String[] tuple=null;
		String message = null;
		EnumPerson passwordField =  EnumPerson.valueOf("password");
		ArrayList<String[]> friendInfo_list = fPanel.getFriendInfoTuple_list();
		ArrayList<String> friendID_list = fPanel.getFriendID_list();
		SortedListModel dlm = fPanel.getSortedListModel();
		
		try {
				while(true)
				{
					tuple = new String[6];
					for(int i=0;i<6;i+=1)
					{
						if(i==passwordField.ordinal())
							continue;
						try {
						tuple[i] = friendListTokenizer.nextToken();
						}
						catch(NoSuchElementException e)
						{
							escape= true;
							break;
						}
					}
					if(escape == true)
						break;
					dlm.add(tuple[0]);
					friendID_list.add(tuple[0]);
					friendInfo_list.add(tuple);
				}
			}
		 catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mainMenu.getFriendTab().setBorder();
	}
	void loadMyInfoFromServer(String content, SettingTab settingTab)
	{
		StringTokenizer packetTokenizer = new StringTokenizer(content, ".");
		String[] myInfo = new String[6];
		
		for(int i=0;i<6;i+=1)
		{
			myInfo[i] = packetTokenizer.nextToken();
		}
		fillSettingTabContent(myInfo);
	}
	void fillSettingTabContent(String[] myInfo)
	{
		JLabel[] myInfoLabel = new JLabel[6];
		SettingTab settingTab = mainMenu.getSettingTab();
		String src;
		
		myInfoLabel[3] = settingTab.getNameLabel();
		src = myInfoLabel[3].getText();
		myInfoLabel[3].setText(src + myInfo[3]);
		
		myInfoLabel[4] = settingTab.getAgeLabel();
		src = myInfoLabel[4].getText();
		myInfoLabel[4].setText(src + myInfo[4]);
		
		myInfoLabel[5] = settingTab.getPhoneNumLabel();
		src = myInfoLabel[5].getText();
		myInfoLabel[5].setText(src + myInfo[5]);
	}
	public void sendChatMessage(String text, String talkCompanion)
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
	public boolean sendJoinedMemberInfo(RegContent regContent) {
		boolean t = false;
		StringBuilder packet = new StringBuilder();
		packet.append("D");
		try {
			packet.append(regContent.getRegID() + ".");
			packet.append(regContent.getRegPassword() + ".");
			packet.append(regContent.getRegName() + ".");
			packet.append(regContent.getRegAge() + ".");
			packet.append(regContent.getRegPhone() + ".");
			out.writeUTF(packet.toString());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}

		return t;
	}
/*	void openListeningService(String talkCompanion)
	{
		Thread t  = new Thread(new TalkingListener(socket),talkCompanion);//new TalkingListener(socket);
		threadArr.add(t);
		t.start();
	}*/
	
}
