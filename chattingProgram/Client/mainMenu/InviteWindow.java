package mainMenu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import utility.NetworkLib;

public class InviteWindow extends JFrame implements ActionListener{
	NetworkLib networkLib;
	JPanel panel;
	JButton confirmBtn;
	JRadioButton[] bInvite;
	ButtonGroup group = new ButtonGroup();
	JPanel friendPanel[];
	JLabel friendID[];
	int friendCnt=0;
	FriendTab friendTab =null;
	ChattingTab chattingTab = null;
	String from=null;
	ArrayList<String> friendIDList;
	ArrayList<String> talkCompanions;
	ArrayList<String[]> friendInfoList;
	String fontname[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
	Font f = new Font(fontname[(int) (Math.random()*fontname.length)], Font.PLAIN, 20);
	JScrollPane scroll;
	public InviteWindow(MainMenu mainMenu,NetworkLib networkLib)
	{
		this.networkLib = networkLib;
		this.friendTab = mainMenu.getFriendTab();
		this.chattingTab = mainMenu.getChattingTab();
		this.friendIDList = friendTab.friendID_list;
		this.friendInfoList = friendTab.friendInfoList;
		from = networkLib.loginID;
		
		scroll = new JScrollPane();
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // 가로바정책
		
		getContentPane().setLayout(new BorderLayout());
		talkCompanions = new ArrayList<String>();
//		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // 가로바정책
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		scroll.setViewportView(panel);
		
		friendCnt = mainMenu.getFriendTab().friendInfoList.size();
		friendPanel = new JPanel[friendCnt];
		bInvite = new JRadioButton[friendCnt];
		friendID = new JLabel[friendCnt];
		confirmBtn = new JButton("초대");
		confirmBtn.addActionListener(this);
	//	JOptionPane.showInputDialog("friendCnt: " + friendCnt);
		for(int i=0;i<friendCnt;i+=1)
		{
			friendPanel[i] = new JPanel();
			bInvite[i] = new JRadioButton();
			bInvite[i].setPreferredSize(new Dimension(100, 200));
			friendID[i] = new JLabel(friendInfoList.get(i)[0]);
			friendID[i].setFont(f);
			friendPanel[i].add(friendID[i]);
			friendPanel[i].add(bInvite[i]);
		//	JOptionPane.showInputDialog("friendList: " + friendInfoList.get(i)[0]);
			panel.add(friendPanel[i]);
		}
	//	scroll.add(panel);
		getContentPane().add(scroll,BorderLayout.CENTER);
		getContentPane().add(confirmBtn,BorderLayout.SOUTH);
		setSize(500,550);
		setVisible(true);
	}
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == confirmBtn)
		{
			for(int i=0;i<friendCnt;i+=1)
			{
				if(bInvite[i].isSelected())
				{
					talkCompanions.add(friendInfoList.get(i)[0]);
				}
			}
			
			
			TalkWindow t = new TalkWindow(networkLib, talkCompanions);
			networkLib.getChattingRoomHashMap().put(Integer.valueOf(t.getChatRoomNumber()), t);
			t.showLogInvitation(from);
			chattingTab.addChatRoomToList(t);
			this.dispose();
		}
	}
}
