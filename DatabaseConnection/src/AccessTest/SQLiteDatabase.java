package AccessTest;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

public class SQLiteDatabase {

	final static Path SQLITE_DB_PATH = Paths.get(new File("").getAbsolutePath().toString(), "\\Database", "student.db");
	final static String DB_URL = "jdbc:sqlite://" + SQLITE_DB_PATH.toAbsolutePath();

	public static void main(String[] args) {
		// variables
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		PreparedStatement preparedStatemen = null;
		
		// 1. Load or register Oracle JDBC driver class
		try {			
			Class.forName("org.sqlite.JDBC");
			
		} catch(ClassNotFoundException cnfex) {
			System.out.println("Problem in loading or " +
					"registering MS Access JDBC driver");
			cnfex.printStackTrace();
		}
		
		// 2. Opening database connection
		try {
			
			// 2.A. Create and get connection using DriverManager Class
			connection = DriverManager.getConnection(DB_URL);
			
			// set auto commit false
			connection.setAutoCommit(false);
			
			// 2.B. Create JDBC Statement
			statement = connection.createStatement();

			// 2.C. Execute SQL and retrieve data into ResultSet
			resultSet = statement.executeQuery("SELECT * FROM Login");
			
			fetchAndDisplayResultSet(resultSet);
			String insertQuery = ("INSERT INTO LOGIN (USERNAME, PASSWORD, EMAIL, ID) VALUES (?, ?, ?, ?)");
			preparedStatemen = connection.prepareStatement(insertQuery);
			
			// insert data			
			for (int i = 2; i < 5; i++) {				
				StringBuilder username = new StringBuilder(); 
				username.append("Username " + LocalDateTime.now().toString());
				preparedStatemen.setString(1, username.toString());
				preparedStatemen.setString(2, "pass" + Math.random());
				preparedStatemen.setString(3, username.append("@mitrais.com").toString());
				preparedStatemen.setInt(4, i);
				preparedStatemen.addBatch();
			}
			
			preparedStatemen.executeBatch();
			
			resultSet = statement.executeQuery("SELECT * FROM Login");			
						
			fetchAndDisplayResultSet(resultSet);
			
			// delete data
			// get Ids
			resultSet = statement.executeQuery("SELECT Id FROM Login");
			resultSet.next();
			int toBeDeletedId = resultSet.getInt(1);
			System.out.println("to be deleted id: " + toBeDeletedId);
			
			preparedStatemen = connection.prepareStatement("DELETE FROM Login WHERE ID = ?");
			preparedStatemen.setInt(1, toBeDeletedId);
			preparedStatemen.executeUpdate();
			
			resultSet = statement.executeQuery("SELECT * FROM Login");			
			
			fetchAndDisplayResultSet(resultSet);
			
			connection.rollback();
			
			resultSet = statement.executeQuery("SELECT * FROM Login");			
			
			fetchAndDisplayResultSet(resultSet);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// close the database connection
			try {
				if (connection != null) {
					resultSet.close();
					statement.close();
					connection.close();
				}
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		}
	}
	
	private static void fetchAndDisplayResultSet(ResultSet resultSet) throws SQLException {
		System.out.println("ID\tUser Name\t\t\tEmail");
		
		// Enumerate result set
		while(resultSet.next()) {
			System.out.println(resultSet.getInt(1) + "\t" +
					resultSet.getString(2) + "\t\t" +
					resultSet.getString(4));
	
		}
	}

}
