package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.StringTokenizer;
enum personTableField{id,password,emp_no,name,age,tel};
public class ServerManager extends Thread{
	Socket socket;
	DataInputStream in;
	DataOutputStream out;
	DB_Person db_person;
	String ID="";   String password=""; 
	String name="";  String age=""; String tel="";
	String workspace = "C:\\Users\\user\\git\\Project1\\chattingProgram\\";
	personTableField passwordField = personTableField.valueOf("password");
	ServerManager(Socket socket, DB_Person db_person)
	{
		this.socket =socket;
		this.db_person = db_person;
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
	public void reply(String message)
	{
		String content=null;
			content = message.substring(1,message.length());
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

		switch(message.charAt(0))
		{
		case 'A': // 친구아이디찾기기능  통신  // content : ID
			saveAndSendClientInfo(db_person, content);
			break;
		case 'C': // 회원한명의 등록된 각각의 친구정보들을 보내줌. 
			sendOnesFriendInfo(content);
			break;
		case 'D':  
			joinMember();
			break;
		case 'L':
			sendLoginFlag(content); 
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
	private void sendOnesFriendInfo(String clientID) {
		BufferedReader reader;
		String path;
		String friendID;
		ArrayList<String> friendID_list = new ArrayList<>();
		int friendID_list_idx=0;
		
		System.out.println("* In sendOnesFriendInfo()");
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
			//friendID = friendID.substring(0, friendID.length()-1);
			System.out.print(friendID + "//");
			System.out.println("HereA" + friendID_list);
			friendID_list.add(friendID);
			}
			System.out.println("HereA" + friendID_list);
			Collections.sort(friendID_list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {
			
		}
	
		System.out.println("HereB" + friendID_list);
		//	System.out.println("tuplecount: " + db_person.person_tuplecount);
		for(int tuple_idx=0;tuple_idx<db_person.person_tuplecount;tuple_idx+=1)
		{
		//	System.out.println("tuple_idx: " + tuple_idx);
		//	System.out.println("friendID_list.get(friendID_list_idx): " + friendID_list.get(friendID_list_idx));
			if(db_person.personTable.get(tuple_idx)[0].equals(friendID_list.get(friendID_list_idx)))
			{
				System.out.println("friendID_list_idx: " + friendID_list_idx);
				try 
				{
					for(int j=0;j<6;j+=1)
					{
						if(j==passwordField.ordinal())
							continue;
						out.writeUTF((String)db_person.personTable.get(tuple_idx)[j]);
					}
				}catch (IOException e) 
				{	// TODO Auto-generated catch block
					e.printStackTrace();
				}
				friendID_list_idx+=1;
				if(friendID_list.size() == friendID_list_idx)
					break;
			}
		}
		System.out.println("here!!");
		try {
			out.writeUTF("#"); 
		}
		catch(Exception e) {}
		finally{}// 다보냈다는 FLAG 신호
		/*String path;
		BufferedReader reader;
		path = new String(workspace);
		try {
			path = path.concat(content);
			path = path.concat("_Friends.txt");
			System.out.println(content);
			reader = new BufferedReader(new FileReader(path));
	 
			String line;
			while(true)
			{
			line = reader.readLine();
			if(line==null)
			{
				out.writeUTF(".");
				break;
			}//System.out.println(line);
			out.writeUTF(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}*/
	}
	private void saveAndSendClientInfo(DB_Person db_person, String tryingAddID) {
		String clientID;
		boolean idfound=false;
		StringTokenizer st = new StringTokenizer(tryingAddID,".");
		
		tryingAddID = st.nextToken();
		clientID = st.nextToken();

		for(int tuple_idx=0;tuple_idx<db_person.person_tuplecount;tuple_idx+=1)
		{
			if(db_person.personTable.get(tuple_idx)[0].equals(tryingAddID))
			{
				idfound = true;
				saveClientIDTo_Friendstxt(clientID,tryingAddID);
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
					out.writeUTF(".");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	private void saveClientIDTo_Friendstxt( String clientID,String tryingAddID) {
		String path;
		BufferedWriter writer;
		
		path = workspace;
		path = path.concat(clientID);
		path = path.concat("_Friends.txt");
		System.out.println(path);
//	System.out.println(content);
		try {
			writer = new BufferedWriter(new FileWriter(path, true));
			writer.write(tryingAddID+"\n");
			writer.flush();
			writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
					catch(Exception e)
					{
						break;
					}
					
				//	System.out.println("Message: " + message);
					reply(message);
				}
		}
		catch(Exception e) { e.printStackTrace();}
		finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
	}
}