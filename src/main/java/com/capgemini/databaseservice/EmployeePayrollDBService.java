package com.capgemini.databaseservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.capgemini.exceptions.DataBaseSQLException;
import com.capgemini.payrolldata.EmployeePayrollData;

public class EmployeePayrollDBService {

	public List<EmployeePayrollData> readData() throws DataBaseSQLException {
		String sql = "SELECT*FROM employee_payroll ;";
		List<EmployeePayrollData> emplist = new ArrayList<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while (result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				double salary = result.getDouble("salary");
				LocalDate startdate = result.getDate("start").toLocalDate();
				emplist.add(new EmployeePayrollData(id, name, salary, startdate));
			}

		} catch (SQLException e) {
			throw new DataBaseSQLException(e.getMessage());
		}
		return emplist;

	}

	private Connection getConnection() throws SQLException {
		String jdbcUrl = "jdbc:mysql://localhost:3306/simple_employee_payroll";
		String userName = "root";
		String passWord = "Yudha@123";
		Connection conn;
		System.out.println("Connecting to Database : " + jdbcUrl);
		conn = DriverManager.getConnection(jdbcUrl, userName, passWord);
		System.out.println("Connection is successful : " + conn);
		return conn;
	}

}
