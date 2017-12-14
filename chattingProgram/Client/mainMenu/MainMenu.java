package mainMenu;

import java.awt.BorderLayout;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import utility.NetworkLib;

class chattingPanel extends JPanel {
	JList chatlist;
	Border border = BorderFactory.createTitledBorder("대화내용");
	JScrollPane scroll = new JScrollPane();
	private DefaultListModel model;// = new Object[5000];
	JLabel label;
	public String tupleInfo = null; 
	String loginID;
	NetworkLib networkLib;
	
	chattingPanel(NetworkLib networkLib,String ID) {
		this.networkLib = networkLib;
		this.loginID = ID;
		model = new DefaultListModel();
		
	}
}


public class MainMenu extends JFrame {
	JTabbedPane tabPane = new JTabbedPane(JTabbedPane.TOP);
	JLabel friend;
	JLabel chatting;
	JLabel setting;
	FriendTab friendTab;
	SettingTab settingTab;
	JPanel chattingTab;
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
		friendTab = new FriendTab(networkLib, loginID);
		chattingTab = new chattingPanel(networkLib, loginID);
		settingTab = new SettingTab(networkLib, loginID);
		friendTab.setPreferredSize(new Dimension(250, 50));
		chattingTab.setPreferredSize(new Dimension(150, 50));
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

	
}