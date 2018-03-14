package uniks_project_microservice.authenticationservice;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import uniks_project_microservice.baseservice.DefaultHandler;

public class AuthenticationHandler extends DefaultHandler {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "java.sql.Driver";
	static String DB_URL = "jdbc:mysql://mysqldb:3306/";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "password";
	
	private boolean retried = false;

	public AuthenticationHandler(String hostname, String serviceName) {
		super(hostname, serviceName);

		if (getHostname() != null && getHostname().equals("localhost")) {
			System.out.println("App started outside docker");
			DB_URL = "jdbc:mysql://localhost:3306/";
		}
	}

	@Override
	public String processMessage(String message) {
		System.out.println("auth process message");
		return selectRecords();
	}

	private String selectRecords() {
		String jsonString = "{error:true}";
		Connection conn = null;
		Statement stmt = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// STEP 3: Open a connection
			System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL + "USERS", USER, PASS);
			System.out.println("Connected database successfully...");

			// STEP 4: Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();

			String sql = "SELECT * FROM REGISTRATION";
			ResultSet rs = stmt.executeQuery(sql);

			jsonString = ResultSetConverter.convert(rs).toString();
			System.out.println(jsonString);

			rs.close();
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
			
			//TODO fix
			if (!retried) {
				retried=true;
				createDatabase();
				createTable();
				insertData();
				selectRecords();
			}
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			} // do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try

		return jsonString;
	}

	public void insertData() {
		Connection conn = null;
		Statement stmt = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// STEP 3: Open a connection
			System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL + "USERS", USER, PASS);
			System.out.println("Connected database successfully...");

			// STEP 4: Execute a query
			System.out.println("Inserting records into the table...");
			stmt = conn.createStatement();
			try {
				String sql = "INSERT INTO REGISTRATION "
						+ "VALUES (100, 'Zara', 'Ali', 18)";
				stmt.executeUpdate(sql);
				sql = "INSERT INTO REGISTRATION "
						+ "VALUES (101, 'Mahnaz', 'Fatma', 25)";
				stmt.executeUpdate(sql);
				sql = "INSERT INTO REGISTRATION "
						+ "VALUES (102, 'Zaid', 'Khan', 30)";
				stmt.executeUpdate(sql);
				sql = "INSERT INTO REGISTRATION "
						+ "VALUES(103, 'Sumit', 'Mittal', 28)";
				stmt.executeUpdate(sql);
				System.out.println("Inserted records into the table...");
			} catch (SQLException e) {
				System.out.println("Entries already exist");
			}

		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			} // do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try

	}

	public void createTable() {
		Connection conn = null;
		Statement stmt = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// STEP 3: Open a connection
			System.out.println("Connecting to a selected database..." + DB_URL);
			conn = DriverManager.getConnection(DB_URL + "USERS", USER, PASS);
			System.out.println("Connected database successfully...");

			DatabaseMetaData meta = conn.getMetaData();
			ResultSet res = meta.getTables(null, null, "REGISTRATION",
					new String[]{"TABLE"});
			while (res.next()) {
				if (res.getString("TABLE_NAME").equals("REGISTRATION")) {
					System.out.println("TABLE REGISTRATION already exists");
					conn.close();
					return;
				}
				// System.out.println(
				// " "+res.getString("TABLE_CAT")
				// + ", "+res.getString("TABLE_SCHEM")
				// + ", "+res.getString("TABLE_NAME")
				// + ", "+res.getString("TABLE_TYPE")
				// + ", "+res.getString("REMARKS"));
			}

			// STEP 4: Execute a query
			System.out.println("Creating table in given database...");
			stmt = conn.createStatement();

			String sql = "CREATE TABLE REGISTRATION " + "(id INTEGER not NULL, "
					+ " first VARCHAR(255), " + " last VARCHAR(255), "
					+ " age INTEGER, " + " PRIMARY KEY ( id ))";

			stmt.executeUpdate(sql);
			System.out.println("Created table in given database...");
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			} // do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try

	}

	public void createDatabase() {
		Connection conn = null;
		Statement stmt = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// STEP 3: Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			// Connection connection = <your java.sql.Connection>
			ResultSet resultSet = conn.getMetaData().getCatalogs();

			// iterate each catalog in the ResultSet
			while (resultSet.next()) {
				// Get the database name, which is at position 1
				String databaseName = resultSet.getString(1);
				if (databaseName.equals("USERS")) {
					conn.close();
					System.out.println("Database USERS already exists");
					return;
				}
			}
			resultSet.close();

			// STEP 4: Execute a query
			System.out.println("Creating database...");
			stmt = conn.createStatement();

			String sql = "CREATE DATABASE USERS";
			stmt.executeUpdate(sql);
			System.out.println("Database created successfully...");
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			} // nothing we can do
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try

	}
}
