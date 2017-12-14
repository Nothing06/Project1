package mainMenu;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import utility.NetworkLib;

public class SettingTab extends JPanel{
	
	JLabel myProfile;
	JLabel myID;
	JLabel myName;
	JLabel myAge;
	JLabel myPhoneNum;
	JButton editBtn;
	JPanel myTopPanel;
	JPanel myInfoPanel;
	NetworkLib networkLib;
	String loginID;
	Font myTopPanelFont = new Font("바탕", Font.PLAIN, 45);;
	Font myInfoPanelFont = new Font("돋움", Font.BOLD, 30);
	public SettingTab()
	{
		//this(null,l)
	}
	public JLabel getAgeLabel()
	{
		return myAge;
	}
	public JLabel getNameLabel()
	{
		return myName;
	}
	public JLabel getPhoneNumLabel()
	{
		return myPhoneNum;
	}
	public JLabel getIDLabel()
	{
		return myID;
	}
	
	public SettingTab(NetworkLib networkLib, String loginID)
	{
		this.networkLib = networkLib;
		this.loginID = loginID;
		
		setLayout(new BorderLayout());
		makeTopPanel();
		makeInfoPanel();
		add(myTopPanel,BorderLayout.NORTH);
		add(myInfoPanel,BorderLayout.CENTER);
		setSize(400,500);
		networkLib.sendMyIDPacketToGetMyInfo(loginID);
	}
	void makeTopPanel()
	{
		myTopPanel = new JPanel();
	//	myTopPanel.setFont(myTopPanelFont);
		myProfile = new JLabel("내 프로필 : " + loginID);
		myProfile.setFont(myTopPanelFont);
		editBtn = new JButton("Edit");
		myTopPanel.add(myProfile);
		myTopPanel.add(editBtn);
	}
	void makeInfoPanel()
	{
		myInfoPanel = new JPanel();
		myInfoPanel.setFont(myInfoPanelFont);
		myInfoPanel.setLayout(new BoxLayout(myInfoPanel, BoxLayout.Y_AXIS));
		myName = new JLabel("이름 : " ); myName.setFont(myInfoPanelFont);
		myAge = new JLabel("나이: ");  myAge.setFont(myInfoPanelFont);
		myPhoneNum = new JLabel("전화번호: ");  myPhoneNum.setFont(myInfoPanelFont);
		myInfoPanel.add(myName);
		myInfoPanel.add(myAge);
		myInfoPanel.add(myPhoneNum);
	}
}
