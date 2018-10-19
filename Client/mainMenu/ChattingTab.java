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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import utility.NetworkLib;
import utility.SortedListModel;

public class ChattingTab extends JPanel implements ListSelectionListener, ActionListener  {
	
	NetworkLib networkLib;
	SortedListModel model;
	JButton inviteBtn;
	JList chatRoomList;
	JScrollPane scroll = new JScrollPane();
	Font chatRoomListFont = new Font("돋움", Font.PLAIN, 17);
	Border border;
	MainMenu mainMenu;
	int getRoomNumber(String roomTitle) {
		String roomNumber="";
		
		for(int i=5;i<roomTitle.length();i+=1) {
			if(roomTitle.charAt(i) >= '0' && roomTitle.charAt(i) <= '9')
			{
				roomNumber += roomTitle.charAt(i);
			}
			else
				break;
		}
		return Integer.valueOf(roomNumber);
	}
	TalkWindow getTalkWindow(int chatRoomNumber) {
		return networkLib.getChattingRoomHashMap().get(chatRoomNumber);
	}
			
	public ChattingTab(MainMenu parent,NetworkLib networkLib, String ID)
	{
		this.mainMenu = parent;
		this.networkLib = networkLib;
		model = new SortedListModel();
		chatRoomList = new JList(model);
		inviteBtn = new JButton("초대");
		inviteBtn.addActionListener(this);
		chatRoomList.setFixedCellHeight(40);
		chatRoomList.setFixedCellWidth(300);
		chatRoomList.setFont(chatRoomListFont);

		MouseListener chatRoomListListener = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {

			}
			public void mouseClicked(MouseEvent mouseEvent) {
				JList list = (JList) mouseEvent.getSource();
				if (mouseEvent.getClickCount() == 2) {
			/*		int index = list.locationToIndex(mouseEvent.getPoint());
					String[] friendData = friendInfoTuple_list.get(index); 
					if(friendData  == null)
						return ; 
					if (friendInfoWindowList.size() > 0) {
						friendInfoWindowList.get(0).dispose();
						friendInfoWindowList.remove(0);*/
					String roomTitle = (String)list.getSelectedValue();
					int roomNumber = getRoomNumber(roomTitle);
					TalkWindow t = networkLib.getChattingRoomHashMap().get(roomNumber);
					if(t != null)
						t.setVisible(true);
					}
					
			//		FriendInfo f = new FriendInfo(friendData, networkLib, talkList,
			//				friendID_list);
			//		friendInfoWindowList.add(f);
					
					

				}
			};
		

		chatRoomList.addMouseListener(chatRoomListListener);
		// friendlist.setPreferredSize(new Dimension(500, 550));
		scroll.setViewportView(chatRoomList);
		border = BorderFactory.createTitledBorder("채팅목록");
		scroll.setBorder(border); // 경계 설정
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // 가로바정책
		// list 셋팅
		// friendlist = new JList(listStr);
		chatRoomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// 리스트의 데이터가 될 목록 설정
		chatRoomList.addListSelectionListener(this); // 이벤트리스너 장착
		
		add(inviteBtn, BorderLayout.SOUTH);
		add(scroll, BorderLayout.CENTER);
		setSize(400, 500);
	}
	public void addChatRoomToList(TalkWindow talkWindow) {
		String title="";
		ArrayList<String> roomUserIDList = talkWindow.getRoomUserIDList();
		int chatRoomNumber = talkWindow.getChatRoomNumber();
		
		title+="#Room";
		title+= String.valueOf(chatRoomNumber);
		title+="# ";
		
		for(int i=0;i<roomUserIDList.size();i+=1) {
			title += roomUserIDList.get(i);
			title +="님// ";
		}
		model.add(title);
		networkLib.getChattingRoomHashMap().put(chatRoomNumber, talkWindow);
		//chatRoomList.add(friendInfoTuple);
		
	//	border = BorderFactory.createTitledBorder("친구목록" + "(" + friendInfoTuple_list.size() + ")");
	//	scroll.setBorder(border); // 경계 설정
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == inviteBtn)
		{
			InviteWindow inviteWindow = new InviteWindow(mainMenu, networkLib);
		}
		
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
