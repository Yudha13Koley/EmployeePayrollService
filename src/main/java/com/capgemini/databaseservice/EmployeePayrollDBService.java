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
import java.util.Arrays;
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
			Connection connection = getConnection();
			Statement statement = connection.createStatement();
			int n = statement.executeUpdate(sql);
			connection.close();
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
		try {
			Connection connection = getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultset = statement.executeQuery(sql);
			empList = getListOfEntries(resultset, empList);
			connection.close();
		} catch (SQLException e) {
			throw new DataBaseSQLException(e.getMessage());
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
		Connection[] connection = new Connection[] { null };
		try {
			connection[0] = this.getConnection();
			connection[0].setAutoCommit(false);
		} catch (SQLException e2) {
			e2.printStackTrace();
		}
		Integer[] employee_id = new Integer[] { -1 };
		boolean[] status = { false, false };
		synchronized (this) {
			employee_id[0] = this.insertEmployeeInDatabaseAndGetId(sql, connection[0]);

			Runnable task1 = () -> {
				this.insertPayrollDetailsForAID(employee_id[0], salary, connection[0]);
				status[0] = true;
			};
			Thread thread1 = new Thread(task1);
			thread1.start();

			Runnable task2 = () -> {
				this.addEmployeeDepartmentDetailsInDatabase(employee_id[0], department_ids, connection[0]);
				status[1] = true;
			};
			Thread thread2 = new Thread(task2);
			thread2.start();
		}
		while (!(status[0] == true && status[1] == true)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		empData = new EmployeePayrollData(employee_id[0], "Capgemini", name, "TBD", salary, start, gender,
				department_ids);
		try {
			connection[0].commit();
		} catch (SQLException e) {
			throw new DataBaseSQLException(e.getMessage());
		} finally {
			try {
				if (connection != null)
					connection[0].close();
			} catch (SQLException e) {
				throw new DataBaseSQLException(e.getMessage());
			}
		}
		return empData;
	}

	private void addEmployeeDepartmentDetailsInDatabase(Integer id, List<Integer> department_ids,
			Connection connection) {
		for (int department_id : department_ids) {
			String sqlToAddDepartment = String.format(
					"INSERT INTO employee_department(employee_id,department_id) " + "VALUES (%d,%d) ;", id,
					department_id);
			try {
				getResultSetForSql(sqlToAddDepartment, connection);
			} catch (DataBaseSQLException e) {
				try {
					connection.rollback();
				} catch (SQLException e1) {
					e.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}

	private void insertPayrollDetailsForAID(Integer id, double salary, Connection connection) {
		double deductions = 0.2 * salary;
		double taxable_pay = salary - deductions;
		double tax = 0.1 * taxable_pay;
		double net_pay = taxable_pay - tax;
		String sqlToAddPayrollDetails = String
				.format("INSERT INTO employee_payroll (employee_id,basic_pay,deductions,taxable_pay,tax,net_pay)"
						+ "VALUES (%d,%.2f,%.2f,%.2f,%.2f,%.2f) ;", id, salary, deductions, taxable_pay, tax, net_pay);
		try {
			getResultSetForSql(sqlToAddPayrollDetails, connection);
		} catch (DataBaseSQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e.printStackTrace();
			}
			e.printStackTrace();
		}

	}

	private Integer insertEmployeeInDatabaseAndGetId(String sql, Connection connection) {
		ResultSet resultSet;
		try {
			resultSet = getResultSetForSql(sql, connection);
			if (resultSet.next()) {
				return resultSet.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private synchronized ResultSet getResultSetForSql(String sql, Connection connection) throws DataBaseSQLException {
		try {
			preparedStatement = connection.prepareStatement(sql, preparedStatement.RETURN_GENERATED_KEYS);
			int rowAffected = preparedStatement.executeUpdate();
			if (rowAffected == 1) {
				return preparedStatement.getGeneratedKeys();
			}
		} catch (SQLException e) {
			throw new DataBaseSQLException(e.getMessage());
		}
		return null;
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
