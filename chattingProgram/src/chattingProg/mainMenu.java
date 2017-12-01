package chattingProg;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class friendPanel extends JPanel implements ListSelectionListener, ActionListener {
	JList friendlist;
	Border border = BorderFactory.createTitledBorder("친구목록");
	JScrollPane scroll = new JScrollPane();
	JButton addFriendBtn = new JButton("친구추가");
	static int friend_cnt = 0;
	private DefaultListModel model;// = new Object[5000];
	JLabel label;
	public String tupleInfo = null; 
	String loginID;
	NetworkLib networkLib;
	friendPanel(NetworkLib networkLib,String ID) {
		this.networkLib = networkLib;
		this.loginID = ID;
		model = new DefaultListModel();
		networkLib.loadfriendlist(model,friend_cnt);
		friendlist = new JList(model);
		friendlist.setPreferredSize(new Dimension(500, 550));
		scroll.setViewportView(friendlist);
		scroll.setBorder(border); // 경계 설정
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // 가로바정책
		// list 셋팅
		// friendlist = new JList(listStr);
		friendlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// 리스트의 데이터가 될 목록 설정
		friendlist.addListSelectionListener(this); // 이벤트리스너 장착
		addFriendBtn.addActionListener(this);

		add(scroll, BorderLayout.CENTER);
		add(addFriendBtn, BorderLayout.SOUTH);
		setSize(400, 500);
	}
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub

	}

	boolean checkAlreadyFriend(String s) {
		int i = 0;
		boolean t = false;
		for (i = 0; i < friend_cnt; i += 1) {
			if (model.getElementAt(i).equals(s)) {
				t = true;
			}
		}
		return t;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub 
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

	private void fillUpModelFromServer( String friendId) {
		
		try {
			ArrayList<Object> list = new ArrayList<Object> ();
		
			boolean idExist = networkLib.getFriendInfoFromNetwork(loginID,friendId,list);
			if (idExist == true) {
				model.addElement(list.get(0));
				friend_cnt += 1;
			} else {
				JOptionPane.showInputDialog("입력하신 아이디는 존재하지 않습니다.");
			}

			System.out.println("* friendInfo");
			for (int j = 0; j < 6; j += 1)
				System.out.println(list.get(j));
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
