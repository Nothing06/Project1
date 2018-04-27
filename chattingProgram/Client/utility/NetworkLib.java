package utility;

import java.awt.event.WindowAdapter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
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
	public static final char sendReqIP = 'I';
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
	public ReceiveFile rf=null;
	public String serverIp = "10.0.31.183";//"123.214.12.123"// "192.168.25.2";//"10.0.27.215";
	public Socket socket;
	public DataOutputStream out;
	public DataInputStream in;
	public ObjectOutputStream oos;
	public ObjectInputStream ois;
	public String loginID;
	File file=null;
	List<Thread> threadArr = new ArrayList<>();
	String content;
	// HashMap<String,chatInfo> chatMessageInfo;
	HashMap<String, TalkWindow> talkingMap = new HashMap();
	HashMap<Integer,TalkWindow> chattingRoom = new HashMap();
	int chattingRoomNumber=0;
	ArrayList<String> ipList = new ArrayList<>();
	ArrayList<String> idList =  new ArrayList<>();;
	ArrayList<String> portList  = new ArrayList<>();;
	LoginWindow loginWindow;
	MainMenu mainMenu = null;
	boolean mPacketArrived = false;
	volatile boolean loginFailed=false;
	Socket sock;
	DataOutputStream out2;
	BufferedOutputStream bout;
	DataInputStream din ;
	boolean fileProc = false;
	@SuppressWarnings("unchecked")
	public void includeLoginWindow(LoginWindow loginWindow) {
		this.loginWindow = loginWindow;
	}
	public HashMap<Integer,TalkWindow> getChattingRoomHashMap(){
		return chattingRoom;
	}
	public ArrayList<String> getIDIPPort(String content){
//		ArrayList<String> ipList = new ArrayList<>();
//		idList = new ArrayList<>();
//		portList =new ArrayList<>();
		ArrayList<String> id_ip_port_List = new ArrayList<>();
		StringTokenizer st = new StringTokenizer(content, "#");
		String token;
	//	mainMenu.popUp("E");
	//	while(st.hasMoreTokens())
		{
			token = st.nextToken();
		//	mainMenu.popUp("token: " + token);
			id_ip_port_List.add(token);
			
			token = st.nextToken();
		//	mainMenu.popUp("token: " + token);
			id_ip_port_List.add(token);
			
			token = st.nextToken();
		//	mainMenu.popUp("token: " + token);
			id_ip_port_List.add(token);
		}
	//	mainMenu.popUp(ipList);
		return id_ip_port_List;
	}
	
	public void caseSendReqIP(String content) {
//		Thread t = new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				try {
					try {
						
						sendFileProc(file,getIDIPPort(content));
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			
//		});
	//	t.start();
	}
	void dispatchContent(String packet) {
		char packetType = packet.charAt(0);
		String content = null;// String content 내용 패턴: "sender.chatMessage"
		content = packet.substring(1, packet.length());
		TalkWindow talkWindow;
		boolean idFound;
		
//		if(mainMenu!=null)
//		mainMenu.popUp(content.charAt(0));
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
		case Packet.sendReqIP:
			
		//	mainMenu.popUp(ipList);
			caseSendReqIP(content);
			break;
		case Packet.getFriendInfo:
			try {
			//	JOptionPane.showInputDialog("getFriendInfo case");
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
			try {
				deliverFileToTalkWindow(content);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 'Z':
			startReceiveFile(content);
			break;
		case Packet.editProfile:
			break;
		case Packet.login:
			loginFailed = LoginFailed(content);
			if (!loginFailed)
			{
			
				GetFriendList();
				
			}
			else
				System.out.println("Login Failed.");
			
			break;
		}
	}
	
	private void startReceiveFile(String content) {
		// TODO Auto-generated method stub
		String senderID = new String(content);
		ReceiveFile rf = new ReceiveFile(senderID, mainMenu, oos,ois);
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

	int findCharacterIdx(String content, int c, int dot_seq) {
		int dot_cnt = 0;
		for (int i = 0; i < content.length(); i += 1) {
			if (content.charAt(i) == c) {
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
		int startIdx = findCharacterIdx(content,'.', 1);
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
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	void deliverFileToTalkWindow(String content) throws FileNotFoundException, IOException {
		StringTokenizer tokenizer = new StringTokenizer(content, "#");
		int fileLen = Integer.valueOf(tokenizer.nextToken());
		String fileName = tokenizer.nextToken();
		String senderID = tokenizer.nextToken();
		String myID = tokenizer.nextToken();
		int fileStartIdx = findCharacterIdx(content,'.',2)+1;
		int bytesRead=0;
		BigInteger a=null;
		byte[] byteArr=null;
		String fileContent = content.substring(fileStartIdx);
		FileOutputStream fos = new FileOutputStream(new File(fileName));
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		
	//	byte[] bytes = new java.math.BigInteger(hexText, 16).toByteArray();
		JOptionPane.showInputDialog("In deliverFileToTalkWindow:: fileLen: "+ fileLen);
		byteArr=hexStringToByteArray(fileContent);
		 fos.write(byteArr);
		 fos.flush();
		 fos.close();
		 System.out.println(byteArr);
		
	}
	void deliver1to1MessageToTalkWindow(String content) {
		StringTokenizer tokenizer = new StringTokenizer(content, ".");
		String from = tokenizer.nextToken();
		int startIdx = findCharacterIdx(content,'.', 1);
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

	
	public static String byteArrayToHex(byte[] a) {
				
		   StringBuilder sb = new StringBuilder();
		   for(byte b: a)
		      sb.append(String.format("%02x", b));
		   return sb.toString();
		}
	public synchronized void sendFileProc(File file,ArrayList<String> id_ip_port_List) throws IOException{
		FileInputStream fin = null;
		BufferedInputStream bin =null;
		StringBuilder filePacket = new StringBuilder();
		byte[] fileContent;
		int fileLen =  (int)file.length();
		byte buf[] = new byte[(int) fileLen];
		ArrayList<Socket> socketList = new ArrayList<>();
		String receiverID = id_ip_port_List.get(0);
		String receiverIP = id_ip_port_List.get(1);
		String receiverPort = id_ip_port_List.get(2);
		Socket sock = null;
		DataOutputStream out2 = null;
		BufferedOutputStream bout = null;
		DataInputStream din = null;
		
		filePacket.append(loginID);
		filePacket.append("#");
		filePacket.append(file.getName());
		filePacket.append("#");
		filePacket.append(String.valueOf(fileLen));
	//	JOptionPane.showInputDialog("filePacket : " + filePacket.toString());
	//	JOptionPane.showInputDialog("roomUserIPList: " + roomUserIPList);
		 
		try{
			
		fin = new FileInputStream(file);
	     bin = new BufferedInputStream(fin, (int)fileLen);
	     bin.read(buf, 0, (int)fileLen);
	   //  filePacket.append(byteArrayToHex(buf));
	     
	  //   filePacket.append(c);    Q:뭔가 더 보내야하나?   A:파일제목!
	     
	     //JOptionPane.showInputDialog("roomUserIPList.size() : " + roomUserIPList.size());
	//   TalkWindow t = new TalkWindow(this,null);
	//     for(int i=0;i<roomUserIPList.size();i+=1) {
	 //   	 if(!idList.get(i).equals(loginWindow.loginID))
	  //  	 {
	    //		 JOptionPane.showInputDialog("roomUserIP: " +roomUserIPList.get(i));
	 //   		 JOptionPane.showInputDialog("idList id:"+idList.get(i));
	  // 		 JOptionPane.showInputDialog("loginWindow id: " + loginWindow.loginID);
//	    		
	    		 sock = new Socket(receiverIP,Integer.valueOf(receiverPort));
		   // 	 socketList.add(sock);
	    		
		    	 out2 = new DataOutputStream(sock.getOutputStream()); 
	    		 out2.writeUTF(filePacket.toString());
	    		 
	    		 
	    		 bout = new BufferedOutputStream(out2,2048);
		    	 din = new DataInputStream(sock.getInputStream());
		    	 sendFile(bout, din, buf, (int)fileLen);
		    	 
		    	 
		    	 
	    //	 }
	   //  }
	//     oos.writeObject(filePacket.toString());
	  //   JOptionPane.showInputDialog("In sendFile() :: fileLen: " +fileLen);
	   }
	   catch(IOException e) {
	      e.printStackTrace();
	   }
		finally {
			bin.close(); bin = null;
			fin.close(); fin=null;
			bout.close(); bout = null;
			out2.close(); out2 = null;
			din.close(); din = null;
			sock.close(); sock = null;
//			for(int i=0;i<socketList.size();i+=1)
//			{
//				Socket sock =	socketList.get(i);
//				sock.close();
//			}
		}
	}
	public void sendFile(BufferedOutputStream bout, DataInputStream din, byte[] data, int fileLen) {
		int size = 2048;
		int count = fileLen / size;
		int rest = fileLen % size;
		int flag = 1;
		String state=null;
		
		if(count ==0)flag=0;
		for(int i=0;i<=count;i+=1) {
			try {
			if(i==count && flag == 0) {
				bout.write(data, 0, rest);
				bout.flush();
				state = din.readUTF();
				//JOptionPane.showConfirmDialog(mainMenu, state);
				mainMenu.popUp(state);
				return;
			}
			else if(i==count) {
				bout.write(data, i*size, rest);
				bout.flush();
				state = din.readUTF();

				mainMenu.popUp(state);
			//	JOptionPane.showConfirmDialog(mainMenu, state);
				return;
			}
			else {
				bout.write(data, i*size, size);
				bout.flush();
				
			}
			}
			catch(SocketException e) {
			//	e.printStackTrace();
				
			}
			catch(IOException ie) {}
		}
	}
	public void sendReqIP(File file,ArrayList<String> roomUserIDList, boolean isGroup) throws IOException {
		
		this.file = file;
		StringBuilder packet = new StringBuilder();
		
		
		packet.append("I");
		packet.append(loginWindow.loginID + "#");
		for(int i=0;i<roomUserIDList.size();i+=1)
			if(!loginID.equals(roomUserIDList.get(i)))
				packet.append(roomUserIDList.get(i)+"#");
		oos.writeObject(packet.toString());
	//
		
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
				packet = (String) ois.readObject();
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
			oos = new ObjectOutputStream(out);
			in = new DataInputStream(socket.getInputStream());
			ois = new ObjectInputStream(in);
			
			
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
					oos.writeObject("Q");
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

	
	void GetFriendList() {
		StringBuilder message = new StringBuilder();
		message.append("C");
		message.append(loginID );
		// System.out.println(message);
		try {
			oos.writeObject(message.toString());
			// System.out.println("E");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String getIp()  {
        URL whatismyip = null;
		try {
			whatismyip = new URL("http://checkip.amazonaws.com");
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        BufferedReader in = null;
        try {
            try {
				in = new BufferedReader(new InputStreamReader(
				        whatismyip.openStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            String ip = null;
			try {
				ip = in.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return ip;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        }

	public void sendLoginPacket(String ID, String password) throws IOException { // case L
		StringBuilder LoginPacket = new StringBuilder();
		this.loginID = ID;
		LoginPacket.append("L");
		LoginPacket.append(ID + "#");
		LoginPacket.append(password + "#");
		LoginPacket.append(InetAddress.getLocalHost().getHostAddress());
		oos.writeObject(LoginPacket.toString());
	}

	public void AddFriend(String loginID, String friendId) {
	//	EnumPerson passwordField = EnumPerson.valueOf("password");
		String searchPacket = "A";
		searchPacket = searchPacket.concat(friendId);
		searchPacket = searchPacket.concat(".");
		searchPacket = searchPacket.concat(loginID);

		try {
			oos.writeObject(searchPacket);
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
			oos.writeObject(searchPacket.toString());
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
			oos.writeObject(searchPacket);
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
		//	System.out.println("loadFriendInfoFromServer:: Added ID: " + ID);
		}
		for(String[] rec : friendInfo_list) {

			dlm.add(rec[PersonRecord.ID]);
			friendID_list.add(rec[PersonRecord.ID]);
			//friendInfo_list.add(rec);
		}
		friendTab.setBorderText(friendInfo_list.size());

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
	
		oos.writeObject(packet.toString());
		
		return packet.toString();
	}
	//1:N대화메세지 전송기능
	public String sendChatMessage(String text, int chatRoomNumber,ArrayList<String> talkCompanions) throws IOException{
		StringBuilder packet = new StringBuilder();
		
		packet.append("G");
		packet.append("#");
		packet.append(String.valueOf(chatRoomNumber));
		packet.append("#" + loginWindow.loginID );
		for(int i=0;i<talkCompanions.size();i+=1)
			packet.append("#"+talkCompanions.get(i));
		packet.append("#."+text);
		oos.writeObject(packet.toString());
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
			oos.writeObject(packet.toString());
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
			oos.writeObject(packet.toString());
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
	

}
