package databasetest.test;

import java.sql.CallableStatement;
import java.sql.ResultSet;

/*Syntax										Stores Procedures
 * {call procedure_name() }					Accept no parameters and return no value
 * {call procedure_name(?, ?) }				Accept two parameters and return no value
 * {? = call procedure_name() }				Accept no parameters and return value
 * {? = call procedure_name(?) }			Accept one parameter and return value
 */
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.testng.Assert;
import org.testng.annotations.Test;

import databasetest.testcomponents.BaseTest;

public class StoredProcedureValidation extends BaseTest{
	Statement s;
	ResultSet rs;
	ResultSet rs1;
	ResultSet rs2;
	CallableStatement cs;
	
	@Test(priority = 1, groups = { "Smoke" })
	void test_storedProcedureExists() throws SQLException {
		s = con.createStatement();	
		
		//ResultSet rs = s.executeQuery("select * from credentials where scenario='zerobalancecard'");
		rs = s.executeQuery("SHOW PROCEDURE STATUS WHERE Name='SelectAllCustomers'");
		rs.next();
		while(rs.next()) {
			Assert.assertEquals(rs.getString("Name"), "SelectAllCustomers");			
		}
	}
	
	@Test(priority=2)
	void test_SelectAllCustomers() throws SQLException {
		cs= con.prepareCall("{CALL SelectAllCustomers()}");
		rs1 = cs.executeQuery();
		s = con.createStatement();	
		rs2 = s.executeQuery("SELECT * FROM customers");
		Assert.assertTrue(compareResultSets(rs1, rs2));
	}
	
	@Test(priority=3)
	void test_SelectAllCustomersByCity() throws SQLException {
		cs= con.prepareCall("{CALL SelectAllCustomersByCity(?)}");
		cs.setString(1, "Singapore");
		rs1 = cs.executeQuery();
		s = con.createStatement();	
		rs2 = s.executeQuery("SELECT * FROM customers WHERE city = 'Singapore'");
		Assert.assertTrue(compareResultSets(rs1, rs2));
	}
	
	@Test(priority=4)
	void test_SelectAllCustomersByCityAndPin() throws SQLException {
		cs= con.prepareCall("{CALL SelectAllCustomersByCityAndPin(?, ?)}");
		cs.setString(1, "Singapore");
		cs.setString(2, "079903");
		rs1 = cs.executeQuery();
		s = con.createStatement();	
		rs2 = s.executeQuery("SELECT * FROM customers WHERE city = 'Singapore' and postalCode = '079903'");
		Assert.assertTrue(compareResultSets(rs1, rs2));
	}
	
	@Test(priority=5)
	void get_order_by_cust() throws SQLException{
		cs = con.prepareCall("CALL get_order_by_cust(?,?,?,?,?)");
		cs.setInt(1, 141);
		cs.registerOutParameter(2, Types.INTEGER);
		cs.registerOutParameter(3, Types.INTEGER);
		cs.registerOutParameter(4, Types.INTEGER);
		cs.registerOutParameter(5, Types.INTEGER);
		cs.executeQuery();

		int shipped = cs.getInt(2);
		int cancelled = cs.getInt(3);
		int resolved = cs.getInt(4);
		int disputed = cs.getInt(5);
		
		//System.out.println(shipped + " " + cancelled + " " + resolved + " " + disputed);
		s = con.createStatement();
		rs = s.executeQuery("select \r\n"
				+ "(select count(*) as 'shipped' from orders where customerNumber=141 and status= 'Shipped') as Shipped,\r\n"
				+ "(select count(*) as 'cancelled' from orders where customerNumber=141 and status= 'Cancelled') as Cancelled,\r\n"
				+ "(select count(*) as 'resolved' from orders where customerNumber=141 and status= 'Resolved') as Resolved,\r\n"
				+ "(select count(*) as 'disputed' from orders where customerNumber=141 and status= 'Disputed') as Disputed;");
		
		rs.next();
		int exp_shipped = rs.getInt("Shipped");
		int exp_cancelled = rs.getInt("Cancelled");
		int exp_resolved = rs.getInt("Resolved");
		int exp_disputed = rs.getInt("Disputed");
		if(shipped==exp_shipped && cancelled==exp_cancelled && resolved==exp_resolved && disputed==exp_disputed)
			Assert.assertTrue(true);
		else
			Assert.assertTrue(false);		
	}
	
	@Test(priority=6)
	void getCustomerShipping() throws SQLException{
		cs = con.prepareCall("CALL getCustomerShipping(?,?)");
		cs.setInt(1, 112);
		cs.registerOutParameter(2, Types.VARCHAR);
		cs.executeQuery();

		String shippingTime = cs.getString(2);
		
		//System.out.println(shipped + " " + cancelled + " " + resolved + " " + disputed);
		s = con.createStatement();
		rs = s.executeQuery("Select country,\r\n"
				+ "case\r\n"
				+ "    When country='USA' then '2-day Shipping'\r\n"
				+ "    When country='USA' then '2-day Shipping'\r\n"
				+ "    Else '5-day Shipping'\r\n"
				+ "End as ShippingTime\r\n"
				+ "    From customers where customerNumber = 112");		
		rs.next();
		String exp_ShippingTime = rs.getString("ShippingTime");
		Assert.assertEquals(shippingTime, exp_ShippingTime);
	}
	
	
}
