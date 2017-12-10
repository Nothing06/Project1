package chattingProg;

public class Application {

	static NetworkLib networkLib;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LoginWindow loginWindow;
		networkLib = new NetworkLib();
		loginWindow = new LoginWindow(networkLib);
		networkLib.loginWindow = loginWindow;
		loginWindow.setTitle("WeChat");
	}
	
}

