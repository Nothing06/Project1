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

public class RegDialog extends JDialog implements ActionListener {
	JPanel reg_input;
	JPanel[] items = new JPanel[6];
	int i = 0;
	final int item_cnt = 5;
	String[] str_items = { "아이디 ", "비밀번호 ", "이름 ", "나이 ", "연락처 " };
	JButton regBtn = new JButton("등록");
	JTextField[] txtArr = new JTextField[5];
	JFrame parent;
	RegContent rc;
	InputChecker inputChecker;
	
	public RegDialog(JFrame frame, RegContent rc, String title) {
		
		super(frame, title);
		parent  =  frame;
		this.rc = rc;
		inputChecker = new InputChecker();
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
		rc.regID = txtArr[0].getText();
		if(!inputChecker.checkID(rc.regID))
		{
			JOptionPane.showInputDialog("아이디는 4글자 이상이여야하며, '.'은 들어갈수없습니다.");
			return false;
		}
		rc.regPassword = txtArr[1].getText();
		if(!inputChecker.checkPassword(rc.regPassword))
		{
			JOptionPane.showInputDialog("비밀번호는 7글자 이상이여야합니다.");
			return false;
		}
		rc.name = txtArr[2].getText();
		if(!inputChecker.checkName(rc.name))
		{
			JOptionPane.showInputDialog("이름은 2글자 이상이여야 합니다.");
			return false;
		}
		rc.age = (txtArr[3].getText());
		if(!inputChecker.checkAge(rc.age))
		{
			JOptionPane.showInputDialog("나이는 1살이상이여야 합니다.");
			return false;
		}
		rc.tel = txtArr[4].getText();
		if(!inputChecker.checkPhoneNumber(rc.tel))
		{
			JOptionPane.showInputDialog("핸드폰번호를 입력해주세요.(예 : 010-1234-5678)");
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
				((LoginWindow) parent).sendJoinedMemberInfo();
				this.dispose();
			}
		}
		
	}
}
