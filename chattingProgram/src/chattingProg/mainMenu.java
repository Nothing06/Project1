package chattingProg;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
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

class friendPanel extends JPanel implements ListSelectionListener, ActionListener{
	JList friendlist;
	Border border;// = BorderFactory.createTitledBorder("친구목록");
	JLabel welcomeLabel;
	JScrollPane scroll = new JScrollPane();
	Font welcomeLabelFont= new Font("Serif", Font.PLAIN, 20);
	Font friendlistFont = new Font("돋움", Font.BOLD, 17);
//new Font("바탕", Font.BOLD, 20);
	JButton addFriendBtn = new JButton("친구추가");
	JButton delFriendBtn = new JButton("친구삭제");
	int friend_cnt = 0;
	private DefaultListModel model;// = new Object[5000];
	JLabel label;
	public String tupleInfo = null; 
	String loginID;
	NetworkLib networkLib;
	ArrayList<JFrame> JFrame_childs = new ArrayList<>();
	ArrayList<String[]> friendInfoTuple_list = new ArrayList<>();
	HashMap<String,TalkDialog> talkList = new HashMap<>();
	
	friendPanel(NetworkLib networkLib,String ID) {
		this.networkLib = networkLib;
		this.loginID = ID;
		welcomeLabel = new JLabel(loginID + "님, weChat에 오신것을 환영합니다.");
		welcomeLabel.setFont(welcomeLabelFont);
		
		model = new DefaultListModel<String>();
		networkLib.loadfriendInfoFromServer(friendInfoTuple_list,model);
		
		friendlist = new JList(model);
		friendlist.setFixedCellHeight(40);
		friendlist.setFixedCellWidth(300);
		friendlist.setFont(friendlistFont);
		
		
		 MouseListener friendlistListener = new MouseAdapter() {
		      public void mouseClicked(MouseEvent mouseEvent) {
		        JList list = (JList) mouseEvent.getSource();
		        if (mouseEvent.getClickCount() == 2) {
		          int index = list.locationToIndex(mouseEvent.getPoint());
		          if (index >= 0) {
		        	  if(JFrame_childs.size() >= 1)
		        	  {
		        		  JFrame_childs.get(0).dispose();
		        		  JFrame_childs.remove(0);
		        	  }
		  			friendInfoDialog f = new friendInfoDialog(friendInfoTuple_list.get(index), networkLib,talkList);
		  			JFrame_childs.add(f);
		  			
		          }
		          
		        }
		      }
		    };

		friendlist.addMouseListener(friendlistListener);
		//friendlist.setPreferredSize(new Dimension(500, 550));
		scroll.setViewportView(friendlist);
		border = BorderFactory.createTitledBorder("친구목록" + "(" + friendInfoTuple_list.size() + ")");
		scroll.setBorder(border); // 경계 설정
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // 가로바정책
		// list 셋팅
		// friendlist = new JList(listStr);
		friendlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// 리스트의 데이터가 될 목록 설정
		friendlist.addListSelectionListener(this); // 이벤트리스너 장착
		addFriendBtn.addActionListener(this);
		delFriendBtn.addActionListener(this);
		add(welcomeLabel, BorderLayout.NORTH);
		add(scroll, BorderLayout.CENTER);
		add(addFriendBtn, BorderLayout.SOUTH);
		setSize(400, 500);
	}
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		//	arg0.
	}

	boolean checkAlreadyFriend(String s) {
		int i = 0;
		boolean t = false;
	//	System.out.println("friend_cnt: " + friend_cnt);
		JOptionPane.showInputDialog(friend_cnt);
		for (i = 0; i < friendInfoTuple_list.size(); i += 1) {
	//		if (model.getElementAt(i).equals(s)) {
			JOptionPane.showInputDialog(friendInfoTuple_list.get(i)[0]);
			if(friendInfoTuple_list.get(i)[0].equals(s)) {
				t = true;
				break;
			}
		}
		return t;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub 
		if(e.getSource() == addFriendBtn)
		{
			String friendId = JOptionPane.showInputDialog("친구 아이디 입력: ");
	
			if (friendId == null)
				return;
	
			if (friendId.equals(loginID)) {
				JOptionPane.showInputDialog("자기자신의 아이디는 추가할수 없습니다.");
				return;
			} else if (friendId.equals("")) {
				return;
			}
			if (checkAlreadyFriend(friendId)) {
				JOptionPane.showInputDialog("이미 친구입니다.");
				return;
			}
			// System.out.println(friendId);
			fillUpModelFromServer( friendId);
		}
		else if(e.getSource() == delFriendBtn)
		{
			JOptionPane.showConfirmDialog(this,"정말 삭제?");
		}
	}

	private void fillUpModelFromServer( String friendId) {
		String[] friendInfoTuple = new String[6];
		try {
		
			boolean idExist = networkLib.getClientInfoFromServer(loginID,friendId,friendInfoTuple
																			);
			if (idExist == true) {
			//	if(!checkAlreadyFriend(friendId))
				{
					model.addElement(friendInfoTuple[0]);
					friendInfoTuple_list.add(friendInfoTuple);
					border = BorderFactory.createTitledBorder("친구목록" + "(" + friendInfoTuple_list.size() + ")");
					scroll.setBorder(border); // 경계 설정
			//		friend_cnt += 1;
				}
			} else {
				JOptionPane.showInputDialog("입력하신 아이디는 존재하지 않습니다.");
			}
	
					
			//	System.out.println(list.get(j));
		} catch (Exception e) {

		}
	}
	

}

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

