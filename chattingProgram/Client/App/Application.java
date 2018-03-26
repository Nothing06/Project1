package App;

import loginMenu.LoginWindow;
import utility.NetworkLib;

public class Application {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LoginWindow loginWindow = null;
	//	NetworkLib networkLib;
//		networkLib = new NetworkLib();
		loginWindow = new LoginWindow();
	//	networkLib.loginWindow = loginWindow;
		loginWindow.setTitle("WeChat");
	}
	
}

