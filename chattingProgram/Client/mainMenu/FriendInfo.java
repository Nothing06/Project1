package mainMenu;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import utility.NetworkLib;

public class FriendInfo extends JFrame implements ActionListener { //친구정보 보여주는 창

	JLabel ID_label;
	JLabel name_label;
	JLabel tel_label;
	JLabel age_label;
	JPanel friendInfoPanel = new JPanel();
	JButton talkBtn;
	JButton sendFileBtn;
	NetworkLib networkLib;
	Font f = new Font("바탕", Font.ITALIC, 25);
	String talkCompanionID;
	HashMap<String,TalkWindow> talkList;
	public FriendInfo(String[] friendInfoTuple, NetworkLib networkLib, 
			HashMap<String,TalkWindow> talkList, ArrayList<String> friendID_list)
	{ // friendInfoTuple[6]의 애트리뷰트 순서:  0:ID , 1:Password, 2: emp_no,
										// 3: 이름, 4: 나이, 5: 전화번호
		this.networkLib = networkLib;
		this.talkList = talkList;
		talkCompanionID = friendInfoTuple[0];
		friendInfoPanel.setLayout(new BoxLayout(friendInfoPanel, BoxLayout.Y_AXIS));
		ID_label = new JLabel("아이디 : " + friendInfoTuple[0]); 
		ID_label.setFont(f);
		name_label = new JLabel("이름 : " + friendInfoTuple[3]);
		name_label.setFont(f);
		age_label = new JLabel("나이 : " + friendInfoTuple[4]);
		age_label.setFont(f);
		tel_label = new JLabel("전화번호: " + friendInfoTuple[5]);
		tel_label.setFont(f);
		talkBtn = new JButton("대화하기");
		sendFileBtn = new JButton("파일전송");
		friendInfoPanel.add(ID_label);
		friendInfoPanel.add(name_label);
		friendInfoPanel.add(age_label);
		friendInfoPanel.add(tel_label);
		friendInfoPanel.add(talkBtn);
		talkBtn.addActionListener(this);
		sendFileBtn.addActionListener(this);
		getContentPane().add(friendInfoPanel);
		setSize(400,400);
		setTitle("FriendInfo");
		setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == talkBtn)
		{
			
			TalkWindow talkDialog=null;
			
			if(talkList.get(talkCompanionID)==null)
			{
				this.dispose();
				talkDialog = new TalkWindow(networkLib, talkCompanionID, talkList);
				talkList.put(talkCompanionID, talkDialog);
				
			}
			else
			{
				TalkWindow tmp  =  talkList.get(talkCompanionID);
				tmp.requestFocus();
			}
		}
		else if(e.getSource() == sendFileBtn)
		{
			 JFileChooser chooser = new JFileChooser();
             FileNameExtensionFilter filter = new FileNameExtensionFilter(
                 "JPG & GIF Images", "jpg", "gif");  //description,......확장자
            chooser.setFileFilter(filter);    //필터 셋팅
            int returnVal = chooser.showOpenDialog(this);
             if(returnVal == JFileChooser.APPROVE_OPTION) {
                System.out.println("You chose to open this file: " +
                     chooser.getSelectedFile(). getName());
             }
//             try {
//			//	networkLib.sendFile(chooser.getSelectedFile().getAbsolutePath(),talkCompanionID);
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
		}
	}
	
}