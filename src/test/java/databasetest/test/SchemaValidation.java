package databasetest.test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.testng.Assert;
import org.testng.annotations.Test;


import databasetest.testcomponents.BaseTest;

public class SchemaValidation extends BaseTest{
	Statement s;
	ResultSet rs;
	
	@Test(groups = { "Smoke" }, priority=1)
	public void tableNameValidation() throws SQLException {			
		//ResultSet rs = s.executeQuery("select * from credentials where scenario='zerobalancecard'");
		ResultSet rs = s.executeQuery("show tables");
		String tableName[] = {"credentials", "customers", "departments", "employees", "offices", "orderdetails", "orders", "payments", "productlines", "products", "regions"};
		int i=0;
		while(rs.next()) {
			System.out.println(rs.getString("Tables_in_classicmodels"));
			String actualTableName = rs.getString("Tables_in_classicmodels");
			Assert.assertEquals(actualTableName, tableName[i]);
			i++;
		}
	}
	
	
}
