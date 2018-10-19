package mainMenu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import utility.NetworkLib;

//2017.12.05  14시40분    TalkDialog 작성시작(오늘의  코딩시작)
public class TalkWindow extends JFrame implements ActionListener {
	
	JButton sendBtn;
	JButton fileBtn;
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
	boolean isGroup=false;
	int chatRoomNumber=0;
	String roomNum;
	static int chatRoomCnt=0;
	ArrayList<String> roomUserIDList;
	String messageString="";
	JPanel chatMessagePanel = new JPanel();
	ArrayList<JLabel> textlist = new ArrayList<>();
	HashMap<Integer,TalkWindow> chatRoomHashMap = new HashMap();
	HashMap<String, TalkWindow> talkList;
	File file;
	
	WindowAdapter windowCloseEvent() {
		return new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				try {
					if(isGroup==false)
					talkList.remove(talkCompanion);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				
			}
		};
	}
	public ArrayList<String> getRoomUserIDList()
	{
		return roomUserIDList;
	}
	public int getChatRoomNumber() {
		return chatRoomNumber;
	}
	public TalkWindow(NetworkLib networkLib, ArrayList<String> talkCompanions)
	{	//Group대화
		this.networkLib = networkLib;
		this.roomUserIDList = talkCompanions;
		isGroup=true;
		this.chatRoomNumber=chatRoomCnt;
		roomNum = "#ROOM";
		roomNum += String.valueOf(chatRoomNumber+1);
		chatRoomCnt+=1;
	//	this.talkList = talkList;
	//	this.networkLib.openListeningService(talkCompanion);
		java.awt.event.WindowAdapter windowAdapter = windowCloseEvent();
		addWindowListener(windowAdapter);
		
		setLayout(new BorderLayout() );
		
		makeChatMessagePanel(true);
		makeInputPanel();
		
		add(chatMessageScroll, BorderLayout.CENTER);
		add(inputPanel, BorderLayout.SOUTH);
		setTitle(roomNum);
		setSize(500,550);
		setVisible(true);
		networkLib.addTalkWindow(talkCompanions, this);
	}
	public TalkWindow(NetworkLib networkLib,  String talkCompanion , HashMap<String,TalkWindow> talkList)
	{ 	// 1:1대화
		this.networkLib = networkLib;
		this.talkCompanion = talkCompanion;
		this.talkList = talkList;
		isGroup=false;
	//	this.networkLib.openListeningService(talkCompanion);
		java.awt.event.WindowAdapter windowAdapter = windowCloseEvent();
		addWindowListener(windowAdapter);
		
		setLayout(new BorderLayout() );
		
		makeChatMessagePanel(false);
		makeInputPanel();
		
		add(chatMessageScroll, BorderLayout.CENTER);
		add(inputPanel, BorderLayout.SOUTH);
		setTitle(talkCompanion);
		setSize(500,550);
		setVisible(true);
		networkLib.addTalkWindow(talkCompanion, this);
	}
/*	void makeChatMessagePanel(ArrayList<String> talkCompanions) {
		chatMessagePanel.setLayout(new BoxLayout(chatMessagePanel, BoxLayout.Y_AXIS));
		messageString = " * ";
		messageString+= ;
		messageString+= "님이 ";
		for(int i=0;i<talkCompanions.size();i+=1)
		{
			messageString += talkCompanions.get(i);
			messageString+="님,";
		}
		messageString+="을 초대하였습니다.\n";
		JLabel text = new JLabel(messageString);
		text.setFont(textFont);
		textlist.add(text);
		chatMessagePanel.add(text);
		chatMessageScroll = new JScrollPane(chatMessagePanel);
	}*/
	void makeChatMessagePanel(boolean isGroup)
	{
		chatMessagePanel.setLayout(new BoxLayout(chatMessagePanel, BoxLayout.Y_AXIS));
		if(isGroup==false)
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
		fileBtn = new JButton("File");
		fileBtn.addActionListener(this);
		sendBtn = new JButton("Send");
		sendBtn.addActionListener(this);
		
		buttonPanel.add(sendBtn);
		messageInput = new JTextArea();
		messageInput_scroll = new JScrollPane(messageInput);
		messageInput_scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		messageInput_scroll.setPreferredSize(new Dimension(300,50));
		textPanel.add(messageInput_scroll);
		inputPanel.add(fileBtn, BorderLayout.WEST);
		inputPanel.add(textPanel, BorderLayout.CENTER);
		inputPanel.add(buttonPanel, BorderLayout.EAST);
	}
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == sendBtn)
		{
			String message = uploadInputMessage();
			if(!message.equals(""))
			{
				try {
					if(isGroup==false)
						networkLib.sendChatMessage(message,talkCompanion);
					else
						networkLib.sendChatMessage(message,chatRoomNumber, roomUserIDList);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		else if(e.getSource() == fileBtn) {
			
			file = getFile();
			try {
				networkLib.sendReqIP(file, roomUserIDList , false); // file을 보내기 위해  보낼 클라이언트들의 ip주소를 먼저 얻어온다. 얻어온 후  file을 보낸다.  
				//networkLib.sendFile(, roomUserIDList, false);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	public File getFile() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(this);
		File file = fc.getSelectedFile();
		
		return file;
	}
	public void showLogInvitation(String from ) {
		int roomUserCnt=0;
		messageString = " * ";
		messageString+= from ;
		messageString+= "님이 ";
		for(int i=0;i<roomUserIDList.size();i+=1)
		{
			if(!roomUserIDList.get(i).equals(from))
			{
				if(roomUserCnt > 0)
					messageString+=",";
				messageString += roomUserIDList.get(i);
				messageString+="님";
				roomUserCnt+=1;
			}
		}
		messageString+="을 초대하였습니다.";
		
		JLabel input = new JLabel(messageString);
		input.setFont(textFont);
		//textlist.add(input);
		chatMessagePanel.add(input);
		chatMessagePanel.revalidate();
	}
	public void deliverNewMessage(String from,String message)
	{
		JLabel input = new JLabel("["+from + "] "+ message);
		input.setFont(textFont);
		textlist.add(input);
		chatMessagePanel.add(input);
		chatMessagePanel.revalidate();
	}
	String uploadInputMessage() {
		String message = "";
		String me = "[Me] ";
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
	

}