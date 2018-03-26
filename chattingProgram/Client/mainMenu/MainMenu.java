package mainMenu;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import loginMenu.LoginWindow;
import utility.NetworkLib;




public class MainMenu extends JFrame implements ActionListener{
	JPanel utilPanel = new JPanel();
	JTabbedPane tabPane = new JTabbedPane(JTabbedPane.TOP);
	JPanel parent = new JPanel();
	JLabel friend;
	JLabel chatting;
	JLabel setting;
	FriendTab friendTab;
	
	SettingTab settingTab;
	ChattingTab chattingTab;
	// public static Thread sender = new Thread(new clientSender());
	// public static Thread receiver = new Thread(new clientReceiver());
//	public static Socket socket;
	private String loginID;
	NetworkLib networkLib;
	
	
	public MainMenu(NetworkLib networkLib, String ID) {
		this.loginID = ID;
		this.networkLib = networkLib;
	//	System.out.println("mainMenu: "+ networkLib);
		setSize(700, 500);
		// getContentPane().setLayout(cards);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// String serverIp = "192.168.0.6";
		// sender.start();
		// receiver.start();
		// setResizable(false);
		
		
		buildLabel();
		buildPanel();
		buildTab();
		setVisible(true);

		java.awt.event.WindowAdapter windowAdapter = networkLib.getAdapter();
		addWindowListener(windowAdapter);
		

	}
	public ChattingTab getChattingTab()
	{
		return this.chattingTab;
	}
	public FriendTab getFriendTab()
	{
		return this.friendTab;
	}
	public SettingTab getSettingTab()
	{
		return this.settingTab;
	}
	private void buildTab() {
		
		tabPane.addTab("친구", friendTab);
		tabPane.addTab("채팅", chattingTab);
		tabPane.addTab("설정", settingTab);
		
		add(tabPane);
		
	}

	private void buildPanel() {
	//	System.out.println("buildPanel()::loginID : " + loginID);
		chattingTab = new ChattingTab(this,networkLib, loginID);
		friendTab = new FriendTab(this,networkLib, loginID);
		settingTab = new SettingTab(this,networkLib, loginID);
		friendTab.setPreferredSize(new Dimension(250, 50));
		chattingTab.setPreferredSize(new Dimension(250, 50));
		settingTab.setPreferredSize(new Dimension(250, 50));
		

		// friendPanel.add(friend);
		// chattingPanel.add(chatting);
		// settingPanel.add(setting);
	}

	private void buildLabel() {
		friend = new JLabel("친구");
		friend.setPreferredSize(new Dimension(250, 50));
		chatting = new JLabel("채팅");
		chatting.setPreferredSize(new Dimension(150, 50));
		setting = new JLabel("설정");
		setting.setPreferredSize(new Dimension(150, 50));
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public void actionPerformed(ActionEvent e) {
//		if(e.getSource() == settingTab.get) {
//			LoginWindow loginWindow = new LoginWindow();
//			this.dispose();
//
//			loginWindow.revalidate();
//		}
//	}
}