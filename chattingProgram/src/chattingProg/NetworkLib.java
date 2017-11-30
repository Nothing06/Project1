package chattingProg;

import java.awt.event.WindowAdapter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public  class NetworkLib {

	public static String serverIp = "10.0.30.38";
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
				in = null;
				out = null;
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
	public boolean getFriendInfoFromNetwork(String loginID,String friendId,ArrayList<Object> list) throws IOException {

		boolean idExist = true;
//		Object[] searchIdInfo = new Object[6];
		String searchId = "A";
		searchId = searchId.concat(friendId);
		searchId = searchId.concat(".");
		searchId = searchId.concat(loginID);

		out.writeUTF(searchId); 
		
		for (int j = 0; j < 6; j += 1) {
			Object searchIdInfo = in.readUTF();
			
			if (searchIdInfo .equals("null")) {
				idExist = false;
			}
			else
			{
				list.add(searchIdInfo);
			}
		}
		return idExist;
	}
	boolean loadfriendlist(DefaultListModel dlm, int friend_cnt) {
		boolean t = false;
		String friendId = null;
		String message = null;
		try {
			message = "C";
			message = message.concat(loginID);
			out.writeUTF(message);

			while (true) {
				friendId = in.readUTF();
				if (friendId.equals(".")) {
					t = true;
					break;
				}
				// System.out.println(friendId);
				dlm.addElement(friendId);
				friend_cnt += 1;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t;
	}
}
