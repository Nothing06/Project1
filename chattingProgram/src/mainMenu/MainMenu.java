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
	FriendPanel friendPanel;
	JPanel chattingPanel;
	JPanel settingPanel;
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
	public FriendPanel getFriendPanel()
	{
		return this.friendPanel;
	}
	private void buildTab() {
		tabPane.addTab("친구", friendPanel);
		tabPane.addTab("채팅", chattingPanel);
		tabPane.addTab("설정", settingPanel);
		add(tabPane);
	}

	private void buildPanel() {
	//	System.out.println("buildPanel()::loginID : " + loginID);
		friendPanel = new FriendPanel(networkLib, loginID);
		chattingPanel = new chattingPanel(networkLib, loginID);
		settingPanel = new SettingPanel();
		friendPanel.setPreferredSize(new Dimension(250, 50));
		chattingPanel.setPreferredSize(new Dimension(150, 50));
		settingPanel.setPreferredSize(new Dimension(150, 50));
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
/*
class friendInfoDialog extends JFrame implements ActionListener {

	JLabel ID_label;
	JLabel name_label;
	JLabel tel_label;
	JLabel age_label;
	JPanel friendInfoPanel = new JPanel();
	JButton talkBtn;
	JButton sendFileBtn;
	NetworkLib networkLib;
	Font f = new Font("바탕", Font.ITALIC, 25);
	String talkCompanion;
	HashMap<String,TalkWindow> talkList;
	friendInfoDialog(String[] friendInfoTuple, NetworkLib networkLib, 
			HashMap<String,TalkWindow> talkList, ArrayList<String> friendID_list)
	{
		this.networkLib = networkLib;
		this.talkList = talkList;
		talkCompanion = friendInfoTuple[0];
		friendInfoPanel.setLayout(new BoxLayout(friendInfoPanel, BoxLayout.Y_AXIS));
		ID_label = new JLabel("아이디 : " + friendInfoTuple[0]);
		ID_label.setFont(f);
		name_label = new JLabel("이름 : " + friendInfoTuple[3]);
		name_label.setFont(f);
		age_label = new JLabel("나이 : " + friendInfoTuple[4]);
		age_label.setFont(f);
		tel_label = new JLabel("전화번호: " + friendInfoTuple[5]);
		tel_label.setFont(f);
		talkBtn = new JButton("대화하기");
		sendFileBtn = new JButton("파일전송");
		friendInfoPanel.add(ID_label);
		friendInfoPanel.add(name_label);
		friendInfoPanel.add(age_label);
		friendInfoPanel.add(tel_label);
		friendInfoPanel.add(talkBtn);
		talkBtn.addActionListener(this);
		sendFileBtn.addActionListener(this);
		getContentPane().add(friendInfoPanel);
		setSize(400,400);
		setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == talkBtn)
		{
			
			TalkWindow talkDialog=null;
			
			if(talkList.get(talkCompanion)==null)
			{
				this.dispose();
				talkDialog = new TalkWindow(networkLib, talkCompanion, talkList);
				talkList.put(talkCompanion, talkDialog);
				
			}
			else
			{
				TalkWindow tmp  =  talkList.get(talkCompanion);
				tmp.requestFocus();
			}
		}
		else if(e.getSource() == sendFileBtn)
		{
			 JFileChooser chooser = new JFileChooser();
             FileNameExtensionFilter filter = new FileNameExtensionFilter(
                 "JPG & GIF Images", "jpg", "gif");  //description,......확장자
            chooser.setFileFilter(filter);    //필터 셋팅
            int returnVal = chooser.showOpenDialog(this);
             if(returnVal == JFileChooser.APPROVE_OPTION) {
                System.out.println("You chose to open this file: " +
                     chooser.getSelectedFile(). getName());
             }
             networkLib.sendFile(chooser.getSelectedFile().getAbsolutePath(),talkCompanion);
		}
	}
	
}*/
/*
//2017.12.05  14시40분    TalkDialog 작성시작(오늘의  코딩시작)
class TalkWindow extends JFrame implements ActionListener {
	
	JButton sendBtn;
//	JTextArea messageArea;
	JTextArea messageInput;
	NetworkLib networkLib;
	JScrollPane chatMessageScroll;
	JScrollPane messageInput_scroll;
	JPanel textPanel = new JPanel();
	JPanel buttonPanel = new JPanel();
	JPanel inputPanel = new JPanel();
	Font textFont = new Font("돋움", Font.BOLD, 18);
	String talkCompanion;
	String messageString="";
	JPanel chatMessagePanel = new JPanel();
	ArrayList<JLabel> textlist = new ArrayList<>();
	HashMap<String, TalkWindow> talkList;
	WindowAdapter windowCloseEvent() {
		return new java.awt.event.WindowAdapter() {
			public void windowClosed(java.awt.event.WindowEvent evt) {
				try {
					talkList.remove(talkCompanion);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				
			}
		};
	}
	TalkWindow(NetworkLib networkLib,  String talkCompanion , HashMap<String,TalkWindow> talkList)
	{
		this.networkLib = networkLib;
		this.talkCompanion = talkCompanion;
		this.talkList = talkList;
	//	this.networkLib.openListeningService(talkCompanion);
		java.awt.event.WindowAdapter windowAdapter = windowCloseEvent();
		addWindowListener(windowAdapter);
		
		setLayout(new BorderLayout() );
		
		makeChatMessagePanel();
		makeInputPanel();
		
		add(chatMessageScroll, BorderLayout.CENTER);
		add(inputPanel, BorderLayout.SOUTH);
		setSize(500,550);
		setVisible(true);
		networkLib.addTalkWindow(talkCompanion, this);
	}
	void makeChatMessagePanel()
	{
		chatMessagePanel.setLayout(new BoxLayout(chatMessagePanel, BoxLayout.Y_AXIS));
		messageString = "   *****  [" + talkCompanion + "]" + "님과의 대화시작   *****\n";
		JLabel text = new JLabel(messageString);
		text.setFont(textFont);
		textlist.add(text);
		chatMessagePanel.add(text);
		chatMessageScroll = new JScrollPane(chatMessagePanel);
	}
	void makeInputPanel()
	{
		inputPanel.setLayout(new BorderLayout());
		sendBtn = new JButton("Send");
		sendBtn.addActionListener(this);
		buttonPanel.add(sendBtn);
		messageInput = new JTextArea();
		messageInput_scroll = new JScrollPane(messageInput);
		messageInput_scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		messageInput_scroll.setPreferredSize(new Dimension(300,50));
		textPanel.add(messageInput_scroll);
		inputPanel.add(textPanel, BorderLayout.CENTER);
		inputPanel.add(buttonPanel, BorderLayout.EAST);
	}
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == sendBtn)
		{
			String message = uploadInputMessage();
			if(!message.equals(""))
				networkLib.sendChatMessage(message,talkCompanion);
		}
	}
	void deliverNewMessage(String message)
	{
		JLabel input = new JLabel("["+talkCompanion + "] "+ message);
		input.setFont(textFont);
		textlist.add(input);
		chatMessagePanel.add(input);
		chatMessagePanel.revalidate();
	}
	String uploadInputMessage() {
		String message = "";
		String me = "Me: ";
		if(messageInput.getText().equals(""))
		{
			return "";
		}
		message = message.concat( messageInput.getText()+"\n");
		JLabel input = new JLabel(me + message);
		input.setFont(textFont);
		textlist.add(input);
		chatMessagePanel.add(input);
		chatMessagePanel.revalidate();
		messageInput.setText("");
		return message;
	}
	

}*/