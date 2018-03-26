package mainMenu;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import loginMenu.LoginWindow;
import loginMenu.RegContent;
import utility.InputChecker;
import utility.NetworkLib;
import utility.PersonRecord;

public class SettingTab extends JPanel implements ActionListener{
	
	JLabel myProfile; 
	JLabel myID; JLabel ID;
	JLabel myName; JLabel Name;
	JLabel myAge; JLabel Age;
	JLabel myPhoneNum; JLabel PhoneNum;
	
	JPanel idPanel;
	JPanel namePanel;
	JPanel agePanel;
	JPanel phoneNumPanel;
	
	private JButton logoutBtn;
	JButton editBtn;
	JPanel myTopPanel;
	JPanel myInfoPanel;
	JPanel settingTab;
	NetworkLib networkLib;
	JFrame parent;
	String loginID;
	Font myTopPanelFont = new Font("����", Font.PLAIN, 45);;
	Font myInfoPanelFont = new Font("����", Font.BOLD, 30);
	private JPanel utilPanel;
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
	public JLabel getLabel(String labelName)
	{
		if(labelName.equals("id"))
			return myID;
		else if(labelName.equals("name"))
			return myName;
		else if(labelName.equals("age"))
			return myAge;
		else if(labelName.equals("phoneNum"))
			return myPhoneNum;
		return null;
	}
	public SettingTab(JFrame parent,NetworkLib networkLib, String loginID)
	{
		this.parent = parent;
		this.networkLib = networkLib;
		this.loginID = loginID;
		settingTab = this;
		setLayout(new BorderLayout());
		makeTopPanel();
		makeInfoPanel();
		add(myTopPanel,BorderLayout.NORTH);
		add(myInfoPanel,BorderLayout.CENTER);
		setSize(400,500);
		networkLib.sendMyIDPacketToGetMyInfo(loginID);
	}
	void editProfile(EditContent editContent) {
		JLabel[] myInfoLabel = new JLabel[3];
		String src;

		String[] prop = new String[]{ "name", "phoneNum"};
		int[]  map = new int[] { PersonRecord.NAME,  PersonRecord.PHONE_NUM};
		
		for(int i = 0 ; i < prop.length; ++i)
		{
			JLabel lab = ((SettingTab) settingTab).getLabel(prop[i]);
			if(!editContent.getPropValue(prop[i]).equals("#"))
				lab.setText(editContent.getPropValue(prop[i]));
		}
		this.repaint();
	}
	JButton getLogoutBtn() {
		return logoutBtn;
	}
	void makeTopPanel()
	{
		myTopPanel = new JPanel();
	//	myTopPanel.setFont(myTopPanelFont);
		myTopPanel.setLayout(new BorderLayout());
		myProfile = new JLabel("\t     \t    \t    �� ������ ");
		myProfile.setFont(myTopPanelFont);
		editBtn = new JButton("Edit");
		editBtn.addActionListener(this);
		
		utilPanel = new JPanel();
		logoutBtn = new JButton("�α׾ƿ�");
		logoutBtn.addActionListener(this);
		utilPanel.add(editBtn);
		utilPanel.add(logoutBtn);
		
		myTopPanel.add(myProfile, BorderLayout.CENTER);
		myTopPanel.add(utilPanel, BorderLayout.EAST);
	}
	void makeInfoPanel()
	{
		myInfoPanel = new JPanel();
		myInfoPanel.setFont(myInfoPanelFont);
		myInfoPanel.setLayout(new BoxLayout(myInfoPanel, BoxLayout.Y_AXIS));
		
		ID = new JLabel(" *  ID: ");  ID.setFont(myInfoPanelFont);
		myID = new JLabel("");  myID.setFont(myInfoPanelFont);
		idPanel = new JPanel();
		idPanel.add(ID); idPanel.add(myID);
		
		Name = new JLabel(" * �̸� : " ); Name.setFont(myInfoPanelFont);
		myName = new JLabel("" ); myName.setFont(myInfoPanelFont);
		namePanel = new JPanel();
		namePanel.add(Name); namePanel.add(myName);
		
		Age = new JLabel(" * ����: ");  Age.setFont(myInfoPanelFont);
		myAge = new JLabel("");  myAge.setFont(myInfoPanelFont);
		agePanel = new JPanel();
		agePanel.add(Age); agePanel.add(myAge);
		
		PhoneNum = new JLabel(" * ��ȭ��ȣ: ");  PhoneNum.setFont(myInfoPanelFont);
		myPhoneNum = new JLabel("");  myPhoneNum.setFont(myInfoPanelFont);
		phoneNumPanel = new JPanel();
		phoneNumPanel.add(PhoneNum); phoneNumPanel.add(myPhoneNum);
		
		myInfoPanel.add(idPanel);
		myInfoPanel.add(namePanel);
		myInfoPanel.add(agePanel);
		myInfoPanel.add(phoneNumPanel);
	}
	public void actionPerformed(ActionEvent e) {

		if(e.getSource() == editBtn)
		{
			EditProfileDialog editProfile = new EditProfileDialog( parent, settingTab, networkLib);
			editProfile.setVisible(true);
		}
		else if(e.getSource() == logoutBtn) {
			JOptionPane.showConfirmDialog(parent,"���� �α׾ƿ��Ͻðڽ��ϱ�?");
			LoginWindow loginWindow = new LoginWindow();
			parent.dispose();

			loginWindow.revalidate();
		}
	}
}




