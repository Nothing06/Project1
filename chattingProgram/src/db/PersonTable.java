package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JTable;

import com.mysql.jdbc.Statement;

public class PersonTable{

	public ArrayList<String[]> personTable =new ArrayList<String[]>();
	//public static DefaultTableModel model;
	public ArrayList<String[]> chatContentTable= new ArrayList<String[]>();
	public JTable table;
	public int person_tuplecount=0;
	int chatContent_tuplecount=0;
	String[] data;
	Connection con;
	PreparedStatement pstmt=null;
	ResultSet rs=null;
	String str[] = {"id","password","emp_no", "이름", "나이", "전화번호"};
	public PersonTable()
	{
	//	model = new DefaultTableModel(customerList,str);
	//	table = new JTable(model);
		
		connect();
		loadFriendInfoDB();
		
	}
	


	private void connect()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			String url="jdbc:mysql://localhost:3306/jdbctest";
			con = DriverManager.getConnection(url,"root","cs%s920026");
			System.out.println("접속: "+con);
			
		}
		catch(Exception e)
		{
			System.out.println("DB접속오류"+e);
		}
	}
	public void insert(String sql)
	{
		Statement stm;
		System.out.println(sql);
		try {
			stm = (Statement) con.createStatement();
			stm.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public void addNewMemberInfo(String id, String password, String emp_no,
								String name, String age, String tel)
	{
		String[] tuple = new String[6];
		tuple[0] = id;
		tuple[1] = password;
		tuple[2] = emp_no;
		tuple[3] = name;
		tuple[4] = age;
		tuple[5] = id;
		
		personTable.add(tuple);
//		customerList[tuple_cnt][1] = password;
//		customerList[tuple_cnt][2] = emp_no;
//		customerList[tuple_cnt][3] = name;
//		customerList[tuple_cnt][4] = (age);
//		customerList[tuple_cnt][5] = tel;
	//	model.addRow(data);
		person_tuplecount+=1;
	}
	public void loadChattingContentDB()
	{
		try
		{
			String sql = "select content from chatContent";
			pstmt = con.prepareStatement(sql);
			System.out.println("pstmt : " + pstmt);
			rs = pstmt.executeQuery();
			System.out.println("rs: "+ rs);
			while(rs.next())
			{
		//		loadItem();
			}
		}
		catch(Exception e)
		{
			System.out.println("select 실행오류: " + e);
		}
	}
	public void loadFriendInfoDB()
	{
		try
		{
			String sql = "select id,password,emp_no,name,age,tel from person order by id";
			pstmt = con.prepareStatement(sql);
			System.out.println("pstmt : " + pstmt);
			rs = pstmt.executeQuery();
			System.out.println("rs: "+ rs);
			while(rs.next())
			{
				loadTablePerson(rs);
			}
		}
		catch(Exception e)
		{
			System.out.println("select 실행오류: " + e);
		}
	}

	private void loadTableChatContent() throws SQLException {
		String id = rs.getString("id");
		String content = rs.getString("content");
		
		
//		customerList[tuple_cnt][0] = id;
//		customerList[tuple_cnt][1] = password;
//		customerList[tuple_cnt][2] = Integer.toString(emp_no);
//		customerList[tuple_cnt][3] = name;
//		customerList[tuple_cnt][4] = Integer.toString(age);
//		customerList[tuple_cnt][5] = tel;
		String[] tuple = new String[2];
		tuple[0] = id;
		tuple[1] = content;
		
		chatContentTable.add(tuple);
//	model.addRow(data);
		chatContent_tuplecount+=1;
	//	System.out.println(id + ", " + password + ", "+ emp_no + ", " + name + ", " + age + ", " + tel);
	}

	private void loadTablePerson(ResultSet rs) throws SQLException { // ResultSet 인자 추가
		String id = rs.getString("id");
		String password = rs.getString("password");
		int emp_no = rs.getInt("emp_no");
		String name=rs.getString("name");
		int age = rs.getInt("age");
		String tel = rs.getString("tel");
		String tmp[] = new String[6];
		
		tmp[0] = id;
		tmp[1] = password;
		tmp[2] = Integer.toString(emp_no);
		tmp[3] = name;
		tmp[4] =  Integer.toString(age);
		tmp[5] = tel;
		personTable.add(tmp);
//		customerList[tuple_cnt][0] = id;
//		customerList[tuple_cnt][1] = password;
//		customerList[tuple_cnt][2] = Integer.toString(emp_no);
//		customerList[tuple_cnt][3] = name;
//		customerList[tuple_cnt][4] = Integer.toString(age);
//		customerList[tuple_cnt][5] = tel;
		
//	model.addRow(data);
		person_tuplecount+=1;
		System.out.println(id + ", " + password + ", "+ emp_no + ", " + name + ", " + age + ", " + tel);
	}
}
