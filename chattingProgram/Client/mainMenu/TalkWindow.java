package mainMenu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
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
	public void deliverNewMessage(String message)
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
	

}