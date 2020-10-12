package com.capgemini.payrollservice;

import java.util.*;

import com.capgemini.payrolldata.EmployeePayrollData;

public class EmployeePayrollService {
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	private List<EmployeePayrollData> employeePayrollList;

	public EmployeePayrollService() {
		// TODO Auto-generated constructor stub
	}

	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
		this.employeePayrollList = employeePayrollList;
	}

	private void readData(Scanner sc) {
		System.out.println("Enter Employee ID : ");
		int id = sc.nextInt();
		System.out.println("Enter Employee Name : ");
		String name = sc.next();
		System.out.println("Enter Employee Salary : ");
		double salary = sc.nextDouble();
		employeePayrollList.add(new EmployeePayrollData(id, name, salary));
	}

	private void writeData() {
		System.out.println("Writing Employee Payroll Details To The Console : ");
		System.out.println(employeePayrollList);
	}

	public static void main(String[] args) {
		ArrayList<EmployeePayrollData> empdetailslist = new ArrayList<>();
		EmployeePayrollService EPS = new EmployeePayrollService(empdetailslist);
		Scanner sc = new Scanner(System.in);
		EPS.readData(sc);
		EPS.writeData();
	}

}
