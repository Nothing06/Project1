package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JTable;

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
	public String[] columnNameArr = {"id","password","emp_no", "name", "age", "tel"};
	
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
			Class.forName("com.mysql.cj.jdbc.Driver");
			String url="jdbc:mysql://localhost:3306/jdbctest?serverTimezone=UTC";
			con = DriverManager.getConnection(url,"root","didgnstm61");
			System.out.println("접속: "+con);
			
		}
		catch(Exception e)
		{
			System.out.println("DB접속오류"+e);
		}
	}
	public String selectOne(String sql, String attributeName) {
		String attributeValue="";
		PreparedStatement pstm = null;
		ResultSet rs=null;
		try {
			pstm = (PreparedStatement)con.prepareStatement(sql);
			rs = pstm.executeQuery(sql);
			
			while(rs.next())
			{
				attributeValue = rs.getString("friendIDList");
			}
			
		}
		catch(SQLException e) {System.out.println();
		e.printStackTrace();}
		
		return attributeValue;
	}
	public ArrayList<String> selectAll(String sql) {
		ArrayList<String> attributeList = new ArrayList<String>();
		PreparedStatement pstm = null;
		ResultSet rs=null;
		int columnIdx=1; //1부터 시작
		int nData=0;
		String data=""; 
		try {
			pstm = (PreparedStatement)con.prepareStatement(sql);
			rs = pstm.executeQuery(sql);
			
			while(rs.next())
			{
				attributeList.add(rs.getString("id"));
				attributeList.add(String.valueOf(rs.getInt("emp_no")));
				attributeList.add(rs.getString("name"));
				attributeList.add(String.valueOf(rs.getInt("age")));
				attributeList.add(rs.getString("tel"));
				attributeList.add(rs.getString("friendIDList"));
				
//				if(columnIdx == 3 || columnIdx == 5) {
//					nData = rs.getInt(columnIdx);
//					System.out.print(nData + ", ");
//					//columnIdx+=1;
//					attributeList.add(String.valueOf(nData));
//				}
//				else	{
//					data = rs.getString(columnIdx);
//					System.out.print(data + ", ");
//					//columnIdx+=1;
//					attributeList.add(data);
//				}
//				columnIdx+=1;
			}
			
		}
		catch(SQLException e) {System.out.println();
		e.printStackTrace();}
		
		return attributeList;
	}
	public void update(String sql) {
		PreparedStatement stm=null;
		System.out.println(sql);
		try {
			stm = (PreparedStatement) con.prepareStatement(sql);
			stm.executeUpdate(sql);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			  try {
				stm.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	public void update(String sql,String[] tuple,int tuple_idx,boolean[] bEdit)
	{
		PreparedStatement stm=null;
		System.out.println(sql);
		try {
			stm = (PreparedStatement) con.prepareStatement(sql);
			stm.executeUpdate(sql);
			for(int i=0;i<tuple.length;i+=1)
				if(bEdit[i])
					personTable.get(tuple_idx)[i] = tuple[i];
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			  try {
				stm.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	public void insert(String sql) // 2017.12.24.  오후 1:09  Statement -> PreparedStatement로 수정
	{
		PreparedStatement stm;
		System.out.println(sql);
		try {
			stm = (PreparedStatement) con.prepareStatement(sql);
			stm.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public void addNewMemberInfoToList(RegContent regContent)
	{
		String[] tuple = new String[6];
		tuple[0] = regContent.getRegID();
		tuple[1] = regContent.getRegPassword();
		tuple[2] = regContent.getRegNo();
		tuple[3] = regContent.getRegName();
		tuple[4] = regContent.getRegAge();
		tuple[5] = regContent.getRegPhone();
		
		personTable.add(tuple);
//		customerList[tuple_cnt][1] = password;
//		customerList[tuple_cnt][2] = emp_no;
//		customerList[tuple_cnt][3] = name;
//		customerList[tuple_cnt][4] = (age);
//		customerList[tuple_cnt][5] = tel;
	//	model.addRow(data);
		person_tuplecount+=1;
	}
//	public void loadChattingContentDB()
//	{
//		try
//		{
//			String sql = "select content from chatContent";
//			pstmt = con.prepareStatement(sql);
//			System.out.println("pstmt : " + pstmt);
//			rs = pstmt.executeQuery();
//			System.out.println("rs: "+ rs);
//			while(rs.next())
//			{
//		//		loadItem();
//			}
//		}
//		catch(Exception e)
//		{
//			System.out.println("select 실행오류: " + e);
//		}
//	}
	public void loadFriendInfoDB() // 2018 03 17 오전 1:14  서버에서 DB내용을 메모리에 로드해주고 실시간으로 변경해줘야됨
	{
		try
		{
			String sql = "select id,password,emp_no,name,age,tel,friendIDList from person order by id";
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
		String friendIDList = rs.getString("friendIDList");
		String tmp[] = new String[7];
		
		tmp[0] = id;
		tmp[1] = password;
		tmp[2] = Integer.toString(emp_no);
		tmp[3] = name;
		tmp[4] =  Integer.toString(age);
		tmp[5] = tel;
		tmp[6] = friendIDList;
		personTable.add(tmp);
//		customerList[tuple_cnt][0] = id;
//		customerList[tuple_cnt][1] = password;
//		customerList[tuple_cnt][2] = Integer.toString(emp_no);
//		customerList[tuple_cnt][3] = name;
//		customerList[tuple_cnt][4] = Integer.toString(age);
//		customerList[tuple_cnt][5] = tel;
		
//	model.addRow(data);
		person_tuplecount+=1;
		System.out.println(id + ", " + password + ", "+ emp_no + ", " + name + ", " + age + ", " + 
								tel + "," + friendIDList);
	}
}
