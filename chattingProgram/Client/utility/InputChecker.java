package utility;

public class InputChecker {
	private String ID;
	private String password;
	private String name;
	private String age;
	private String phoneNum;
	
	public InputChecker()
	{
		
	}
	public boolean checkID(String ID)
	{
		boolean t = true;
		int len = ID.length();
		if(len <= 3)
		{
			return false;
		}
		for(int i=0;i<ID.length();i+=1)
		{
			if(ID.charAt(i)=='.')
			{
				t = false;
			}
		}
		return t;
	}
	public boolean checkPassword(String password)
	{
		if(password.length() <= 6)
			return false;
		return true;
	}
	public boolean checkName(String name)
	{
		if(name.length() <= 1)
			return false;
		return true;
	}
	public boolean checkAge(String age)
	{
		for(int i=0;i<age.length();i+=1) {
			if(age.charAt(i) < '0' || age.charAt(i) >'9')
				return false;
		}
		int n = Integer.valueOf(age);
		if(n<1)
			return false;
		return true;
	}
	public boolean isdigit(char c)
	{
		return c>='0' && c<='9';
	}
	public boolean checkPhoneNumber(String phoneNum)
	{
		int i=0;
		int len = phoneNum.length();
		if(len != 13)
		{
			return false;
		}
		String firstToken = phoneNum.substring(0, 3);
		
		if(!firstToken.equals("010"))
		{
			return false;
		}
		
		for(i=0;i<phoneNum.length();i+=1)
		{
			if(!isdigit(phoneNum.charAt(i)))
			{
				if(phoneNum.charAt(i)=='-' && (i==3 || i==8)) return true;
				else return false;
			}
		}
		return true;
	}
}
