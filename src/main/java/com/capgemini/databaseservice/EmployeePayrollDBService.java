package com.capgemini.databaseservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.capgemini.exceptions.DataBaseSQLException;
import com.capgemini.payrolldata.EmployeePayrollData;

public class EmployeePayrollDBService {

	private PreparedStatement preparedStatement;

	private PreparedStatement getPrepareStatementInstance(String sql) throws DataBaseSQLException {
		try {
			Connection connection = this.getConnection();
			preparedStatement = connection.prepareStatement(sql);
			return preparedStatement;
		} catch (SQLException e) {
			throw new DataBaseSQLException(e.getMessage());
		}
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

	public List<EmployeePayrollData> readData() throws DataBaseSQLException {
		String sql = "SELECT*FROM employee_payroll ;";
		List<EmployeePayrollData> emplist = new ArrayList<>();
		ResultSet result = null;
		try {
			result = getPrepareStatementInstance(sql).executeQuery();
			while (result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				double salary = result.getDouble("salary");
				LocalDate startdate = result.getDate("start").toLocalDate();
				emplist.add(new EmployeePayrollData(id, name, salary, startdate));
			}
			preparedStatement.close();
		} catch (SQLException e) {
			throw new DataBaseSQLException(e.getMessage());
		}
		return emplist;

	}

	public int setSalaryOfEmployee(String name, double salary) throws DataBaseSQLException {
		String sql = String.format("UPDATE employee_payroll SET salary=%.2f WHERE name='%s' ;", salary, name);
		try {
			int n = getPrepareStatementInstance(sql).executeUpdate();
			preparedStatement.close();
			return n;
		} catch (SQLException e) {
			throw new DataBaseSQLException(e.getMessage());
		}
	}

}
