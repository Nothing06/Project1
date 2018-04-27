package loginMenu;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import utility.NetworkLib;

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
	KeyListener keyListener;
	JButton regButton;
	JPanel bottom_panel;
	public static String loginID;
	String loginPassword;
	//String serverIp = "10.0.29.89";
	RegDialog regDialog;
	RegContent regContent;
	public NetworkLib getNetworkLib()
	{
		return networkLib;
	}
	public static String getLoginID() {
		return loginID;
	}
	public LoginWindow() {
		this.networkLib = new NetworkLib(this);
		this.networkLib.includeLoginWindow(this);
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
		loginImage = new ImageIcon("C:\\Users\\user\\Pictures\\wechat2.png");
		
		img_label = new JLabel(loginImage);
		img_label.setSize(500, 400);
		img_panel = new JPanel();
		img_panel.add(img_label);
	}

	void makeInputPanel() {
		 KeyListener keyListener = new KeyListener() {
		      public void keyPressed(KeyEvent keyEvent) {
		    	  switch(keyEvent.getKeyCode())
		    	  {
		    	  case KeyEvent.VK_ENTER:
		    		  	loginButton.doClick();
		    		  	break;
		    	  }
		      }

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		      };

		      
		Font f = new Font("바탕", Font.BOLD, 20);
		input_panel = new JPanel();
		input_panel.setLayout(new GridLayout(2, 2));
		id_label = new JLabel("ID");
		password_label = new JLabel("Password");
		loginID_input = new JTextField();
		loginID_input.setFont(f);
		loginID_input.addKeyListener(keyListener);
		password_input = new JPasswordField();
		password_input.setFont(f);
		password_input.addKeyListener(keyListener);
		
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
		regDialog = new RegDialog(this, "회원가입", networkLib);
		regDialog.setVisible(true);
		// sendRegisteredClientInfo();
	}
	@Override
	public void actionPerformed(ActionEvent e) {

		
		
		if (e.getSource() == loginButton) {
//			loginPerform();
			loginID = loginID_input.getText();
			
			if (loginID.equals("")) {
				JOptionPane.showInputDialog("아이디를 입력해주세요");
				return  ;
			} 
			loginPassword = new String(password_input.getPassword());
		//	JOptionPane.showInputDialog(loginPassword);
			if(loginPassword.equals(""))
			{
				JOptionPane.showInputDialog("비밀번호를 입력해주세요");
				return  ;
			}
			networkLib = new NetworkLib(this);
			networkLib.loginID= loginID;
			networkLib.start();
			try {
				networkLib.sendLoginPacket(loginID, loginPassword);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} 
		else if (e.getSource() == regButton) {
			registerProcess();
			
		}
	}


}


