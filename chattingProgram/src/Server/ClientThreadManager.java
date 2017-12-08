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
import java.util.StringTokenizer;
enum personTableField{id,password,emp_no,name,age,tel};
public class ClientThreadManager extends Thread{
	Socket socket;
	DataInputStream in;
	DataOutputStream out;
	HashMap clients;
	String clientID=null;
	DB_Person db_person;
	String ID="";   String password=""; 
	String name="";  String age=""; String tel="";
	String workspace = "C:\\Users\\user\\git\\Project1\\chattingProgram\\";
	personTableField passwordField = personTableField.valueOf("password");
	@SuppressWarnings("unchecked")
	ClientThreadManager(Socket socket, DB_Person db_person,HashMap clients)
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
	boolean checkIfRegistered(DB_Person db_person, String id, String password)
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
	void readMemberInfoFromClient()
	{
		
		try {
			ID = in.readUTF();
			password = in.readUTF(); name=in.readUTF();
			age = in.readUTF();	tel = in.readUTF();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		
		System.out.println("Here1");
		String loginID="";
		String loginPassword="";

		switch(packet.charAt(0))
		{
		case 'A': // 친구아이디찾기기능  통신  // content : ID
			saveAndSendClientInfo(db_person, content);
			break;
		case 'C': // 회원한명의 등록된 각각의 친구정보들을 보내줌. 
			try {
				sendClientFriendInfo(content);	}
			catch (IOException e1) {
				e1.printStackTrace();	}
			break;
		case 'D':  
			joinMember();
			break;
		case 'L':
			sendLoginFlag(content); 
			break;
		case 'M':
			try {
				deliverChatMessage(content);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 'Q':
			
			break;
		case '1': // 방 통신
			break;
		case '2': //...
			break;
		case '3':
			break;
		}
	}
	private void sendLoginFlag(String content) {
		String loginID;
		String loginPassword;
		try {
			loginID = in.readUTF();
			loginPassword = in.readUTF();
			if(checkIfRegistered(db_person, loginID,loginPassword))
			{
				clientID = new String(loginID);
				clients.put(clientID, out);
				out.writeUTF("1");
				System.out.println("login id: " + loginID);
				System.out.println("login password: " + loginPassword);
				System.out.println("sent login success flag.");
			}
			else
			{
				out.writeUTF("0");
				//in.close();
				//out.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void joinMember() {
		String dbCommand;
		readMemberInfoFromClient();
/*		System.out.println("** registered Content from client");
		System.out.println(ID); System.out.print(password);
		System.out.println(name); System.out.println(age);
		System.out.println(tel);*/
		dbCommand = "insert into person "
				+ "(id,password,emp_no,name,age,tel)" + " values('"
				+ ID + "','" + password
				+ "','" + 2009100224 + "','" + name + "','" + age + "','" + tel + "')";
		db_person.insert(dbCommand);
		db_person.addNewMemberInfo(ID, password, "2009100224", name, age, tel);
		createNewMemberFile(ID);
	}
	void readClientFriendInfoFromStorage(ArrayList<String> clientFriendIDList)
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
	private void sendClientFriendInfo(String clientID) throws IOException {
		ArrayList<String> clientFriendIDList = new ArrayList<>();
		int friendID_list_idx=0;
		StringBuilder clientFriendIDPkt = new StringBuilder();
		
		readClientFriendInfoFromStorage(clientFriendIDList);
		clientFriendIDPkt.append("C");
		if(clientFriendIDList.size() > 0)
		{
			for(int tuple_idx=0;tuple_idx<db_person.person_tuplecount;tuple_idx+=1)
			{
				if( db_person.personTable.get(tuple_idx)[0].equals(clientFriendIDList.get(friendID_list_idx))) 		
				{
				//	System.out.println("friendID_list_idx: " + friendID_list_idx);
					for(int j=0;j<6;j+=1)
					{
						if(j==passwordField.ordinal())
							continue;
						//	out.writeUTF((String)db_person.personTable.get(tuple_idx)[j]);
						clientFriendIDPkt.append((String)db_person.personTable.get(tuple_idx)[j] + ".");
					}
					friendID_list_idx+=1;
					if(clientFriendIDList.size() == friendID_list_idx)
						break;
				}
			}
		}
		out.writeUTF(clientFriendIDPkt.toString());
	}
	private void saveAndSendClientInfo(DB_Person db_person, String tryingAddID) {
		String clientID;
		boolean idfound=false;
		StringTokenizer st = new StringTokenizer(tryingAddID,".");
		
		System.out.println("tryingAddID: " + tryingAddID);
		tryingAddID = st.nextToken();
		System.out.println("tryingAddID: " + tryingAddID);
		clientID = st.nextToken();

		for(int tuple_idx=0;tuple_idx<db_person.person_tuplecount;tuple_idx+=1)
		{
			if(db_person.personTable.get(tuple_idx)[0].equals(tryingAddID))
			{
				idfound = true;
				saveTryingAddIDTo_clientIDtxt(clientID,tryingAddID);
				try {
					for(int j=0;j<6;j+=1)
					{
						if(j==passwordField.ordinal())
							continue;
						out.writeUTF((String)db_person.personTable.get(tuple_idx)[j]);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
		if(idfound== false)
		{
			for(int j=0;j<6;j+=1)
				try {
					if(j==1) continue;
					out.writeUTF(".");
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
	private void deliverChatMessage(String content) throws IOException
	{
		StringTokenizer tokenizer = new StringTokenizer(content,".");
		String sender = tokenizer.nextToken();
		String receiver = tokenizer.nextToken();
		StringBuilder packet = new StringBuilder();
		int	messageFirstIdx = findDotIdx(content, 2)+1;
		String message = content.substring(messageFirstIdx, content.length());
		DataOutputStream out;
		out = (DataOutputStream)clients.get(receiver);

		if(out == null)
		{
			//out.writeUTF("M" + sender +".이 로그인하지 않았습니다.");
		}
		else
		{
			packet.append("M");
			packet.append(sender+".");
			packet.append(message);
			out.writeUTF(packet.toString()); //클라이언트대화창 , 클라이언트에서 메세지받을 작업 구현
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
					}
					catch(SocketException e)
					{
						System.out.println("클라이언트와의 연결을 종료합니다.");
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