package databasetest.testcomponents;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class BaseTest {
	String host = "localhost";
	String port = "3306";
	public Connection con;

	@BeforeClass(alwaysRun = true)
	public Connection dbConnectionSetup() throws SQLException {
		con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/classicmodels", "root",
				"root");
		return con;
	}

	@AfterClass(alwaysRun = true)
	public void tearDown() throws SQLException {
		con.close();
	}
	
	public boolean compareResultSets(ResultSet resultSet1, ResultSet resultSet2) throws SQLException {
		while(resultSet1.next()) {
			resultSet2.next();
			int count = resultSet1.getMetaData().getColumnCount();
			for(int i=1; i<=count; i++) {
				if(!StringUtils.equals(resultSet1.getString(i), resultSet2.getString(i))) {
					return false;
				}
			}
		}
		return true;
	}
	
	
}
