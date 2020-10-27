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
	private static EmployeePayrollDBService empployeePayrollDBService;

	private EmployeePayrollDBService() {
	}

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

	private List<EmployeePayrollData> readDataForASQL(String sql) throws DataBaseSQLException {
		List<EmployeePayrollData> emplist = new ArrayList<>();
		ResultSet result = null;
		try {
			result = getPrepareStatementInstance(sql).executeQuery();
			emplist = getListOfEntries(result, emplist);
			preparedStatement.close();
		} catch (SQLException e) {
			throw new DataBaseSQLException(e.getMessage());
		}
		return emplist;
	}

	private List<EmployeePayrollData> getListOfEntries(ResultSet result, List<EmployeePayrollData> emplist)
			throws SQLException {
		while (result.next()) {
			int id = result.getInt("id");
			String name = result.getString("name");
			double salary = result.getDouble("salary");
			LocalDate startdate = result.getDate("start").toLocalDate();
			char gender = result.getString("gender").charAt(0);
			emplist.add(new EmployeePayrollData(id, name, salary, startdate, gender));
		}
		return emplist;
	}

	public static EmployeePayrollDBService getDBServiceInstance() {
		if (empployeePayrollDBService == null)
			return new EmployeePayrollDBService();
		else
			return empployeePayrollDBService;
	}

	public List<EmployeePayrollData> readData() throws DataBaseSQLException {
		String sql = "SELECT*FROM employee_payroll ;";
		return readDataForASQL(sql);
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

	public List<EmployeePayrollData> readDataForJoiningDates() throws DataBaseSQLException {
		String sql = "SELECT*FROM employee_payroll WHERE start BETWEEN CAST('2020-01-01' AS DATE) AND DATE(NOW()) ;";
		return readDataForASQL(sql);
	}

	public boolean addColumnInDatabase() throws DataBaseSQLException {
		String sql = "ALTER TABLE employee_payroll ADD gender CHAR(1) CHECK (gender='M' OR gender='F') AFTER name;";
		return executeSql(sql);
	}

	public boolean updateGender() throws DataBaseSQLException {
		String sql = "UPDATE employee_payroll SET gender='M' WHERE name='Bill' or name='Charlie' ;";
		boolean result = executeSql(sql);
		String sql2 = "UPDATE employee_payroll SET gender='F' WHERE name='Terisa' ;";
		boolean result2 = executeSql(sql2);
		return result && result2;
	}

	private boolean executeSql(String sql) throws DataBaseSQLException {
		try {
			getPrepareStatementInstance(sql).execute();
			preparedStatement.close();
			return true;
		} catch (SQLException e) {
			throw new DataBaseSQLException(e.getMessage());
		}
	}

}
