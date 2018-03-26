package utility;


import java.awt.event.WindowAdapter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import loginMenu.LoginWindow;
import loginMenu.RegContent;
import mainMenu.EditContent;
import mainMenu.FriendTab;
import mainMenu.MainMenu;
import mainMenu.SettingTab;
import mainMenu.TalkWindow;

enum EnumPerson {
	id, password, emp_no, name, age, tel
};

final class Packet {
	public static final char addFriend = 'A';
	public static final char getUserInfo = 'B';
	public static final char getFriendInfo = 'C';
	public static final char joinMember = 'D';
	public static final char editProfile = 'E';
	public static final char sendFile = 'F';
	public static final char sendChatMessage_1TON = 'G';
	public static final char login = 'L';
	public static final char sendChatMessage_1TO1 = 'M';
	public static final char searchMember='S';
}

class ChattingRoom{
	ArrayList<String> talkingPeopleName;
	int chatRoomNumber;
	public ArrayList<String> getTalkingPeopleName() {
		return talkingPeopleName;
	}
	public void setTalkingPeopleName(ArrayList<String> talkingPeopleName) {
		this.talkingPeopleName = talkingPeopleName;
	}
	public int getChatRoomNumber() {
		return chatRoomNumber;
	}
	public void setChatRoomNumber(int chatRoomNumber) {
		this.chatRoomNumber = chatRoomNumber;
	}
}
public class NetworkLib extends Thread { // 받은패킷을 ArrayList에 저장한다!! 그리고 mainMenu에서는 networkLib에서의 최근받은 패킷을 가져온다!..

	// private TalkingListener talkingListener;
	
	public String serverIp = "123.214.12.123";// "192.168.25.2";//"10.0.27.215";
	public Socket socket;
	public DataOutputStream out;
	public DataInputStream in;
	public String loginID;
	List<Thread> threadArr = new ArrayList<>();
	String content;
	// HashMap<String,chatInfo> chatMessageInfo;
	HashMap<String, TalkWindow> talkingMap = new HashMap();
	HashMap<Integer,TalkWindow> chattingRoom = new HashMap();
	int chattingRoomNumber=0;
	
	LoginWindow loginWindow;
	MainMenu mainMenu = null;
	boolean mPacketArrived = false;
	volatile boolean loginFailed=false;
	
	@SuppressWarnings("unchecked")
	public void includeLoginWindow(LoginWindow loginWindow) {
		this.loginWindow = loginWindow;
	}
	public HashMap<Integer,TalkWindow> getChattingRoomHashMap(){
		return chattingRoom;
	}
	void dispatchContent(String packet) {
		char packetType = packet.charAt(0);
		String content = null;// String content 내용 패턴: "sender.chatMessage"
		content = packet.substring(1, packet.length());
		TalkWindow talkWindow;
		boolean idFound;
		
		switch (packetType) {
		case Packet.sendChatMessage_1TO1:
			deliver1to1MessageToTalkWindow(content);
			break;
		case Packet.sendChatMessage_1TON:
			talkWindow = deliver1toNMessageToTalkWindow(content);
			mainMenu.getChattingTab().addChatRoomToList(talkWindow);
			break;
		case Packet.addFriend:
			String[] friendInfoTuple = new String[6];
			idFound = checkIfIDFound(content, friendInfoTuple);
			if (idFound == true)
				mainMenu.getFriendTab().addFriendToList(friendInfoTuple);
			else
				JOptionPane.showConfirmDialog(mainMenu, "검색하신 ID는 등록되지 않은 ID입니다.");
			break;
		case Packet.searchMember:
			String[] searchMemberInfo = new String[6];
			idFound = checkIfIDFound(content, searchMemberInfo);
			if (idFound == true)
				mainMenu.getFriendTab().popUpFriendInfo(searchMemberInfo);
			else
				JOptionPane.showConfirmDialog(mainMenu, "검색하신 ID는 등록되지 않은 ID입니다.");
			break;
		case Packet.getUserInfo:
			loadMyInfoFromServer(content, mainMenu.getSettingTab());
			break;
		case Packet.getFriendInfo:
			try {
				loadFriendInfoFromServer(content, mainMenu.getFriendTab());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("* Case C :: Packet.getFriendInfo");
				e.printStackTrace();
			}
			break;
		case Packet.joinMember:
			break;
		case Packet.sendFile:
			// recvFile();
			break;
		case Packet.editProfile:
			break;
		case Packet.login:
			loginFailed = LoginFailed(content);
			if (!loginFailed)
				GetFriendList();
			else
			//System.out.println("Login Failed.");
			
			break;
		}
	}
	
