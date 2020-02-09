package com.utils;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.example.demo.model.OrderShipped;

public class AWSDBConnection {

	private static final String PUBLIC_DNS = "mydatabase1.cpxzs2rub2zs.eu-west-3.rds.amazonaws.com";
	private static final String PORT = "3306";
	private static final String DATABASE = "orderDataBase";
	private static final String REMOTE_DATABASE_USERNAME = "rubenaz";
	private static final String DATABASE_USER_PASSWORD = "gSo5f#9f5";
	
	// for testing
	public static void main(String[] args) {
//		Connection connection=connectJDBCToAWSEC2();
//		if(connection!=null) {
//			runTestQuery();
//		}
		isOrderNumberExist("19120310503EGFX");
	//	insertOrderNumber("test ","test");
	//	fetchAllData();
//		ArrayList<String> cDiscountOrderNumbers=new ArrayList<String>();
//		cDiscountOrderNumbers.add("1911111404BFYFY");
//		cDiscountOrderNumbers.add("1911111618BEEN1");
//		getAllOrderNumber(cDiscountOrderNumbers);
	}

	private static Connection connectJDBCToAWSEC2() {

		System.out.println("----MySQL JDBC Connection Testing -------");

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		Connection connection = null;

		try {
			connection = DriverManager.getConnection("jdbc:mysql://" + PUBLIC_DNS + ":" + PORT + "/" + DATABASE,
					REMOTE_DATABASE_USERNAME, DATABASE_USER_PASSWORD);
		} catch (SQLException e) {
			System.out.println("Connection Failed!:\n" + e.getMessage());
		}

		if (connection != null) {
			System.out.println("SUCCESS!!!! You made it, take control     your database now!");
		} else {
			System.out.println("FAILURE! Failed to make connection!");
		}
		return connection;
	}

	// method to check order number exist in DB or not in order_shipped table
	public static boolean isOrderNumberExist(String order_number) {
		Connection connection = connectJDBCToAWSEC2();
		PreparedStatement statement = null;
		boolean status = true;
		try {
			String sql = "SELECT * FROM orderDataBase.order_shipped where  order_number=?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, order_number);
			ResultSet rs = statement.executeQuery();

			// STEP 5: Extract data from result set
			if(rs.next()==false) {
				status = false;
			}
			// STEP 6: Clean-up environment
			rs.close();
			statement.close();
			connection.close();
			System.out.println("val"+status);
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (statement != null)
					statement.close();
			} catch (SQLException se2) {
			} // nothing we can do
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try
		return status;
	}

	// method to insert order number after placing order
	public static void insertOrderNumber(String order_number, String vendorNumber) {
		Connection connection = connectJDBCToAWSEC2();
		PreparedStatement statement = null;
		try {
			String sql = "insert into orderDataBase.order_shipped (order_number, vendor_number, shipped) values(?,?,?);";

			statement = connection.prepareStatement(sql);
			statement.setString(1, order_number);
			statement.setString(2, vendorNumber);
			statement.setString(3, "false");
			int recordsInserted = statement.executeUpdate();
			if (recordsInserted>0) {
				System.out.println("Record inserted successfully with order_number "+order_number+" vendorNumber "+vendorNumber);
			}
			statement.close();
			connection.close();
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (statement != null)
					statement.close();
			} catch (SQLException se2) {
			} // nothing we can do
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		}
	}
	
	public static boolean fetchAllData() {
		Connection connection = connectJDBCToAWSEC2();
		PreparedStatement statement = null;
		boolean status = true;
		try {
			String sql = "SELECT * FROM orderDataBase.order_shipped";
			statement = connection.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			
			rs.close();
			statement.close();
			connection.close();
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (statement != null)
					statement.close();
			} catch (SQLException se2) {
			} // nothing we can do
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try
		return status;
	}
	
	public static Map<String,String> getAllOrderNumber(ArrayList<String> cDiscountOrderNumbers, String tableName) {
		Map<String,String> orderNumbersMap=new HashMap<String, String>();
		Connection connection = connectJDBCToAWSEC2();
		PreparedStatement statement = null;
		try {
			String sql = "select order_number,vendor_number from orderDataBase."+tableName+" where order_number in (?)";
			sql=any(sql, cDiscountOrderNumbers.size());
			System.out.println("sql "+sql);
			statement = connection.prepareStatement(sql);
			for (int i = 0; i < cDiscountOrderNumbers.size(); i++) {
				statement.setString(i+1, cDiscountOrderNumbers.get(i));
			}
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				orderNumbersMap.put(rs.getString("order_number"), rs.getString("vendor_number"));
				System.out.println("order number "+rs.getString("order_number"));
				System.out.println("vendor number "+rs.getString("vendor_number"));
			}
			rs.close();
			statement.close();
			connection.close();
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			
			// finally block used to close resources
			try {
				if (statement != null)
					statement.close();
			} catch (SQLException se2) {
			} // nothing we can do
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try
		return orderNumbersMap;
	}
	
	public static String any(String sql, final int params) {
	    // Create a comma-delimited list based on the number of parameters.
	    final StringBuilder sb = new StringBuilder(
	            new String(new char[params]).replace("\0", "?,")
	    );
	    sb.setLength(Math.max(sb.length() - 1, 0));

	    if (sb.length() > 1) {
	        sql = sql.replace("(?)", "(" + sb + ")");
	    }

	    // Return the modified comma-delimited list of parameters.
	    return sql;
	}
	
	public static Map<String,OrderShipped> getAllOrderNumber(String tableName) {
		Map<String,OrderShipped> orderNumbersMap=new HashMap<String, OrderShipped>();
		Connection connection = connectJDBCToAWSEC2();
		PreparedStatement statement = null;
		try {
			String sql = "select * from orderDataBase."+tableName;
			statement = connection.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				if(tableName.equalsIgnoreCase("order_shipped")) {
					OrderShipped ob=new OrderShipped(rs.getString("order_number"), 
							rs.getString("shipped"), 
							rs.getString("tracking_number"), 
							rs.getString("trackingurl"), 
							rs.getString("transporter_name"), 
							rs.getString("vendor_number"),new Date());
					orderNumbersMap.put(rs.getString("order_number"), ob);
				}
				else {
					OrderShipped ob=new OrderShipped(rs.getString("order_number"), 
							rs.getString("shipped"), 
							null, 
							rs.getString("tracking_url"), 
							null, 
							rs.getString("vendor_number"),new Date());
					orderNumbersMap.put(rs.getString("order_number"), ob);
				}
				
			}
			rs.close();
			statement.close();
			connection.close();
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			
			// finally block used to close resources
			try {
				if (statement != null)
					statement.close();
			} catch (SQLException se2) {
			} // nothing we can do
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try
		return orderNumbersMap;
	}
}
