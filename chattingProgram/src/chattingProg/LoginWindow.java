package chattingProg;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginWindow extends JFrame implements ActionListener{
	JPanel img_panel;
	ImageIcon loginImage;
	JPanel info_panel;
	NetworkLib networkLib=null;
	// info_panel 내부 컨테이너들
	JPanel input_panel;
	JPanel button_panel;
	JLabel img_label;
	JLabel id_label;
	JLabel password_label;
	JTextField loginID_input;
	JPasswordField password_input;
	JButton loginButton;

	JButton regButton;
	JPanel bottom_panel;
	public String loginID;
	String loginPassword;
	String serverIp = "192.168.0.6";
	registerDialog regDialog;
	public registerContent regContent;

	public LoginWindow(NetworkLib networkLib) {
		this.networkLib = networkLib;
	//	networkLib.start();
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		makeImagePanel();
		makeInfoPanel();
		add(img_panel);
		add(info_panel);
		setSize(500, 550);
		this.setResizable(false);
		this.setTitle("WeChat");
		// setBounds(300, 300, 250, 100);
		setVisible(true);
	}

	void makeImagePanel() {
		loginImage = new ImageIcon("C:\\Users\\user\\Pictures\\wechat.png");
		img_label = new JLabel(loginImage);
		img_panel = new JPanel();
		img_panel.add(img_label);
	}

	void makeInputPanel() {
		Font f = new Font("바탕", Font.BOLD, 20);
		input_panel = new JPanel();
		input_panel.setLayout(new GridLayout(2, 2));
		id_label = new JLabel("ID");
		password_label = new JLabel("Password");
		loginID_input = new JTextField();
		loginID_input.setFont(f);
		password_input = new JPasswordField();
		password_input.setFont(f);
		input_panel.add(id_label);
		input_panel.add(loginID_input);
		input_panel.add(password_label);
		input_panel.add(password_input);
	}

	void makeButton() {
		button_panel = new JPanel();
		button_panel.setLayout(new BorderLayout());
		loginButton = new JButton("로그인");
		loginButton.addActionListener(this);
		button_panel.add(loginButton, BorderLayout.CENTER);
	}

	void makeBottomPart() {
		bottom_panel = new JPanel();
		bottom_panel.setLayout(new FlowLayout());
		JLabel l = new JLabel("안녕하세요 weChat입니다.  아이디가 없으신가요?  ");
		regButton = new JButton("회원가입");
		regButton.addActionListener(this);
		bottom_panel.add(l);
		bottom_panel.add(regButton);
	}

	void makeInfoPanel() {
		info_panel = new JPanel();
		info_panel.setLayout(new BorderLayout());
		makeInputPanel();
		info_panel.add(input_panel, BorderLayout.CENTER);
		makeButton();
		info_panel.add(button_panel, BorderLayout.EAST);
		makeBottomPart();
		info_panel.add(bottom_panel, BorderLayout.SOUTH);
	}

	

	void registerProcess() {
		// JOptionPane.showInputDialog
		regContent = new registerContent();
		regDialog = new registerDialog(this, regContent, "회원가입");
		regDialog.setVisible(true);
		// sendRegisteredClientInfo();
	}

	public void actionPerformed(ActionEvent e) {

		
		
		if (e.getSource() == loginButton) {
			loginID = loginID_input.getText();
			if (loginID.equals("")) {
				JOptionPane.showInputDialog("아이디를 입력해주세요");
				return  ;
			} 
			loginPassword = new String(password_input.getPassword());
			JOptionPane.showInputDialog(loginPassword);
			if(loginPassword.equals(""))
			{
				JOptionPane.showInputDialog("비밀번호를 입력해주세요");
				return  ;
			}
			Application.networkLib.loginID= loginID;
			Application.networkLib.start();
			try {
				Application.networkLib.sendLoginPacket(loginID, loginPassword);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} 
		else if (e.getSource() == regButton) {
			networkLib.start();
			registerProcess();
		}
	}

	boolean sendJoinedMemberInfo() {
		boolean t = false;
		Socket temp_Socket;
		DataOutputStream temp_out;
		try {
			temp_Socket = new Socket(networkLib.serverIp, 8000);
			temp_out = new DataOutputStream(temp_Socket.getOutputStream());

			temp_out.writeUTF("D");
			temp_out.writeUTF(regContent.regID);
			temp_out.writeUTF(regContent.regPassword);
			temp_out.writeUTF(regContent.name);
			temp_out.writeUTF(regContent.age);
			temp_out.writeUTF(regContent.tel);

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			temp_Socket = null;
			temp_out = null;

		}

		return t;
	}
/*boolean try_login() {
		boolean t = false;
		try {
			serverIp = "10.0.25.42";
			socket = new Socket(serverIp, 8000);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			loginID = id.getText();
			if (loginID.equals("")) {
				JOptionPane.showInputDialog("아이디를 입력해주세요");
				return false;
			} 
			
			NetworkLib.sendLoginPacket(loginID, new String(password.getPassword()));

			String isvalid = in.readUTF(); // 0 or 1
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
	}*/

}

class registerContent {
	String regID;
	String regPassword;
	String name;
	String age;
	String tel;
}

class registerDialog extends JDialog implements ActionListener {
	JPanel reg_input;
	JPanel[] items = new JPanel[6];
	int i = 0;
	final int item_cnt = 5;
	String[] str_items = { "아이디 ", "비밀번호 ", "이름 ", "나이 ", "연락처 " };
	JButton regBtn = new JButton("등록");
	JTextField[] txtArr = new JTextField[5];
	JFrame parent;
	registerContent rc;
	InputChecker inputChecker;
	
	public registerDialog(JFrame frame, registerContent rc, String title) {
		
		super(frame, title);
		parent  =  frame;
		this.rc = rc;
		inputChecker = new InputChecker();
		reg_input = new JPanel();
		reg_input.setLayout(new BoxLayout(reg_input, BoxLayout.Y_AXIS));
		for (i = 0; i < item_cnt; i += 1) {
			items[i] = new JPanel();
			items[i].setLayout(new FlowLayout());
			items[i].add(new JLabel(str_items[i]));
			txtArr[i] = new JTextField(25);
			txtArr[i].setEditable(true);
			items[i].add(txtArr[i]);
			reg_input.add(items[i]);
		}
		items[5] = new JPanel();
		items[5].add(regBtn);
		reg_input.add(items[5]);

		regBtn.addActionListener(this);
		getContentPane().add(reg_input);
		setSize(400, 500);
	}
	boolean getJoinInputInfo_Ok()
	{
		rc.regID = txtArr[0].getText();
		if(!inputChecker.checkID(rc.regID))
		{
			JOptionPane.showInputDialog("아이디는 4글자 이상이여야하며, '.'은 들어갈수없습니다.");
			return false;
		}
		rc.regPassword = txtArr[1].getText();
		if(!inputChecker.checkPassword(rc.regPassword))
		{
			JOptionPane.showInputDialog("비밀번호는 7글자 이상이여야합니다.");
			return false;
		}
		rc.name = txtArr[2].getText();
		if(!inputChecker.checkName(rc.name))
		{
			JOptionPane.showInputDialog("이름은 2글자 이상이여야 합니다.");
			return false;
		}
		rc.age = (txtArr[3].getText());
		if(!inputChecker.checkAge(rc.age))
		{
			JOptionPane.showInputDialog("나이는 1살이상이여야 합니다.");
			return false;
		}
		rc.tel = txtArr[4].getText();
		if(!inputChecker.checkPhoneNumber(rc.tel))
		{
			JOptionPane.showInputDialog("핸드폰번호를 입력해주세요.(예 : 010-1234-5678)");
			return false;
		}
		return true;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		if(e.getSource() == regBtn)
		{
			if(getJoinInputInfo_Ok())
			{
				((LoginWindow) parent).sendJoinedMemberInfo();
				this.dispose();
			}
		}
		
	}
}
