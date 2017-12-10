package chattingProg;

public class Application {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LoginWindow loginWindow;
		NetworkLib networkLib;
		networkLib = new NetworkLib();
		loginWindow = new LoginWindow(networkLib);
		networkLib.loginWindow = loginWindow;
		loginWindow.setTitle("WeChat");
	}
	
}

