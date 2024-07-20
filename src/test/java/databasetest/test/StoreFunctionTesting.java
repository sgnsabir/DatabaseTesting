package databasetest.test;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.testng.Assert;
import org.testng.annotations.Test;

import databasetest.testcomponents.BaseTest;

public class StoreFunctionTesting extends BaseTest {
	ResultSet rs;
	ResultSet rs1;
	ResultSet rs2;
	CallableStatement cs;

	@Test(priority = 1)
	void test_storedFunctionExists() throws SQLException {
		rs = con.createStatement().executeQuery("SHOW FUNCTION STATUS WHERE Name='CustomerLevel'");
		rs.next();
		Assert.assertEquals(rs.getString("Name"), "customerLevel");
	}

	@Test(priority = 2)
	void test_CustomerLevel_with_SQLStatement() throws SQLException {
		rs1 = con.createStatement().executeQuery("SELECT customerName, customerLevel(creditLimit) FROM customers");
		rs2 = con.createStatement().executeQuery("SELECT customerName,\r\n"
				+ "CASE\r\n"
				+ "WHEN creditLimit > 50000 THEN 'PLATINUM'\r\n"
				+ "WHEN creditLimit > 10000 AND creditLimit<50000 THEN 'GOLD'\r\n"
				+ "WHEN creditLimit <10000 THEN 'SILVER'\r\n"
				+ "END as customerlevel FROM customers");
		System.out.println(rs1);
		Assert.assertTrue(compareResultSets(rs1, rs2));
		
	}
	
	@Test(priority = 3)
	void test_CustomerLevel_with_StoredProcedure() throws SQLException {
		cs = con.prepareCall("{CALL getCustomerLevel(?,?)}");
		cs.setInt(1, 131);
		cs.registerOutParameter(2, Types.INTEGER);
		cs.executeQuery();
		String customerLevel = cs.getString(2);
		rs = con.createStatement().executeQuery("SELECT customerName,\r\n"
				+ "CASE\r\n"
				+ "WHEN creditLimit > 50000 THEN 'PLATINUM'\r\n"
				+ "WHEN creditLimit > 10000 AND creditLimit<50000 THEN 'GOLD'\r\n"
				+ "WHEN creditLimit <10000 THEN 'SILVER'\r\n"
				+ "END as customerlevel FROM customers WHERE customerNumber=131");	
		rs.next();
		String exp_customerLevel = rs.getString("customerlevel");
		Assert.assertEquals(customerLevel, exp_customerLevel);
	}

}