	boolean checkIfIDFound(String content, String[] friendInfoTuple) {
		StringTokenizer friendContent = new StringTokenizer(content, ".");
		String attribute = null;
		boolean idFound = true;
		for (int i = 0; i < 6; i += 1) {
			if (i == 1)// passwordField skip
				continue;
			try {
				friendInfoTuple[i] = friendContent.nextToken();

			} catch (NoSuchElementException e) {
				idFound = false;
				break;
			}

		}
		return idFound;
	}

	int findDotIdx(String content, int dot_seq) {
		int dot_cnt = 0;
		for (int i = 0; i < content.length(); i += 1) {
			if (content.charAt(i) == '.') {
				dot_cnt += 1;
				if (dot_cnt == dot_seq)
					return i;
			}
		}
		return -1;
	}
	TalkWindow deliver1toNMessageToTalkWindow(String content)
	{
		StringTokenizer tokenizer = new StringTokenizer(content, "#");
		String chattingRoomNumber = tokenizer.nextToken();
		String from = tokenizer.nextToken();
		int startIdx = findDotIdx(content, 1);
		String message = content.substring(startIdx + 1, content.length());
		String token;
		//mPacketArrived = true;
		TalkWindow talkWindow;
		talkWindow = chattingRoom.get(Integer.valueOf(chattingRoomNumber));
		
		if (talkWindow != null) {
			talkWindow.deliverNewMessage(from,message);
		}
		else
		{
			ArrayList<String> roomUserIDList=null;
			
			roomUserIDList = new ArrayList<String>();
			roomUserIDList.add(from);
			while(tokenizer.hasMoreTokens())
			{
				token = tokenizer.nextToken();
				if(token.charAt(0)=='.')
					break;
				roomUserIDList.add(token);
			}
			
			
			talkWindow = new TalkWindow(this, roomUserIDList);
			//public TalkWindow(NetworkLib networkLib, ArrayList<String> talkCompanions)
			chattingRoom.put(Integer.valueOf(chattingRoomNumber), talkWindow);
			
			talkWindow.showLogInvitation(from);
			talkWindow.deliverNewMessage(from,message);
			
		}
		return talkWindow;
	}
	void deliver1to1MessageToTalkWindow(String content) {
		StringTokenizer tokenizer = new StringTokenizer(content, ".");
		String from = tokenizer.nextToken();
		int startIdx = findDotIdx(content, 1);
		String message = content.substring(startIdx + 1, content.length());
		
		mPacketArrived = true;
		TalkWindow talkWindow;
		talkWindow = talkingMap.get(from);
		if (talkWindow == null) {// 생성자 :: TalkWindow(NetworkLib networkLib, String talkCompanion ,
									// HashMap<String,TalkWindow> talkList)
			talkWindow = new TalkWindow(this, from, talkingMap);
			talkingMap.put(from, talkWindow);
			talkWindow.deliverNewMessage(from,message);
		} else
			talkWindow.deliverNewMessage(from,message);
	}

	void recvFile(String content) {
		StringTokenizer tokenizer = new StringTokenizer(content, ".");
		String sender = tokenizer.nextToken();
		String file = tokenizer.nextToken();
		StringBuilder packet = new StringBuilder();
		
		//FileOutputStream = new FileOutputStream()
	}

	public void sendFile(String filePath, String receiverID) throws IOException {
		FileInputStream fileInputStream = null;
		StringBuilder filePacket = new StringBuilder();
		byte[] fileContent;

		filePacket.append("F");
		filePacket.append(loginID + ".");
		filePacket.append(receiverID + ".");
		/*
			File file = new File(filePath);
			fileContent = new byte[(int) file.length()];
			fileInputStream = new FileInputStream(file);

			fileInputStream.read(fileContent);
			filePacket.append(fileContent.toString());
			out.writeUTF(filePacket.toString());*/
		File file = new File(filePath);
		byte buf[] = new byte[(int)file.length()];
		
		try(InputStream in = new FileInputStream(file);) {
	      
	     in.read(buf);
	     in.close();
	  //   filePacket.append(c);    Q:뭔가 더 보내야하나?   A:파일제목!
	     filePacket.append(buf.toString());
	     out.writeUTF(filePacket.toString());
	   }
	   catch(IOException e) {
	      e.printStackTrace();
	   }
	}
			// out.write(fileContent, 0, bytesArray.length); 
	public void addTalkWindow(ArrayList<String> friendIDList, TalkWindow talkWindow)
	{
		chattingRoom.put(chattingRoomNumber,talkWindow);
		chattingRoomNumber+=1;
	}
	public void addTalkWindow(String friendID, TalkWindow talkWindow) {
		talkingMap.put(friendID, talkWindow);
	}
	
