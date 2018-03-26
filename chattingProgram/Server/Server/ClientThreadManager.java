package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import Packet.PacketHeader;
import db.PersonTable;
import loginMenu.RegContent;
enum personTableField{id,password,emp_no,name,age,tel};
public class ClientThreadManager extends Thread{
	Socket socket;
	DataInputStream in;
	DataOutputStream out;
	HashMap clients;
	String clientID=null;
	PersonTable db_person;
	RegContent newMemberInfo;
	String workspace = "C:\\Users\\user\\git\\Project1\\chattingProgram\\FriendListFileDir\\";
	personTableField passwordField = personTableField.valueOf("password");
	
	@SuppressWarnings("unchecked")
	ClientThreadManager(Socket socket, PersonTable db_person,HashMap clients)
	{
		this.socket =socket;
		this.db_person = db_person;
		this.clients = clients;
	
		try {
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			
		}catch(IOException e)
		{
			
		}
	}
	boolean checkIfRegistered(PersonTable db_person, String id, String password)
	{
		String tuple_id;
		String tuple_password;
		boolean is_valid = false;
		for(int i=0;i< db_person.person_tuplecount; i+=1)
		{
			tuple_id  = (String) db_person.personTable.get(i)[0];//[0];
			tuple_password = (String)db_person.personTable.get(i)[1];
			System.out.println(tuple_id);
			System.out.println(tuple_password);
			if(tuple_id.equals(id) && tuple_password.equals(password))
			{
				is_valid = true;
				break;
			}
		}
		return is_valid;
	}
	void readJoinMemberInfoFromClient(String content) // 
	{
		StringTokenizer tokenizer = new StringTokenizer(content,".");
		newMemberInfo = new RegContent();
		
			newMemberInfo.setRegID(tokenizer.nextToken());
			newMemberInfo.setRegPassword(tokenizer.nextToken());
			newMemberInfo.setRegName(tokenizer.nextToken());
			newMemberInfo.setRegAge(tokenizer.nextToken());
			newMemberInfo.setRegPhone(tokenizer.nextToken());
		 
	}
	void createNewMemberFile(String clientID)
	{
		BufferedWriter writer=null;
	
		String path = new String(workspace);
		path = path.concat(clientID);
		path = path.concat("_Friends.txt");
		System.out.println(path);
	//	System.out.println(content);
		try {
			writer = new BufferedWriter(new FileWriter(path));
			writer.write("");
			writer.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
	}
	public void reply(String packet)
	{
		String content=null;
			content = packet.substring(1,packet.length());
	//	System.out.println(message);
		boolean idfound = false;
		String path = null;
		BufferedReader reader = null;
		BufferedWriter writer = null;
		String clientID = null;
		String dbCommand =null;
		
		//System.out.println("Here1");
		String loginID="";
		String loginPassword="";
		System.out.println("packet: " + packet);
		switch(packet.charAt(0))
		{
		case PacketHeader.addFriend: // 친구아이디찾기기능  통신  // content : ID
			saveAndSendClientInfo(db_person, content,true); // last boolean은  
															//true면 멤버정보 보내주는것 + add/save 기능 ,  false면  멤버정보만 보내주는것
			break;
		case PacketHeader.getUserInfo:
			sendUserInfo(content);
			break;
		case PacketHeader.getFriendInfo: // 회원한명의 등록된 각각의 친구정보들을 보내줌. 
			try {
			//	System.out.println("reply case C:");
				sendClientFriendListInfo(content);	}
			catch (IOException e1) {
				e1.printStackTrace();	}
			break;
		case PacketHeader.joinMember:  
			joinMember(content);
			break;
		case PacketHeader.editProfile:
			editUserProfile(content);
			break;
		case PacketHeader.sendFile:
			try {
				deliverFileToClient(content);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case PacketHeader.login:
		//	System.out.println("reply() :: case L : " + content);
			sendLoginFlag(content); 
			break;
		case PacketHeader.sendChatMessage_1TON:
			try {
				deliverChatMessage_1toN(content);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case PacketHeader.sendChatMessage_1TO1:
			try {
				deliverChatMessage_1to1(content);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 'Q':
			
			break;
		case PacketHeader.searchMember:
			saveAndSendClientInfo(db_person, content,false);
			break;
		case '1': // 방 통신
			break;
		case '2': //...
			break;
		case '3':
			break;
		}
	}
	private void editUserProfile(String content) {
		// TODO Auto-generated method stub
		// id, passwd, name, phoneNum
		int i=0;
		int personTable_idx=-1;
		String sqlsrc="update person set ";
		String sql="update person set ";
		StringTokenizer editContent = new StringTokenizer(content, ".");
		String attributeName[] = {"id","password", "name","tel"};
		String[] attributeValue = new String[4];
		int changedColumnCnt=0;
		while(editContent.hasMoreTokens())
		{
			attributeValue[i] = editContent.nextToken();
			i+=1;
		}
		
		
		for(i=0;i<db_person.personTable.size();i+=1)
		{
			if(db_person.personTable.get(i)[0].equals(attributeValue[0]))
			{
				personTable_idx = i;
				break;
			}
		}
		
		int attributeValue_idx=1;
		for(i=0;i<6;i+=1)
		{
			if(i%2==1) // password, name, tel Index:  1, 3, 5
			{
				if(!attributeValue[attributeValue_idx].equals("#"))
				{
					db_person.personTable.get(personTable_idx)[i] = attributeValue[attributeValue_idx];
				}
				attributeValue_idx+=1;
			}
		}
		for(int x=1;x<4;x+=1)
		{
			if(attributeValue[x].equals("#"))
			{
				continue;
			}
			else
			{
				if(changedColumnCnt > 0)
					sql+=",";
				sql += attributeName[x];
				sql += "='";
				sql += attributeValue[x];
				sql += "'";
				changedColumnCnt+=1;
				System.out.println("aN: " + attributeName[x] + " aV:" + attributeValue[x]);
			}
		}
		if(sql.equals(sqlsrc))
		{
			System.out.println("No Change to edit Profile Info.");
			return;
		}
		else {
			sql+=" where id=";
			sql+="'";
			sql+=attributeValue[0];
			sql+="'";
			db_person.update(attributeValue[0],sql);
			System.out.println("editted Profile Info.");
		}
	}
	private void sendLoginFlag(String content) {
		String loginID;
		String loginPassword;
		StringTokenizer loginContent = new StringTokenizer(content, ".");
		try {
			loginID = loginContent.nextToken(); System.out.println("LoginID: " + loginID);
			loginPassword = loginContent.nextToken(); System.out.println("LoginPassword: " + loginPassword);
			if(checkIfRegistered(db_person, loginID,loginPassword))
			{
				clientID = new String(loginID);
				clients.put(clientID, out);
				out.writeUTF("L1");
				System.out.println("login id: " + loginID);
				System.out.println("login password: " + loginPassword);
				System.out.println("sent login success flag.");
			}
			else
			{
				out.writeUTF("L0");
				System.out.println("Sent login Failed Flag.");
				//in.close();
				//out.close();
			}
		//	Scanner s = new Scanner(System.in);
		//	s.nextLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private int createRegNoForNewMember()
	{
		int regYear = (int)(Math.random()*21) + 2000;
		int regFilter1 = (int)(Math.random()*1000);
		int regFilter2 = (int)(Math.random()*1000);
		StringBuilder regNo = new StringBuilder();
		regNo.append(String.valueOf(regYear));
		regNo.append(String.valueOf(regFilter1));
		regNo.append(String.valueOf(regFilter2));
		return Integer.valueOf(regNo.toString());
	}
	private void joinMember(String content) {
		String dbCommand;
		int regNo =0;
		readJoinMemberInfoFromClient(content);
/*		System.out.println("** registered Content from client");
		System.out.println(ID); System.out.print(password);
		System.out.println(name); System.out.println(age);
		System.out.println(tel);*/
		regNo = createRegNoForNewMember();
		dbCommand = "insert into person "
				+ "(id,password,emp_no,name,age,tel)" + " values('"
				+ newMemberInfo.getRegID() + "','" + newMemberInfo.getRegPassword()
				+ "','" + regNo + "','" + newMemberInfo.getRegName() + "','" 
						+ Integer.valueOf(newMemberInfo.getRegAge())+ "','" + newMemberInfo.getRegPhone() + "')";
		db_person.insert(dbCommand);
		db_person.addNewMemberInfoToList(newMemberInfo);
		createNewMemberFile(newMemberInfo.getRegID());
	}
	void readClientFriendInfoFromFile(ArrayList<String> clientFriendIDList)
	{
		BufferedReader reader;
		String path;
		String friendID;
		path = new String(workspace);
		path = path.concat(clientID);
		path = path.concat("_Friends.txt");
		
		try {
		//	System.out.println(clientID);
			reader = new BufferedReader(new FileReader(path));
		
			while(true)
			{
			friendID = reader.readLine();
			//System.out.print(friendID);
			if(friendID == null)
				break;
			else if(friendID.equals(""))
				continue;
			clientFriendIDList.add(friendID);
			}
			Collections.sort(clientFriendIDList, String.CASE_INSENSITIVE_ORDER);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {
			
		}
	}
	private void sendClientFriendListInfo(String clientID) throws IOException {  // case C
		// 한고객의 모든친구들의 각각 튜플내용 다 보내주기(패스워드제외)
		ArrayList<String> clientFriendIDList = new ArrayList<>();
		int friendID_list_idx=0;
		StringBuilder clientFriendListInfoPkt = new StringBuilder();
		
		readClientFriendInfoFromFile(clientFriendIDList); // 파일에 있는 한 고객의 친구 아이디를 전부 읽어
																//ArrayList::clientFriendIDList에 저장.
		clientFriendListInfoPkt.append("C");
		if(clientFriendIDList.size() > 0)
		{
			for(int tuple_idx=0;tuple_idx < db_person.person_tuplecount;tuple_idx+=1)
			{
				if( db_person.personTable.get(tuple_idx)[0].equals(clientFriendIDList.get(friendID_list_idx))) 		
				{
				//	System.out.println("friendID_list_idx: " + friendID_list_idx);
					for(int j=0;j<6;j+=1)
					{ // id, no, name, age, phoneNum
						if(j==passwordField.ordinal())
							continue;
						//	out.writeUTF((String)db_person.personTable.get(tuple_idx)[j]);
						clientFriendListInfoPkt.append((String)db_person.personTable.get(tuple_idx)[j] + ".");
					}
					friendID_list_idx+=1;
					if(clientFriendIDList.size() == friendID_list_idx)
						break;
				}
			}
		}
		//System.out.println(clientFriendListInfoPkt.toString());
		out.writeUTF(clientFriendListInfoPkt.toString());
	}
	private void saveAndSendClientInfo(PersonTable db_person, String tryingAddID, boolean save) { // case A,S
		String clientID;
		boolean idfound=false;
		StringTokenizer st = new StringTokenizer(tryingAddID,".");
		StringBuilder friendInfo = new StringBuilder();
	//	System.out.println("tryingAddID: " + tryingAddID);
		tryingAddID = st.nextToken();
	//	System.out.println("tryingAddID: " + tryingAddID);
		clientID = st.nextToken();

		if(save == true)
			friendInfo.append("A");
		else
			friendInfo.append("S");
		for(int tuple_idx=0;tuple_idx<db_person.person_tuplecount;tuple_idx+=1)
		{
			if(db_person.personTable.get(tuple_idx)[0].equals(tryingAddID))
			{
				idfound = true;
				if(save == true)
					saveTryingAddIDTo_clientIDtxt(clientID,tryingAddID);
				
					for(int j=0;j<6;j+=1)
					{
						if(j==passwordField.ordinal())
							continue;
						friendInfo.append((String)db_person.personTable.get(tuple_idx)[j] + ".");
					//	out.writeUTF((String)db_person.personTable.get(tuple_idx)[j]);
					}
				
				break;
			}
		}
		if(idfound== true)
		{
			try {
				out.writeUTF(friendInfo.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(idfound== false)
		{
			try {
				if(save==true)
					out.writeUTF("A.....");
				else
					out.writeUTF("S.....");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void saveTryingAddIDTo_clientIDtxt( String clientID,String tryingAddID) {
		String path;
		PrintWriter writer;
		
		path = workspace;
		path = path.concat(clientID);
		path = path.concat("_Friends.txt");
		System.out.println(path);
//	System.out.println(content);
		try {
			writer = new PrintWriter(new FileWriter(path, true));
	
		//	writer.flush();
			System.out.print("In saveClientIDTo_Friendstxt : " + tryingAddID + "\n");
			writer.write(tryingAddID);
			writer.println();
			writer.flush();
			writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	int findDotIdx(String content, int dot_seq) //dot_seq: 1부터 시작
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
	private void deliverFileToClient(String content) throws IOException
	{
		StringTokenizer tokenizer = new StringTokenizer(content, ".");
		String sender = tokenizer.nextToken();
		String receiver = tokenizer.nextToken();
		String file = tokenizer.nextToken();
		StringBuilder packet = new StringBuilder();
		
		packet.append("F");
		packet.append(sender+".");
		packet.append(file);
		
		DataOutputStream out = (DataOutputStream)clients.get(receiver);
		out.writeUTF(packet.toString());
		
	}
	private void deliverChatMessage_1toN(String content) throws IOException
	{
		int messageFirstIdx = findDotIdx(content,1)+1;
		String subContent = content.substring(0,messageFirstIdx-1);
		StringTokenizer tokenizer = new StringTokenizer(subContent,"#");
		String chatRoomNumber = tokenizer.nextToken();
		String sender = tokenizer.nextToken();
		List<String> receivers = new ArrayList<String>();
		String token;
		String message = content.substring(messageFirstIdx, content.length());
		//int personCnt=0;
		
		while(tokenizer.hasMoreTokens())
		{
			token = tokenizer.nextToken();
		if(token.charAt(0)=='.')
			break;
		receivers.add(token);
			//personCnt+=1;
		}
		/*
		packet.append("G"+"#");
		packet.append(chatRoomNumber + "#");
		packet.append(sender+"#");
		for(int x=0;x<receivers.size();x+=1)
		{
			packet.append(receivers.get(x) + "#");
		}
		packet.append("." + message);*/
		
		DataOutputStream[] out = new DataOutputStream[receivers.size()];
		for(int i=0;i<receivers.size();i+=1) {
			
				out[i] = (DataOutputStream)clients.get(receivers.get(i));
				if(out[i] == null)
				{
			//		out[i].writeUTF("G" + sender +".이 로그인하지 않았습니다.");
				}
				else
				{
					if(!receivers.get(i).equals(sender)) 
						out[i].writeUTF("G"+content); //클라이언트대화창 , 클라이언트에서 메세지받을 작업 구현
				
				}
		}
		
		
	}
	private void deliverChatMessage_1to1(String content) throws IOException
	{
		StringTokenizer tokenizer = new StringTokenizer(content,".");
		String sender = tokenizer.nextToken();
		String receiver = tokenizer.nextToken();
		StringBuilder packet = new StringBuilder(); // server에서 client로 보낼 패킷 
		int	messageFirstIdx = findDotIdx(content, 2)+1;
		String message = content.substring(messageFirstIdx, content.length());
		DataOutputStream out;
		out = (DataOutputStream)clients.get(receiver);
		
		if(out == null)
		{
			out.writeUTF("M" + sender +".이 로그인하지 않았습니다.");
		}
		else
		{
			packet.append("M");
			packet.append(sender+".");
			packet.append(message);
			out.writeUTF(packet.toString()); //클라이언트대화창 , 클라이언트에서 메세지받을 작업 구현
		}
	}
	private void sendUserInfo(String content)
	{
		int i=0;
		StringTokenizer packetTokenizer = new StringTokenizer(content, ".");
		String userID = packetTokenizer.nextToken();
		StringBuilder userInfoPacket = new StringBuilder();
		ArrayList<String[]> personTableTupleList = db_person.personTable;
		userInfoPacket.append("B");
		for(i=0;i<db_person.person_tuplecount;i+=1)
		{
			if(personTableTupleList.get(i)[0].equals(userID))
			{
				for(int x=0;x<6;x+=1)
				{
					userInfoPacket.append(personTableTupleList.get(i)[x] + ".");
				}
			}
		}
		try {
			out.writeUTF(userInfoPacket.toString());
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
				while(in!=null)
				{
					try {
					message = in.readUTF();
					System.out.println("* Server message: " + message);
					}
					catch(SocketException e)
					{
					//	e.printStackTrace();
						System.out.println(clientID + "님께서  서버와의 연결을 종료하였습니다.");
						break;
					}
					
				//	System.out.println("Message: " + message);
					reply(message);
				}
		}
		catch(Exception e) { e.printStackTrace();}
		finally {
			
		
		}
		
	}
}