package PlanetaryDefense.PD.driver;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;


public class MysqlDriver {
	Connection connection = null;
	Statement stmt = null;
	JsonObject PDResults = new JsonObject();
	
  public Connection getConnection()
  {
    try {
      Class.forName("com.mysql.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      System.out.println("Where is your MySQL JDBC Driver?");
      e.printStackTrace();
      return null;
    }

    try {
      connection = DriverManager
      .getConnection("jdbc:mysql://127.0.0.1:3306/test","root", "cody0924");

    } catch (SQLException e) {
      System.out.println("Connection Failed! Check output console");
      e.printStackTrace();
      return null;
    }
    
    return connection;
  }
	
	public String getUserByEmail(String email){
	  String user = null;
		if (connection != null) {
			try {
				stmt = connection.createStatement();
				String sql = "SELECT username FROM test.users WHERE email=" + "'" + email + "'";
				ResultSet rs = stmt.executeQuery(sql);
				while(rs.next()){
					//Retrieve by column name
					user = rs.getString("username");			        
				}
				rs.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}   
		}
		return user;
	}
	
	 public List<String> getRoleByUser(String user){
	   List<String> roles = new ArrayList<String>();
	    if (connection != null) {
	      try {
	        stmt = connection.createStatement();
	        String sql = "SELECT role FROM test.user_roles WHERE username=" + "'" + user + "'";
	        ResultSet rs = stmt.executeQuery(sql);
	        while(rs.next()){
	          //Retrieve by column name
	          String role = rs.getString("role");
	          roles.add(role);
	        }
	        rs.close();

	      } catch (SQLException e) {
	        e.printStackTrace();
	      }   
	    }
	    return roles;
	  }

}