	public void run() {
		String id = null;
		String password = null;
		String packet = null;

		while (in != null ) {
			if(loginFailed == true)
				break;
			try {
				packet = in.readUTF();
				dispatchContent(packet);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showInputDialog("서버와의 연결이 끊겼습니다.");
				break;
			}

		}

	}

	public NetworkLib(LoginWindow loginWindow) {
		this();
		this.loginWindow = loginWindow;
	}

	public NetworkLib() {
		try {
			socket = new Socket(serverIp, 8000);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	boolean LoginFailed(String content) {
		if (content.equals("1")) {
			MainMenu main = new MainMenu(this, loginID);
			loginWindow.dispose();

			main.revalidate();
			mainMenu = main;
			return false;
		}
		JOptionPane.showInputDialog("Login failed.\n"); // failed. 0을 받았을때.
		return true;
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

	/*
	 * boolean try_login(String loginID,String password_input) { boolean t = false;
	 * try {
	 * 
	 * //JOptionPane.showInputDialog("here2"); sendLoginPacket(loginID,
	 * (password_input)); // JOptionPane.showInputDialog("here3"); /* String isvalid
	 * = in.readUTF(); // 0 or 1 // JOptionPane.showInputDialog("here4"); if
	 * (isvalid.equals("0")) { // JOptionPane.showInputDialog(isvalid); t = false;
	 * in.close(); out.close(); } else if (isvalid.equals("1")) { t = true; //
	 * JOptionPane.showInputDialog(isvalid); } } catch (UnknownHostException e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); } catch (IOException e)
	 * { // TODO Auto-generated catch block e.printStackTrace(); }
	 * 
	 * return t; }
	 */
	void GetFriendList() {
		StringBuilder message = new StringBuilder();
		message.append("C");
		message.append(loginID);
		// System.out.println(message);
		try {
			out.writeUTF(message.toString());
			// System.out.println("E");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendLoginPacket(String ID, String password) throws IOException { // case L
		StringBuilder LoginPacket = new StringBuilder();
		this.loginID = ID;
		LoginPacket.append("L");
		LoginPacket.append(ID + ".");
		LoginPacket.append(password);
		out.writeUTF(LoginPacket.toString());
	}

	public void AddFriend(String loginID, String friendId) {
	//	EnumPerson passwordField = EnumPerson.valueOf("password");
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
	public void searchMemberInfo(String loginID, String searchID)
	{
		StringBuilder searchPacket = new StringBuilder(); 
			searchPacket.append("S");
		searchPacket = searchPacket.append(searchID);
		searchPacket = searchPacket.append(".");
		searchPacket = searchPacket.append(loginID);

		try {
			out.writeUTF(searchPacket.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendMyIDPacketToGetMyInfo(String loginID) {
		EnumPerson passwordField = EnumPerson.valueOf("password");
		String searchPacket = "B";
		// searchPacket = searchPacket.concat(friendId);
		searchPacket = searchPacket.concat(".");
		searchPacket = searchPacket.concat(loginID);

		try {
			out.writeUTF(searchPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	void getFriendInfo(String id)
	{
		
	}
	void loadFriendInfoFromServer(String content, FriendTab friendTab) throws Exception{

		String friendId = null;
		StringTokenizer friendListTokenizer = new StringTokenizer(content, ".");
		String attribute;
		boolean escape = false;
		String[] record = null;
		String message = null;
		EnumPerson passwordField = EnumPerson.valueOf("password");
		ArrayList<String[]> friendInfo_list = friendTab.getFriendInfoTuple_list();
		ArrayList<String> friendID_list = friendTab.getFriendID_list();
		SortedListModel dlm = friendTab.getSortedListModel();
		
		String ID;
		String no ;
		String name;
		String age ;
		String phoneNum;
		System.out.println("In loadFriendInfoFromServer:: ");
		while(friendListTokenizer.hasMoreTokens())
		{
			record = new String[6];
			ID = friendListTokenizer.nextToken();
			no = friendListTokenizer.nextToken();
			name = friendListTokenizer.nextToken();
			age = friendListTokenizer.nextToken();
			phoneNum = friendListTokenizer.nextToken();
			record = new String[] { ID, null, null, name, age, phoneNum};
 			friendInfo_list.add( record );  
			System.out.println("loadFriendInfoFromServer:: Added ID: " + ID);
		}
		for(String[] rec : friendInfo_list) {

			dlm.add(rec[PersonRecord.ID]);
			friendID_list.add(rec[PersonRecord.ID]);
			//friendInfo_list.add(rec);
		}
		friendTab.setBorderText(friendInfo_list.size());
//		border = BorderFactory.createTitledBorder("친구목록" + "(" + friendInfoList.size() + ")");
//		scroll.setBorder(border); // 경계 설정
		
		/* 수업에서  아래(BOTTOM)와 같이 소스작성하지말고, 위(UP)와 같이 소스 작성하라고 함.
			while (true) {
				record = new String[6];
				for (int i = 0; i < 6; i += 1) {
					if (i == passwordField.ordinal())
						continue;
					try {
						record[i] = friendListTokenizer.nextToken();
					} catch (NoSuchElementException e) {
						escape = true;
						break;
					}
				}
				if (escape == true)
					break;
				dlm.add(record[0]);
				friendID_list.add(record[0]);
				friendInfo_list.add(record);
			}
		mainMenu.getFriendTab().setBorder();*/
	}

	void loadMyInfoFromServer(String content, SettingTab settingTab) {
		StringTokenizer packetTokenizer = new StringTokenizer(content, ".");
		String[] myInfo = new String[6];

		for (int i = 0; i < 6; i += 1) {
			myInfo[i] = packetTokenizer.nextToken();
		}
		fillSettingTabContent(myInfo);
	}

	void fillSettingTabContent(String[] myInfo) {
		JLabel[] myInfoLabel = new JLabel[6];
		SettingTab settingTab = mainMenu.getSettingTab();
		String src;

		String[] prop = new String[]{ "id","name", "age", "phoneNum"};
		int[]  map = new int[] {PersonRecord.ID, PersonRecord.NAME, PersonRecord.AGE, PersonRecord.PHONE_NUM};
		for(int i = 0 ; i < prop.length; ++i)
		{
			JLabel lab = settingTab.getLabel(prop[i]);
			src = lab.getText();
			lab.setText(src + myInfo[map[i]]); 
		}
		
/*		
 * 	   수업시간에   아래(밑의 소스)와 같이 코딩하지말고, 위(위에 소스)와 같이 코딩하라고 지도받음.
 		myInfoLabel[0] = settingTab.getIDLabel();
		src = myInfoLabel[0].getText();
		myInfoLabel[0].setText(src + myInfo[0]);

		myInfoLabel[3] = settingTab.getNameLabel();
		src = myInfoLabel[3].getText();
		myInfoLabel[3].setText(src + myInfo[3]);

		myInfoLabel[4] = settingTab.getAgeLabel();
		src = myInfoLabel[4].getText();
		myInfoLabel[4].setText(src + myInfo[4]);

		myInfoLabel[5] = settingTab.getPhoneNumLabel();
		src = myInfoLabel[5].getText();
		myInfoLabel[5].setText(src + myInfo[5]);*/
	}
	//1:1대화메세지 전송기능
	public String sendChatMessage(String text, String talkCompanion) throws IOException { // sendChatmessage
		StringBuilder packet = new StringBuilder();
		
		packet.append("M");
		packet.append(loginID + ".");
		packet.append(talkCompanion + ".");
		 packet.append(text);
	
		out.writeUTF(packet.toString());
		
		return packet.toString();
	}
	//1:N대화메세지 전송기능
	public String sendChatMessage(String text, int chatRoomNumber,ArrayList<String> talkCompanions) throws IOException{
		StringBuilder packet = new StringBuilder();
		
		packet.append("G");
		packet.append("#");
		packet.append(String.valueOf(chatRoomNumber));
		packet.append("#" + loginID );
		for(int i=0;i<talkCompanions.size();i+=1)
			packet.append("#"+talkCompanions.get(i));
		packet.append("#."+text);
		out.writeUTF(packet.toString());
		return packet.toString();
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
	/*
	 * void openListeningService(String talkCompanion) { Thread t = new Thread(new
	 * TalkingListener(socket),talkCompanion);//new TalkingListener(socket);
	 * threadArr.add(t); t.start(); }
	 */

	public void sendEditProfileInfo(String loginID,EditContent editContent) {
		// TODO Auto-generated method stub
		boolean t = false;
		StringBuilder packet = new StringBuilder();
		packet.append("E");
		try {
			packet.append(loginID+".");
			packet.append(editContent.getPassword() + ".");
			packet.append(editContent.getName() + ".");
			packet.append(editContent.getPhone());
			out.writeUTF(packet.toString());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}

	}

	public void inviteFriends(ArrayList<String> f) {
		// TODO Auto-generated method stub
		
	}
	public void sendFile(File file) {
		// TODO Auto-generated method stub
		
	}

}
