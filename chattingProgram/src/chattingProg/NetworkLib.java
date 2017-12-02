package chattingProg;

import java.awt.event.WindowAdapter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
enum EnumPerson{id,password,emp_no,name,age,tel};
public  class NetworkLib {

	public static String serverIp = "192.168.0.6";
	public Socket socket;
	public DataOutputStream out;
	public DataInputStream in;
	private String loginID;
	public NetworkLib(String loginID)
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
	public boolean getClientInfoFromServer(String loginID,String friendId,String[] tuple,
										ArrayList<String[]> list) throws IOException {

		boolean idExist = true;
		EnumPerson passwordField =  EnumPerson.valueOf("password");
//		Object[] searchIdInfo = new Object[6];
		String searchPacket = "A";
		searchPacket = searchPacket.concat(friendId);
		searchPacket = searchPacket.concat(".");
		searchPacket = searchPacket.concat(loginID);

		out.writeUTF(searchPacket); 
		
		for (int j = 0; j < 6; j += 1) {
			if(j==(int)(passwordField.ordinal())) continue; // 
			String searchIdInfo_attribute = in.readUTF();
			
			if (searchIdInfo_attribute.equals(".")) {
				idExist = false;
			}
			else
			{
				tuple[j] = searchIdInfo_attribute;
			}
		}
		list.add(tuple);
		return idExist;
	}
	boolean loadfriendInfoFromServer(ArrayList<String[]> friendInfo_list, DefaultListModel dlm, int friend_cnt) {
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
				//	JOptionPane.showInputDialog(tuple[j]);
					if(tuple[j].equals("#"))
					{
						t=true;
						break;
					}
				//	JOptionPane.showInputDialog("H1");
				}
				if(t==true)
					break;
			//	JOptionPane.showInputDialog("H2");
			//	System.out.println("tuple: " + tuple[0] + " " + tuple[2] + " "+tuple[3] + 
				//									" " + tuple[4] + " " + tuple[5]);
				dlm.addElement(tuple[0]);
				friendInfo_list.add(tuple);
				friend_cnt += 1;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t;
	}
}
