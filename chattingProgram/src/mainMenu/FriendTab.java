package mainMenu;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import utility.NetworkLib;
import utility.SortedListModel;
public class FriendTab extends JPanel implements ListSelectionListener, ActionListener{ // JTabbedPane의 친구 항목
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
	SortedListModel model;// = new Object[5000];
	JLabel label;
	public String tupleInfo = null; 
	String loginID;
	NetworkLib networkLib;
	ArrayList<JFrame> friendInfoWindowList = new ArrayList<>();
	ArrayList<String[]> friendInfoTuple_list = new ArrayList<>();
	ArrayList<String> friendID_list = new ArrayList<>();
	HashMap<String,TalkWindow> talkList = new HashMap<>();
	String searchingID=null; 
	
	
	public FriendTab(NetworkLib networkLib,String ID) {
		this.networkLib = networkLib;
		this.loginID = ID;
		welcomeLabel = new JLabel(loginID + "님, weChat에 오신것을 환영합니다.");
		welcomeLabel.setFont(welcomeLabelFont);
		
		
		model = new SortedListModel();
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
		        	  if(friendInfoWindowList .size() > 0)
		        	  {
		        		  friendInfoWindowList .get(0).dispose();
		        		  friendInfoWindowList .remove(0);
		        	  }
		  			FriendInfo f = new FriendInfo(friendInfoTuple_list.get(index), networkLib,
		  													talkList, friendID_list);
		  			friendInfoWindowList .add(f);
		  			
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
	
//		this.networkLib.sendLoginIDToGetFriendList();
	}

	public ArrayList<String[]> getFriendInfoTuple_list()
	{
		return friendInfoTuple_list;
	}
	public ArrayList<String> getFriendID_list()
	{
		return friendID_list;
	}
	public SortedListModel getSortedListModel()
	{
		return model;
	}
	public void setBorder()
	{
		border = BorderFactory.createTitledBorder("친구목록" + "(" + friendInfoTuple_list.size() + ")");
		scroll.setBorder(border);
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
	//	JOptionPane.showInputDialog(friend_cnt);
		for (i = 0; i < friendInfoTuple_list.size(); i += 1) {
	//		if (model.getElementAt(i).equals(s)) {
		//	JOptionPane.showInputDialog(friendInfoTuple_list.get(i)[0]);
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
			String[] friendInfoTuple = new String[6];
			 searchingID = JOptionPane.showInputDialog("친구 아이디 입력: ");
	
			if (searchingID == null)
				return;
	
			if (searchingID.equals(loginID)) {
				JOptionPane.showInputDialog("자기자신의 아이디는 추가할수 없습니다.");
				return;
			} else if (searchingID.equals("")) {
				return;
			}
			if (checkAlreadyFriend(searchingID)) {
				JOptionPane.showInputDialog("이미 친구입니다.");
				return;
			}
		
			networkLib.sendAddFriendPacketToServer(loginID,searchingID,friendInfoTuple);
			// System.out.println(friendId);
		//	fillUpModelFromServer( friendId);
		}
		else if(e.getSource() == delFriendBtn)
		{
			JOptionPane.showConfirmDialog(this,"정말 삭제?");
		}
	}
	void caseAddFriend()
	{
		
	}
	public void addFriendToList( String[] friendInfoTuple) {
			model.add(friendInfoTuple[0]);
			friendInfoTuple_list.add(friendInfoTuple);
			Collections.sort(friendInfoTuple_list, new Comparator<String[]> () {
				@Override
				public int compare(String[] o1, String[] o2) {
					// TODO Auto-generated method stub
					return o1[0].compareToIgnoreCase(o2[0]);
				}
			});
			border = BorderFactory.createTitledBorder("친구목록" + "(" + friendInfoTuple_list.size() + ")");
			scroll.setBorder(border); // 경계 설정
	}
}