class EditProfileDialog extends JDialog implements ActionListener {
	JPanel edit_input;
	final int item_cnt = 3;
	JPanel[] items = new JPanel[item_cnt+1];
	int i = 0;
	
	enum edit_items  {PASSWORD,NAME,PHONE};
	public edit_items item;
	String[] str_items = { "��й�ȣ ","�̸�", "����ó " };
	JButton editBtn = new JButton("����");
//	JPasswordField passwd = new JPasswordField(30);
	JTextField[] txtArr = new JTextField[item_cnt];
	JFrame parent;
	EditContent editContent;
	InputChecker inputChecker;
	NetworkLib networkLib;
	JPanel settingTab;
	static String title = "Edit Profile";
	public EditProfileDialog(JFrame parent, JPanel settingTab, NetworkLib networkLib) {
		
		super(parent, title);
		this.networkLib = networkLib;
		this.parent = parent;
		this.settingTab = settingTab;
		inputChecker = new InputChecker();
		
		edit_input = new JPanel();
		edit_input.setLayout(new BoxLayout(edit_input, BoxLayout.Y_AXIS));
		for (i = 0; i < item_cnt; i += 1) {
			items[i] = new JPanel();
			items[i].setLayout(new FlowLayout());
			items[i].add(new JLabel(str_items[i]));
			if(i == edit_items.PASSWORD.ordinal())
				txtArr[i] = new JPasswordField(25);
			else	
				txtArr[i] = new JTextField(25);
			txtArr[i].setEditable(true);
			items[i].add(txtArr[i]);
			edit_input.add(items[i]);
		}
		items[item_cnt] = new JPanel();
		items[item_cnt].add(editBtn);
		edit_input.add(items[item_cnt]);

		editBtn.addActionListener(this);
		getContentPane().add(edit_input);
		setSize(400, 500);
	}
	boolean checkEditInfo_Ok()
	{
		editContent = new EditContent();
		
		
		if(!txtArr[0].getText().equals(""))
		{
			if(!inputChecker.checkPassword(((JPasswordField)txtArr[0]).getPassword().toString()))
			{
				JOptionPane.showInputDialog("��й�ȣ�� 7���� �̻��̿����մϴ�.");
				return false;
			}
			
			editContent.setPassword(((JPasswordField)txtArr[0]).getPassword().toString());
		}
		else
		{
			editContent.setPassword("#");
		}
		
		
		if(!txtArr[1].getText().equals(""))
		{
			if(!inputChecker.checkName(txtArr[1].getText()))
			{
				JOptionPane.showInputDialog("�̸��� 2���� �̻��̿��� �մϴ�.");
				return false;
			}
			editContent.setName(txtArr[1].getText());
		}
		else
		{
			editContent.setName("#");
		}
		
		
		if(!txtArr[2].getText().equals(""))
		{
			if(!inputChecker.checkPhoneNumber(txtArr[2].getText()))
			{
				JOptionPane.showInputDialog("�ڵ�����ȣ�� �Է����ּ���.(�� : 010-1234-5678)");
				return false;
			}
			editContent.setPhone(txtArr[2].getText());
		}
		else
		{
			editContent.setPhone("#");
		}
		return true;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		if(e.getSource() == editBtn)
		{
			if(checkEditInfo_Ok())
			{
				networkLib.sendEditProfileInfo(networkLib.loginID,editContent);
				((SettingTab) settingTab).editProfile(editContent);
				parent.invalidate();
				parent.repaint();
				this.dispose();
			}
		}
		
	}
}
