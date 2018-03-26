package loginMenu;

import java.awt.FlowLayout;
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

import utility.InputChecker;
import utility.NetworkLib;

public class RegDialog extends JDialog implements ActionListener {
	JPanel regInputPanel;
	final int regItemCnt = 7;
	JPanel[] regItemPanelList = new JPanel[regItemCnt];
	int i = 0;
	
	enum regItemEnum  {ID,PASSWORD,NAME,AGE,PHONE,REGISTER};
	public regItemEnum item;
	String[] regItemNameArr = { "���̵� ", "��й�ȣ ", "�̸� ", "���� ", "����ó ","���" };
	JButton regBtn = new JButton("���");
	JTextField[] regItemInputArr = new JTextField[7]; // ����ó�� JTextField�� 3��(ex. 010 / 3425 / 4251) 
	JFrame parent;
	RegContent regContent;
	InputChecker inputChecker;
	NetworkLib networkLib;
	void makeRegItemNameLabel(int idx) {
		regItemPanelList[idx].add(new JLabel(regItemNameArr[idx]));
	}
	void makeRegItemPanel(int idx) {
		regItemPanelList[idx] = new JPanel();
		regItemPanelList[idx].setLayout(new FlowLayout());
	}
	public RegDialog(JFrame frame, String title, NetworkLib networkLib) {
		
		super(frame, title);
		this.networkLib = networkLib;
		parent  =  frame;
		inputChecker = new InputChecker();
		makeRegDialog();

		
		getContentPane().add(regInputPanel);
		setSize(400, 500);
	}
	private void makeRegDialog() {
		regContent = new RegContent();
		regInputPanel = new JPanel();
		regInputPanel.setLayout(new BoxLayout(regInputPanel, BoxLayout.Y_AXIS));
		makeRegItemPanelList();
		regInputPanel.add(regItemPanelList[regItemEnum.PHONE.ordinal()]);
		
		makeRegBtnPanel();
	}
	private void makeRegItemPanelList() {
		for (i = 0; i < regItemCnt; i += 1) {
			makeRegItemPanel(i);
			if(i<=regItemEnum.PHONE.ordinal())
				makeRegItemNameLabel(i);
			
			makeRegInputItem(i);
			
			if(i>=regItemEnum.PHONE.ordinal())
			{
				regItemPanelList[regItemEnum.PHONE.ordinal()].add(regItemInputArr[i]);
				regItemPanelList[regItemEnum.PHONE.ordinal()].add(new JLabel("  "));
			}
			else {
				regItemPanelList[i].add(regItemInputArr[i]);
				regInputPanel.add(regItemPanelList[i]);
			}
		}
	}
	private void makeRegBtnPanel() {
		regItemPanelList[regItemEnum.REGISTER.ordinal()] = new JPanel();
		regItemPanelList[regItemEnum.REGISTER.ordinal()].add(regBtn);
		regInputPanel.add(regItemPanelList[regItemEnum.REGISTER.ordinal()]);
		regBtn.addActionListener(this);
	}
	private void makeRegInputItem(int i) {
		if(i == regItemEnum.PASSWORD.ordinal())
			regItemInputArr[i] = new JPasswordField(25);
		else if(i>= regItemEnum.PHONE.ordinal())
			regItemInputArr[i] = new JTextField(5);
		else	
			regItemInputArr[i] = new JTextField(25);
		regItemInputArr[i].setEditable(true);
	}
	boolean checkUpRegInput()
	{
		regContent.setRegID( regItemInputArr[0].getText());
		if(!inputChecker.checkID(regContent.getRegID()))
		{
			JOptionPane.showInputDialog("���̵�� 4���� �̻��̿����ϸ�, '.'�� ���������ϴ�.");
			return false;
		}
		regContent.setRegPassword( regItemInputArr[1].getText());
		if(!inputChecker.checkPassword(regContent.getRegPassword()))
		{
			JOptionPane.showInputDialog("��й�ȣ�� 7���� �̻��̿����մϴ�.");
			return false;
		}
		regContent.setRegName( regItemInputArr[2].getText());
		if(!inputChecker.checkName(regContent.getRegName()))
		{
			JOptionPane.showInputDialog("�̸��� 2���� �̻��̿��� �մϴ�.");
			return false;
		}
		regContent.setRegAge(regItemInputArr[3].getText());
		if(!inputChecker.checkAge(regContent.getRegAge()))
		{
			JOptionPane.showInputDialog("�߸� �Է��ϼ̽��ϴ�.(�ڽ��� ���� ���ڸ� �Է����ּ���.)");
			return false;
		}
		regContent.setRegPhone(regItemInputArr[4].getText() + "-"
									+ regItemInputArr[5].getText() + "-" 
									+ regItemInputArr[6].getText() );
		if(!inputChecker.checkPhoneNumber(regContent.getRegPhone()))
		{
			JOptionPane.showInputDialog("�ڵ�����ȣ�� �Է����ּ���.(�� : 010-1234-5678)");
			return false;
		}
		return true;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		if(e.getSource() == regBtn)
		{
			if(checkUpRegInput())
			{
				networkLib.sendJoinedMemberInfo(regContent);
				this.dispose();
			}
		}
		
	}
}
