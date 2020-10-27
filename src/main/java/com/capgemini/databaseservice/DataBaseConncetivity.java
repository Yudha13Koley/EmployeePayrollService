package com.capgemini.databaseservice;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

public class DataBaseConncetivity {
	public static void main(String[] args) {
		String jdbcUrl = "jdbc:mysql://localhost:3306/simple_employee_payroll";
		String userName = "root";
		String passWord = "Yudha@123";
		Connection conn;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Driver Loaded !");
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Cannot find the Driver in the classpath !", e);
		}

		listDrivers();

		try {
			System.out.println("Connecting to Database : " + jdbcUrl);
			conn = DriverManager.getConnection(jdbcUrl, userName, passWord);
			System.out.println("Connection is successful : " + conn);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void listDrivers() {
		Enumeration<Driver> driverList = DriverManager.getDrivers();
		while (driverList.hasMoreElements()) {
			Driver driverClass = (Driver) driverList.nextElement();
			System.out.println(" " + driverClass.getClass().getName());
		}

	}

}
