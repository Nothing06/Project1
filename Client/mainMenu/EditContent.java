package mainMenu;

public class EditContent{
	private String password;
	private String name;
	private int age;
	private String phone;
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPropValue(String propName)
	{
		if(propName.equals("password"))
			return password;
		else if(propName.equals("name"))
			return name;
		else if(propName.equals("phoneNum"))
			return phone;
		return null;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
}