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

public class LoginWindow extends JFrame implements ActionListener {
	JPanel img_panel;
	ImageIcon loginImage;
	JPanel info_panel;
	NetworkLib networkLib;
	// info_panel ���� �����̳ʵ�
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
	String serverIp = "10.0.25.42";
	registerDialog regDialog;
	public static registerContent regContent;

	public LoginWindow() {
		networkLib = new NetworkLib(loginID);
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
		Font f = new Font("����", Font.BOLD, 20);
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
		loginButton = new JButton("�α���");
		loginButton.addActionListener(this);
		button_panel.add(loginButton, BorderLayout.CENTER);
	}

	void makeBottomPart() {
		bottom_panel = new JPanel();
		bottom_panel.setLayout(new FlowLayout());
		JLabel l = new JLabel("�ȳ��ϼ��� weChat�Դϴ�.  ���̵� �����Ű���?  ");
		regButton = new JButton("ȸ������");
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
		regDialog = new registerDialog(this, "ȸ������");
		regDialog.setVisible(true);
		// sendRegisteredClientInfo();
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == loginButton) {
			JOptionPane.showInputDialog("LoginWindow-ActionPerformed");
			networkLib.loginProcess(this,  loginID_input, password_input);
		} else if (e.getSource() == regButton) {
			registerProcess();
		}
	}

	static boolean sendJoinedMemberInfo() {
		boolean t = false;
		Socket temp_Socket;
		DataOutputStream temp_out;
		try {
			temp_Socket = new Socket(NetworkLib.serverIp, 8000);
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
/*
	boolean try_login() {
		boolean t = false;
		try {
			serverIp = "10.0.25.42";
			socket = new Socket(serverIp, 8000);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			loginID = id.getText();
			if (loginID.equals("")) {
				JOptionPane.showInputDialog("���̵� �Է����ּ���");
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
	String[] str_items = { "���̵� ", "��й�ȣ ", "�̸� ", "���� ", "����ó " };
	JButton regBtn = new JButton("���");
	JTextField[] txtArr = new JTextField[5];

	public registerDialog(JFrame frame, String title) {
		super(frame, title);
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

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		LoginWindow.regContent = new registerContent();

		LoginWindow.regContent.regID = txtArr[0].getText();
		LoginWindow.regContent.regPassword = txtArr[1].getText();
		LoginWindow.regContent.name = txtArr[2].getText();
		LoginWindow.regContent.age = (txtArr[3].getText());
		LoginWindow.regContent.tel = txtArr[4].getText();
		JOptionPane.showInputDialog("hereA");
		LoginWindow.sendJoinedMemberInfo();
		JOptionPane.showInputDialog("hereB");
		this.dispose();

	}
}
