package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import Packet.PacketHeader;
import db.PersonTable;
import db.RegContent;
enum personTableField{id,password,emp_no,name,age,tel};
public class ClientThreadManager extends Thread{
	Socket socket;
	DataInputStream dis;
	DataOutputStream dos;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	HashMap client_oos;
	HashMap client_ois;
	String clientID=null;
	String clientIP = null;
	PersonTable db_person;
	RegContent newMemberInfo;
	String workspace = "C:\\Users\\syhn6\\OneDrive\\문서\\chattingApp_workspace\\chattingApp\\src\\FriendListFileDir\\";
	personTableField passwordField = personTableField.valueOf("password");
	static HashMap<String,String> clientIPList ;
	static String ipPkt;
	int waitClientCnt=0;
	int waitClientIdx=0;
//	int pPacketReceivedCnt = 0;
	int pPacketReceivedCnt=0;
	int pPacketSentCnt = 0;;
	static ArrayList<String> pPacketList;
	static int columnCnt=7;
	static {
		clientIPList = new HashMap<>();
	}
	@SuppressWarnings("unchecked")
	ClientThreadManager(Socket socket, PersonTable db_person,HashMap client_oos,HashMap client_ois)
	{
		this.socket =socket;
		this.db_person = db_person;
		this.client_oos = client_oos;
		this.client_ois = client_ois;
		
		try {
			dis = new DataInputStream(socket.getInputStream());
			ois = new ObjectInputStream(dis);
			dos = new DataOutputStream(socket.getOutputStream());
			oos = new ObjectOutputStream(dos);
			
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
		if(packet!=null)
			content = packet.substring(1,packet.length());
		System.out.println(packet);
		boolean idfound = false;
		String path = null;
		BufferedReader reader = null;
		BufferedWriter writer = null;
		String clientID = null;
		
		String dbCommand =null;
		String ipPacket = null;
		
		
		//System.out.println("Here1");
		String loginID="";
		String loginPassword="";
		System.out.println("packet: " + packet);
		switch(packet.charAt(0))
		{
		case PacketHeader.addFriend: // 친구아이디찾기기능  통신  // content : ID
		//	saveAndSendClientInfo(db_person, content,true); // last boolean은  
															//true면 멤버정보 보내주는것 + add/save 기능 ,  false면  멤버정보만 보내주는것
			
			
			addFriend(content); // 20181018_친구추가기능 작성
			break;
		case PacketHeader.getUserInfo:
			sendUserInfo(content);
			break;
		case PacketHeader.getFriendInfo: // 회원한명의 등록된 각각의 친구정보들을 보내줌. 
			try {
			//	System.out.println("reply case C:");
			//	sendClientFriendListInfo(content);	
			    sendFriendListInfo(content);	
				}
			catch (IOException e1) {
				e1.printStackTrace();	}
			break;
		case PacketHeader.joinMember:  
			joinMember(content);
			break;
		case PacketHeader.editProfile:
			editUserProfile(content);
			break;
		case PacketHeader.sendReqIP:
			try {
				
			//	sendReceiveFileStartSignal(content);
				
				pPacketSentCnt= makeIPPacket_SendSignal(content);
				
			//	ipPkt = new String(ipPacket);
				System.out.println("case I) ipPkt: " + ipPkt);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			break;
		case PacketHeader.sendFile:
//			try {
//				//deliverFileToClient(content);
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
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
		case 'P':
//			pPacketList.add(content);
//			while(pPacketList.size() < pPacketSentCnt) {
//				
//			}
		//	System.out.println("case P) ipPkt: " + ipPkt);
		//	if(pPacketReceivedCnt == pPacketSentCnt) 
			{
				sendIPPortPacket(content);
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
	private void sendFriendListInfo(String content) throws IOException {
		// TODO Auto-generated method stub
		ArrayList<String> clientInfo = null;
		ArrayList<String> friendIDList = new ArrayList<String>();
		int friendID_list_idx=0;
		StringTokenizer st = null;// st = new StringTokenizer(, "%");
		String clientID = content;
		StringBuilder clientFriendListInfoPkt = new StringBuilder();
		StringBuilder selectSql = new StringBuilder();
		StringTokenizer st2;
		String attValue;
		
		//readClientFriendInfoFromFile(clientFriendIDList); // 파일에 있는 한 고객의 친구 아이디를 전부 읽어 //ArrayList::clientFriendIDList에 저장.
		selectSql.append("select friendIDList from person where id = '");
		selectSql.append(clientID);
		selectSql.append("';");
		attValue = db_person.selectOne(selectSql.toString(), "friendIDList");
		
		st2  = new StringTokenizer(attValue, "%");
		while(st2.hasMoreTokens()) {
			friendIDList.add(st2.nextToken());
		}
		clientFriendListInfoPkt.append("C");
		for(int i=0;i<friendIDList.size();i+=1) {
			selectSql = null;
			selectSql = new StringBuilder();
			selectSql.append("select * from person where id ='");
			selectSql.append(friendIDList.get(i));
			selectSql.append("';");
			
			System.out.println("selectSql: " + selectSql.toString());
			clientInfo = db_person.selectAll(selectSql.toString());
			System.out.println("clientInfo : " + clientInfo);
			
			
			if(clientInfo.size() > 0)
			{
	//			st = new StringTokenizer(clientFriendIDList.get(0), "%");	
	//			while(st.hasMoreTokens()) {
	//				clientFriendListInfoPkt.append(st.nextToken() + ".");
	//			}
				for(int tuple_idx=0;tuple_idx < clientInfo.size();tuple_idx+=1)
				{
					clientFriendListInfoPkt.append(clientInfo.get(tuple_idx) + ".");
				}
			}
		}
		System.out.println("clientFriendListInfoPkt: " + clientFriendListInfoPkt.toString());
		oos.writeObject(clientFriendListInfoPkt.toString());
	}
	private void addFriend(String content) {
		// TODO Auto-generated method stub
		StringTokenizer st = new StringTokenizer(content,".");
		String tryingAddID = st.nextToken();
		String clientID = st.nextToken();
		ArrayList<String> attributeList;
		attributeList = db_person.selectAll(tryingAddID);
		if(attributeList.isEmpty())
		{
			try {
		//		if(save==true)
					oos.writeObject("A......");
//				else
//					oos.writeObject("S.....");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		 saveFriendID(db_person,clientID, tryingAddID);
		sendFriendInfo(db_person,clientID,tryingAddID);
	}
	private boolean sendFriendInfo(PersonTable db_person2, String clientID, String tryingAddID) {
		// TODO Auto-generated method stub
		String sql = "select * from person where id='";
		sql += tryingAddID;
		sql += "';";
		StringBuilder packet = new StringBuilder();
		ArrayList<String> attributeValueList = null;
		
		attributeValueList = db_person2.selectAll(sql);
		try {
			for(int i=0;i<columnCnt;i+=1) {
				packet.append(attributeValueList.get(i));
				packet.append(".");
			}
			oos.writeObject(packet.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	private void saveFriendID(PersonTable db_person2, String clientID, String tryingAddID) {
		// TODO Auto-generated method stub
		StringBuilder selectSql = new StringBuilder();
		StringBuilder updateSql = new StringBuilder();
		StringBuilder attributeList = new StringBuilder();
		
	//boolean[] bEdit = new boolean[] {false, false,false, false, false, false, true};
		selectSql.append("select friendIDList from person where id='");
		selectSql.append(clientID);
		selectSql.append("';");
		
		attributeList.append((String)db_person2.selectAll(selectSql.toString()).get(0));//(db_person2.select(selectSql.toString()).get(0);
		attributeList.append("%");
		attributeList.append(tryingAddID);
		
		updateSql.append("update table person set friendIDList='");
		updateSql.append(attributeList.toString());
		updateSql.append("';");
		db_person2.update(updateSql.toString());
	}
	//여기확인해보기//
	private void sendReceiveFileStartSignal(String sender,ArrayList<String> clientIDList) throws IOException {
		StringBuilder pkt=new StringBuilder();
		pkt.append("Z");
		pkt.append(sender);
		System.out.println("clientIDList: " + clientIDList);
		//clientIDList.size()나 안의 객체값이 의심된다.
		for(int i=0;i<clientIDList.size();i+=1)
		{
			
			ObjectOutputStream oos = (ObjectOutputStream)client_oos.get(clientIDList.get(i));
			if(oos!=null)
			{	
				System.out.println("In sendReceiveFileStartSignal : " + pkt.toString());
				oos.writeObject(pkt.toString());
			}
		}
		//waitClientCnt = clientIDList.size();
	}
	private void sendIPPortPacket(String content) {
	//	StringTokenizer packetTokenizer = new StringTokenizer(packet, "@");
		String id;
		StringTokenizer id_ip_port = new StringTokenizer(content,"#");
	//	StringTokenizer portTokenizer = new StringTokenizer(content,"#");
	//	String srchID = portTokenizer.nextToken();
		StringBuilder pkt = new StringBuilder();
//		System.out.println(packet);
		String ip;
		String port;
		String senderID;
		
		Scanner sc = new Scanner(System.in);
		
//		System.out.println(" In sendIPPacket()");
//		System.out.println("content: " + content);
//		System.out.println("packet: " + packet);

		pkt.append("I");
		senderID = id_ip_port.nextToken();
		pkt.append(id_ip_port.nextToken()+"#");
		pkt.append(id_ip_port.nextToken()+"#");
		pkt.append(id_ip_port.nextToken()+"#");
//		while(idPartTokenizer.hasMoreTokens()) {
//			
//			id = idPartTokenizer.nextToken();
//			ip = ipPartTokenizer.nextToken(); //packet구조 바꾸는중
//			if(id.equals(srchID)) {
//				pkt.append(id+"#");
//				pkt.append(ip+"#");
//				pkt.append(portTokenizer.nextToken() + "#");
//			}
//		}
//		while(st.hasMoreTokens()) {
//			
//			id = st.nextToken();
//			System.out.println(id);
//			pkt.append(id+"#");
//			
//			token = st.nextToken();
//			System.out.println(token);
//			pkt.append(token+"#");
//			
//			st2.nextToken();
//			token = st2.nextToken();
//			System.out.println(token);
//			pkt.append(token +"#");
//		}
		System.out.println("In sendIPPacket: " + pkt);
		try {
				((ObjectOutputStream)client_oos.get(senderID)).writeObject(pkt.toString());
			//	sc.nextLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private int makeIPPacket_SendSignal(String content) throws IOException {
		// TODO Auto-generated method stub
		StringTokenizer tokenizer = new StringTokenizer(content,"#");
		ArrayList<String> clientIDList = new ArrayList<>();
		ArrayList<String> idportList = new ArrayList<>();
		String senderID;
		int pPacketReceiveCnt=0;
		StringBuilder packet = new StringBuilder();
		//packet.append("I");
		senderID = tokenizer.nextToken();
		packet.append(senderID + "#");
		while(tokenizer.hasMoreTokens()) {
			clientID = tokenizer.nextToken();
			System.out.println("clientID: " + clientID);
			clientIDList.add(clientID);
			packet.append(clientID + "#");
			
		
		}
		packet.append("@");
	//저기 case I)에서 왜 IP부분이 null이 나왔을까 생각해보기...
		for(int i=0;i<clientIDList.size();i+=1)
		{
			packet.append(clientIPList.get(clientIDList.get(i)) + "#");
		}
		pPacketReceiveCnt = clientIDList.size();
		sendReceiveFileStartSignal(senderID, clientIDList);
		System.out.println("In makeIPPacket_SendSignal: " + packet.toString());
		
		return pPacketReceiveCnt;
	}
	int getTupleIndex(String id) {
		int tupleIdx=-1;
		for(int i=0;i<db_person.personTable.size();i+=1) // get TupleIndex from db_person
		{
			if(db_person.personTable.get(i)[0].equals(id))
			{
				tupleIdx = i;
				break;
			}
		}
		return tupleIdx;
	}
	private void editUserProfile(String content) {
		// TODO Auto-generated method stub
		// id, passwd, name, phoneNum
		int i=0;
		int tableTupleIdx=-1;
		int columnNameArr_idx=0;
	
		StringTokenizer editPacketTokenizer = new StringTokenizer(content, ".");
		
								// {"id","password","emp_no", "name", "age", "tel"};
		String[] tuple = new String[columnCnt];
		int changedColumnCnt=0;
		String id = editPacketTokenizer.nextToken();
		boolean bEditable[] = {false, true, false, true, false, true};
		
		
		while(editPacketTokenizer.hasMoreTokens())
		{
			if(bEditable[i])
			{
				tuple[i] = editPacketTokenizer.nextToken();
			}
			else
				tuple[i] = null;
			i+=1;
		}
		
		tableTupleIdx = getTupleIndex(id);
		editPersonTableTuple(tableTupleIdx, tuple);
		editQuery(tableTupleIdx, tuple, changedColumnCnt, id);
	}
	private void editQuery( int tableTupleIdx,String[] tuple,
			int changedColumnCnt, String id) {
		String attributeName[] = {"id","password", "emp_no","name","age","tel","friendIDList"};
		String sqlsrc="update person set ";
		String sql="update person set ";
		boolean bEdit[] = new boolean[columnCnt];
		for(int x=0;x<columnCnt;x+=1)
		{
			if(tuple[x]!=null)
			{
				if(tuple[x].equals("#"))
				{
					bEdit[x]=false;
					continue;
				}
				else
				{
					if(changedColumnCnt > 0)
						sql+=",";
					sql += attributeName[x];
					sql += "='";
					sql += tuple[x];
					sql += "'";
					changedColumnCnt+=1;
					bEdit[x] = true;
					System.out.println("aN: " + attributeName[x] + " aV:" + tuple[x]);
				}
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
			sql+=id;
			sql+="'";
			db_person.update(sql,tuple,tableTupleIdx,bEdit);
			System.out.println("editted Profile Info.");
		}
	}
	private void editPersonTableTuple(int tupleIdx, String[] tuple) {
		int i;
		for(i=0;i<6;i+=1)
		{
			//if(i%2==1) // password, name, tel Index:  1, 3, 5
			if(tuple[i]!=null) {
					if(tuple[i].equals("#")==false) // # is blank(In class editContent,
																		// uneditted content value is "#")
					{
						db_person.personTable.get(tupleIdx)[i] = tuple[i];
					}
			}
		}
	}
	
	private void sendLoginFlag(String content) {
		String loginID;
		String loginPassword;
		String loginIP;
		StringTokenizer loginContent = new StringTokenizer(content, "#");
		try {
			loginID = loginContent.nextToken(); System.out.println("LoginID: " + loginID);
			loginPassword = loginContent.nextToken(); System.out.println("LoginPassword: " + loginPassword);
			loginIP = loginContent.nextToken(); System.out.println("Login Client IP: " + loginIP);
			
			if(checkIfRegistered(db_person, loginID,loginPassword))
			{
				clientID = new String(loginID);
				clientIP = new String(loginIP);
				client_oos.put(clientID, oos);
				client_ois.put(clientID,ois);
				clientIPList.put(clientID,clientIP);
				
					System.out.println("clientIPList[]" + clientIPList.get(clientID));
				oos.writeObject("L1");
				System.out.println("login id: " + loginID);
				System.out.println("login password: " + loginPassword);
				System.out.println("sent login success flag.");
			}
			else
			{
				oos.writeObject("L0");
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
	private void sendClientFriendListInfo() throws IOException {  // case C
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
		System.out.println(clientFriendListInfoPkt.toString());
		oos.writeObject(clientFriendListInfoPkt.toString());
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
				oos.writeObject(friendInfo.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(idfound== false)
		{
			try {
				if(save==true)
					oos.writeObject("A.....");
				else
					oos.writeObject("S.....");
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
	int findCharacterIdx(String content, int character, int dot_seq) //dot_seq: 1부터 시작
	{
		int dot_cnt=0;
		for(int i=0;i<content.length();i+=1)
		{
			if(content.charAt(i)==character)
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
		int fileStartIdx = findCharacterIdx(content,'.', 2)+1;
		StringTokenizer tokenizer = new StringTokenizer(content, "#");
		
		
		
		String sender = tokenizer.nextToken();
		String fileName = tokenizer.nextToken();
		String fileLen = tokenizer.nextToken();
		StringBuilder packet;// = new StringBuilder();
		String packetInfoBeforeReceiver;
		String receiverID;// = tokenizer.nextToken();
		ArrayList<String> receiverIDList=new ArrayList<>();
		
		packet = new StringBuilder();
		packet.append("F" + sender + "#" );
		packet.append(fileName + "#" + fileLen + "#");
		packetInfoBeforeReceiver = packet.toString();
		
		
		while(tokenizer.hasMoreTokens())
		{
			receiverID = tokenizer.nextToken();
			receiverIDList.add(receiverID);
		}
		String pkt = null;
		for(int i=0;i<receiverIDList.size();i+=1) {
			ObjectOutputStream oos = (ObjectOutputStream)client_oos.get(receiverIDList.get(i));
			if(oos!=null)
			{	
				pkt = packetInfoBeforeReceiver + receiverIDList.get(i) + "#." + content.substring(fileStartIdx);
				oos.writeObject(pkt);
				System.out.println("Sent file to " + receiverIDList.get(i) + ".");
			}
			
		}
		
	}
	private void deliverChatMessage_1toN(String content) throws IOException
	{
		int messageFirstIdx = findCharacterIdx(content,'.',1)+1;
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
		
		ObjectOutputStream[] out = new ObjectOutputStream[receivers.size()];
		for(int i=0;i<receivers.size();i+=1) {
			
				out[i] = (ObjectOutputStream)client_oos.get(receivers.get(i));
				if(out[i] == null)
				{
			//		out[i].writeUTF("G" + sender +".이 로그인하지 않았습니다.");
				}
				else
				{
					if(!receivers.get(i).equals(sender)) 
						out[i].writeObject("G"+content); //클라이언트대화창 , 클라이언트에서 메세지받을 작업 구현
				
				}
		}
		
		
	}
	private void deliverChatMessage_1to1(String content) throws IOException
	{
		StringTokenizer tokenizer = new StringTokenizer(content,".");
		String sender = tokenizer.nextToken();
		String receiver = tokenizer.nextToken();
		StringBuilder packet = new StringBuilder(); // server에서 client로 보낼 패킷 
		int	messageFirstIdx = findCharacterIdx(content,'.', 2)+1;
		String message = content.substring(messageFirstIdx, content.length());
		ObjectOutputStream out;
		out = (ObjectOutputStream)client_oos.get(receiver);
		
		if(out == null)
		{
			out.writeObject("M" + sender +".이 로그인하지 않았습니다.");
		}
		else
		{
			packet.append("M");
			packet.append(sender+".");
			packet.append(message);
			out.writeObject(packet.toString()); //클라이언트대화창 , 클라이언트에서 메세지받을 작업 구현
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
			oos.writeObject(userInfoPacket.toString());
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
				while(ois!=null)
				{
					try {
					message = (String) ois.readObject();
					System.out.println("* Server message: " + message);
					}
					catch(SocketException e)
					{
					//	e.printStackTrace();
						System.out.println(clientID + "님께서  서버와의 연결을 종료하였습니다.");
						break;
					}
					catch(EOFException e)
					{
						
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