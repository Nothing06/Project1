package mainMenu;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import utility.NetworkLib;

public class SettingPanel extends JPanel{
	
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
	public SettingPanel()
	{
	}
	public SettingPanel(NetworkLib networkLib, String loginID)
	{
		this.networkLib = networkLib;
		this.loginID = loginID;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		makeTopPanel();
		makeInfoPanel();
		
	}
	void makeTopPanel()
	{
		myTopPanel = new JPanel();
		myProfile = new JLabel("내 프로필 : " + loginID);
		editBtn = new JButton("Edit");
		myTopPanel.add(myProfile);
		myTopPanel.add(editBtn);
	}
	void makeInfoPanel()
	{
	//	myName = new JLabel("이름 : " + )
	}
}
