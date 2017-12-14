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
import javax.swing.JTextField;

import utility.InputChecker;
import utility.NetworkLib;

public class RegDialog extends JDialog implements ActionListener {
	JPanel reg_input;
	JPanel[] items = new JPanel[6];
	int i = 0;
	final int item_cnt = 5;
	String[] str_items = { "���̵� ", "��й�ȣ ", "�̸� ", "���� ", "����ó " };
	JButton regBtn = new JButton("���");
	JTextField[] txtArr = new JTextField[5];
	JFrame parent;
	RegContent regContent;
	InputChecker inputChecker;
	NetworkLib networkLib;
	public RegDialog(JFrame frame, String title, NetworkLib networkLib) {
		
		super(frame, title);
		this.networkLib = networkLib;
		parent  =  frame;
		inputChecker = new InputChecker();
		regContent = new RegContent();
		reg_input = new JPanel();
		reg_input.setLayout(new BoxLayout(reg_input, BoxLayout.Y_AXIS));
		for (i = 0; i < item_cnt; i += 1) {
			items[i] = new JPanel();
			items[i].setLayout(new FlowLayout());
			items[i].add(new JLabel(str_items[i]));
			txtArr[i] = new JTextField(25);
			txtArr[i].setEditable(true);
			items[i].add(txtArr[i]);
			reg_input.add(items[i]);
		}
		items[5] = new JPanel();
		items[5].add(regBtn);
		reg_input.add(items[5]);

		regBtn.addActionListener(this);
		getContentPane().add(reg_input);
		setSize(400, 500);
	}
	boolean getJoinInputInfo_Ok()
	{
		regContent.setRegID( txtArr[0].getText());
		if(!inputChecker.checkID(regContent.getRegID()))
		{
			JOptionPane.showInputDialog("���̵�� 4���� �̻��̿����ϸ�, '.'�� ���������ϴ�.");
			return false;
		}
		regContent.setRegPassword( txtArr[1].getText());
		if(!inputChecker.checkPassword(regContent.getRegPassword()))
		{
			JOptionPane.showInputDialog("��й�ȣ�� 7���� �̻��̿����մϴ�.");
			return false;
		}
		regContent.setRegName( txtArr[2].getText());
		if(!inputChecker.checkName(regContent.getRegName()))
		{
			JOptionPane.showInputDialog("�̸��� 2���� �̻��̿��� �մϴ�.");
			return false;
		}
		regContent.setRegAge(txtArr[3].getText());
		if(!inputChecker.checkAge(regContent.getRegAge()))
		{
			JOptionPane.showInputDialog("���̴� 1���̻��̿��� �մϴ�.");
			return false;
		}
		regContent.setRegPhone(txtArr[4].getText());
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
			if(getJoinInputInfo_Ok())
			{
				networkLib.sendJoinedMemberInfo(regContent);
				this.dispose();
			}
		}
		
	}
}
