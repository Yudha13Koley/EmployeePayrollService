package com.capgemini.databaseservice;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.capgemini.exceptions.DataBaseSQLException;
import com.capgemini.payrolldata.EmployeePayrollData;

public class EmployeePayrollDBService {
	private int connectionCounter = 0;
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

	private synchronized Connection getConnection() throws SQLException {
		connectionCounter++;
		String jdbcUrl = "jdbc:mysql://localhost:3306/payroll_service";
		String userName = "root";
		String passWord = "Yudha@123";
		Connection conn = null;
		System.out.println("Processing Thread : " + Thread.currentThread().getName()
				+ " Connecting to Database with id : " + connectionCounter);
		conn = DriverManager.getConnection(jdbcUrl, userName, passWord);
		System.out.println("Processing Thread : " + Thread.currentThread().getName() + " ID is " + connectionCounter
				+ "Connecting is successful : " + conn);
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
			int id = result.getInt("employee_id");
			String company_name = result.getString("company_name");
			String name = result.getString("name");
			String address = result.getString("address");
			double salary = result.getDouble("basic_pay");
			LocalDate startdate = result.getDate("start").toLocalDate();
			char gender = result.getString("gender").charAt(0);
			String[] departments = result.getString("departments").split(",");
			List<Integer> department_ids = new ArrayList<>();
			for (String s : departments) {
				department_ids.add(Integer.parseInt(s));
			}
			emplist.add(new EmployeePayrollData(id, company_name, name, address, salary, startdate, gender,
					department_ids));
		}
		return emplist;
	}

	public List<EmployeePayrollData> readData() throws DataBaseSQLException {
		String sql = "Select a.employee_id,b.company_name,name,address,gender,start,phone_no,c.basic_pay,group_concat(d.department_id) as departments FROM "
				+ "employee_details a,companies b,employee_payroll c,employee_department d "
				+ "WHERE a.employee_id=c.employee_id AND a.company_id=b.company_id AND a.employee_id=d.employee_id "
				+ " GROUP BY a.employee_id;";
		return readDataForASQL(sql);
	}

	public int setSalaryOfEmployee(String name, double salary) throws DataBaseSQLException {
		double deductions = 0.2 * salary;
		double taxable_pay = salary - deductions;
		double tax = 0.1 * taxable_pay;
		double net_pay = taxable_pay - tax;
		String sql = String.format(
				"UPDATE employee_payroll SET "
						+ "basic_pay=%.2f,deductions=%.2f,taxable_pay=%.2f,tax=%.2f,net_pay=%.2f "
						+ "WHERE employee_id= (SELECT employee_id FROM employee_details WHERE name='%s');",
				salary, deductions, taxable_pay, tax, net_pay, name);
		try {
			int n = getPrepareStatementInstance(sql).executeUpdate();
			preparedStatement.close();
			return n;
		} catch (SQLException e) {
			throw new DataBaseSQLException(e.getMessage());
		}
	}

	public List<EmployeePayrollData> readDataForJoiningDates() throws DataBaseSQLException {
		String sql = "Select a.employee_id,b.company_name,name,address,gender,start,phone_no,c.basic_pay,group_concat(d.department_id) as departments FROM "
				+ "employee_details a,companies b,employee_payroll c,employee_department d "
				+ "WHERE a.employee_id=c.employee_id AND a.company_id=b.company_id AND a.employee_id=d.employee_id "
				+ "AND a.start BETWEEN CAST('2020-01-01' AS DATE) AND DATE(NOW()) GROUP BY a.employee_id ; ";
		return readDataForASQL(sql);
	}

	public EmployeePayrollData getEmployeeFromDatabase(String name) throws DataBaseSQLException {
		String sql = String.format(
				"Select a.employee_id,b.company_name,name,address,gender,start,phone_no,c.basic_pay,group_concat(d.department_id) as departments FROM "
						+ "employee_details a,companies b,employee_payroll c,employee_department d "
						+ "WHERE a.employee_id=c.employee_id AND a.company_id=b.company_id AND a.employee_id=d.employee_id AND a.name='%s' "
						+ " GROUP BY a.employee_id;",
				name);
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
		String sql = "SELECT a.gender,AVG(b.basic_pay) as average_salary FROM employee_details a,employee_payroll b "
				+ "WHERE a.employee_id=b.employee_id GROUP BY a.gender ;";
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

	public EmployeePayrollData addEmployeeToPayroll(int company_id, String name, char gender, double salary,
			LocalDate start, List<Integer> department_ids) throws DataBaseSQLException {
		String sql = String.format(
				"INSERT INTO employee_details (company_id,name,gender,start) VALUES (%d,'%s','%s','%s'); ", company_id,
				name, gender, Date.valueOf(start));
		EmployeePayrollData empData = null;
		Connection connection = null;
		Statement statement = null;
		int employee_id = -1;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			int rowAffected = statement.executeUpdate(sql, preparedStatement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next()) {
					employee_id = resultSet.getInt(1);
				}
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new DataBaseSQLException(e.getMessage());
			}
			throw new DataBaseSQLException(e.getMessage());
		}
		double deductions = 0.2 * salary;
		double taxable_pay = salary - deductions;
		double tax = 0.1 * taxable_pay;
		double net_pay = taxable_pay - tax;

		String sqlToAddPayrollDetails = String.format(
				"INSERT INTO employee_payroll (employee_id,basic_pay,deductions,taxable_pay,tax,net_pay)"
						+ "VALUES (%d,%.2f,%.2f,%.2f,%.2f,%.2f) ;",
				employee_id, salary, deductions, taxable_pay, tax, net_pay);
		try {
			int rowAffected = statement.executeUpdate(sqlToAddPayrollDetails, preparedStatement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next()) {
					employee_id = resultSet.getInt(1);
				}
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new DataBaseSQLException(e.getMessage());
			}
			throw new DataBaseSQLException(e.getMessage());
		}
		for (int department_id : department_ids) {
			String sqlToAddDepartment = String.format(
					"INSERT INTO employee_department(employee_id,department_id) " + "VALUES (%d,%d) ;", employee_id,
					department_id);
			try {
				int rowAffected = statement.executeUpdate(sqlToAddDepartment, statement.RETURN_GENERATED_KEYS);
				if (rowAffected == 1) {
					ResultSet resultSet = statement.getGeneratedKeys();
					if (resultSet.next()) {
						employee_id = resultSet.getInt(1);
					}
				}
			} catch (SQLException e) {
				try {
					connection.rollback();
				} catch (SQLException e1) {
					throw new DataBaseSQLException(e.getMessage());
				}
				throw new DataBaseSQLException(e.getMessage());
			}
		}
		empData = new EmployeePayrollData(employee_id, "Capgemini", name, "TBD", salary, start, gender, department_ids);
		try {
			connection.commit();
		} catch (SQLException e) {
			throw new DataBaseSQLException(e.getMessage());
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				throw new DataBaseSQLException(e.getMessage());
			}
		}
		return empData;
	}

	public boolean deleteEmployee(String name) throws DataBaseSQLException {
		String sql = String.format(
				"DELETE FROM employee_payroll WHERE employee_id=(SELECT employee_id FROM employee_details WHERE name='%s') ;",
				name);
		String sql2 = String.format(
				"DELETE FROM employee_department WHERE employee_id=(SELECT employee_id FROM employee_details WHERE name='%s') ;",
				name);
		String sql3 = String.format("UPDATE employee_details SET is_active=false WHERE name='%s' ;", name);
		try {
			preparedStatement = getPrepareStatementInstance(sql);
			boolean b = preparedStatement.execute();
			boolean b2 = preparedStatement.execute(sql2);
			boolean b3 = preparedStatement.execute(sql3);
			preparedStatement.close();
			return !(b && b2 && b3);
		} catch (SQLException e) {
			throw new DataBaseSQLException(e.getMessage());
		}

	}

}
