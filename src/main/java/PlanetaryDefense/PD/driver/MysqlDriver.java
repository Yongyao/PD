package PlanetaryDefense.PD.driver;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gson.JsonObject;

import PlanetaryDefense.PD.wiki.Shell;

public class MysqlDriver {
	Connection connection = null;
	Statement stmt = null;
	JsonObject PDResults = new JsonObject();
	
	public String updatePWD(String username, String oldpwd, String newpwd){
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
			return null;
		}

		try {
			connection = DriverManager
			.getConnection("jdbc:mysql://127.0.0.1:3306/test","root", "admin");

		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return null;
		}

		if (connection != null) {
			try {
				stmt = connection.createStatement();

				String sql = "SELECT password FROM test.users WHERE username=" + "'" + username + "'";
				ResultSet rs = stmt.executeQuery(sql);
				String old = null;
				while(rs.next()){
					//Retrieve by column name
					old = rs.getString("password");			        
				}
				rs.close();

				if(old.equals(oldpwd)){
					sql = "UPDATE `test`.`users` SET `password`=" + "'" + newpwd + "'" + "WHERE `username`=" + "'" + username + "'";
					stmt.executeUpdate(sql);
					Shell.changeWikiPwd(username, newpwd);
					
			        PDResults.addProperty("PDResults", "Your password has been changed successfully.");					
				}else{
					PDResults.addProperty("exception", "Please make sure your current password is correct.");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	   
		}
		return PDResults.toString();
	}

	public static void main(String[] args){
		// TODO Auto-generated method stub
       MysqlDriver test = new MysqlDriver();
       test.updatePWD("cody", "123", "cody");

	}

}
