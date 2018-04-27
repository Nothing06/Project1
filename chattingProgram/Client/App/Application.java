package App;

import loginMenu.LoginWindow;
import utility.NetworkLib;

public class Application {
// //demon thread 
//	String a1 = "hello";
//	String a2 = "hello";
//	
	
//	String b = new String("hello");
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//System.out.println(a1s.equals(a2), a1==a2);
		LoginWindow loginWindow = null;
	//	NetworkLib networkLib;
//		networkLib = new NetworkLib();
		loginWindow = new LoginWindow();
	//	networkLib.loginWindow = loginWindow;
		loginWindow.setTitle("WeChat");
	}
	
}

