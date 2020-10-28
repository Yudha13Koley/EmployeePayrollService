package com.capgemini.databaseservice;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.capgemini.exceptions.DataBaseSQLException;
import com.capgemini.payrolldata.EmployeePayrollData;

public class EmployeePayrollDBService {

	private PreparedStatement preparedStatement;
	private static EmployeePayrollDBService empployeePayrollDBService;

	private EmployeePayrollDBService() {
	}

	public static EmployeePayrollDBService getDBServiceInstance() {
		if (empployeePayrollDBService == null)
			return new EmployeePayrollDBService();
		else
			return empployeePayrollDBService;
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

	public EmployeePayrollData getEmployeeFromDatabase(String name) throws DataBaseSQLException {
		String sql = String.format("SELECT*FROM employee_payroll WHERE name='%s';", name);
		List<EmployeePayrollData> empList = new LinkedList<>();
		preparedStatement = getPrepareStatementInstance(sql);
		try {
			ResultSet resultset = preparedStatement.executeQuery();
			empList = getListOfEntries(resultset, empList);
		} catch (SQLException e) {
			throw new DataBaseSQLException(e.getMessage());
		} finally {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				throw new DataBaseSQLException(e.getMessage());
			}
		}
		return empList.get(0);
	}

	public Map<Character, Double> readAVGSalariesByGender() throws DataBaseSQLException {
		String sql = "SELECT gender,AVG(salary) as average_salary FROM employee_payroll GROUP BY gender ;";
		try {
			ResultSet result = getPrepareStatementInstance(sql).executeQuery();
			Map<Character, Double> resultMap = new HashMap<>();
			while (result.next()) {
				char gender = result.getString("gender").charAt(0);
				double average_salary = result.getDouble("average_salary");
				resultMap.put(gender, average_salary);
			}
			preparedStatement.close();
			return resultMap;
		} catch (SQLException e) {
			throw new DataBaseSQLException(e.getMessage());
		}
	}

	public EmployeePayrollData addEmployeeToPayroll(String name, String gender, double salary, LocalDate start)
			throws DataBaseSQLException {
		String sql = String.format(
				"INSERT INTO employee_payroll (name,gender,salary,start) VALUES ('%s','%s',%.2f,'%s'); ", name, gender,
				salary, Date.valueOf(start));
		EmployeePayrollData empData = null;
		int employee_id = -1;
		try {
			preparedStatement = getPrepareStatementInstance(sql);
			int rowAffected = preparedStatement.executeUpdate(sql, preparedStatement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = preparedStatement.getGeneratedKeys();
				if (resultSet.next()) {
					employee_id = resultSet.getInt(1);
				}
			}
		} catch (SQLException e) {
			throw new DataBaseSQLException(e.getMessage());
		}
		double deductions = 0.2 * salary;
		double taxable_pay = salary - deductions;
		double tax = 0.1 * taxable_pay;
		double net_pay = taxable_pay - tax;

		String sqlToAddPayrollDetails = String.format(
				"INSERT INTO payroll_details(employee_id,basic_pay,deductions,taxable_pay,tax,net_pay)"
						+ "VALUES (%d,%.2f,%.2f,%.2f,%.2f,%.2f) ;",
				employee_id, salary, deductions, taxable_pay, tax, net_pay);
		try {
			int rowAffected = preparedStatement.executeUpdate(sqlToAddPayrollDetails,
					preparedStatement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = preparedStatement.getGeneratedKeys();
				if (resultSet.next()) {
					employee_id = resultSet.getInt(1);
				}
			}
			empData = new EmployeePayrollData(employee_id, name, salary, start, gender.charAt(0));

		} catch (SQLException e) {
			throw new DataBaseSQLException(e.getMessage());
		} finally {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				throw new DataBaseSQLException(e.getMessage());
			}
		}
		return empData;
	}

}
