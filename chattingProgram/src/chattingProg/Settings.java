package chattingProg;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Settings extends JPanel{
	
	JLabel myProfile;
	JLabel myID;
	JLabel myName;
	JLabel myAge;
	JLabel myPhoneNum;
	JButton editBtn;
	JPanel myProfilePanel;
	JPanel myInfoPanel;
	Settings()
	{
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
	}
}