class settingPanel extends JPanel {

}

public class mainMenu extends JFrame {
	JTabbedPane tabPane = new JTabbedPane(JTabbedPane.TOP);
	JLabel friend;
	JLabel chatting;
	JLabel setting;
	JPanel friendPanel;
	JPanel chattingPanel;
	JPanel settingPanel;
	// public static Thread sender = new Thread(new clientSender());
	// public static Thread receiver = new Thread(new clientReceiver());
//	public static Socket socket;
	private String loginID;
	NetworkLib networkLib;
	
	
	public mainMenu(NetworkLib networkLib, String ID) {
		loginID = ID;
		this.networkLib = networkLib;
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

	private void buildTab() {
		tabPane.addTab("친구", friendPanel);
		tabPane.addTab("채팅", chattingPanel);
		tabPane.addTab("설정", settingPanel);
		add(tabPane);
	}

	private void buildPanel() {
		friendPanel = new friendPanel(networkLib, loginID);
		chattingPanel = new chattingPanel(networkLib, loginID);
		settingPanel = new settingPanel();
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
class friendInfoDialog extends JFrame implements ActionListener {

	JLabel ID_label;
	JLabel name_label;
	JLabel tel_label;
	JLabel age_label;
	JPanel friendInfoPanel = new JPanel();
	JButton talkBtn;
	NetworkLib networkLib;
	Font f = new Font("바탕", Font.ITALIC, 25);
	String talkCompanion;
	HashMap<String,TalkDialog> talkList;
	friendInfoDialog(String[] friendInfoTuple, NetworkLib networkLib,HashMap<String,TalkDialog> talkList)
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
		friendInfoPanel.add(ID_label);
		friendInfoPanel.add(name_label);
		friendInfoPanel.add(age_label);
		friendInfoPanel.add(tel_label);
		friendInfoPanel.add(talkBtn);
		talkBtn.addActionListener(this);
		getContentPane().add(friendInfoPanel);
		setSize(400,400);
		setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == talkBtn)
		{
			
			TalkDialog talkDialog=null;
			
			if(talkList.get(talkCompanion)==null)
			{
				this.dispose();
				talkDialog = new TalkDialog(networkLib, talkCompanion);
				talkList.put(talkCompanion, talkDialog);
				
			}
			else
			{
				TalkDialog tmp  =  talkList.get(talkCompanion);
				tmp.requestFocus();
			}
		}
	}
	
}
//2017.12.05  14시40분    TalkDialog 작성시작(오늘의  코딩시작)
class TalkDialog extends JFrame implements ActionListener {
	
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
	TalkDialog(NetworkLib networkLib,  String talkCompanion )
	{
		this.networkLib = networkLib;
		this.talkCompanion = talkCompanion;
	//	this.networkLib.openListeningService(talkCompanion);
		
		setLayout(new BorderLayout() );
		
		makeChatMessagePanel();
		makeInputPanel();
		
		add(chatMessageScroll, BorderLayout.CENTER);
		add(inputPanel, BorderLayout.SOUTH);
		setSize(500,550);
		setVisible(true);
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
	private String uploadInputMessage() {
		String message = "Me: ";
		if(messageInput.getText().equals(""))
		{
			return "";
		}
		message = message.concat( messageInput.getText()+"\n");
		JLabel input = new JLabel(message);
		input.setFont(textFont);
		textlist.add(input);
		chatMessagePanel.add(input);
		chatMessagePanel.revalidate();
		messageInput.setText("");
		return message;
	}
	

}