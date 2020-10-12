package com.capgemini.payrollservice;

import java.util.*;

import com.capgemini.fileioservice.EmployeePayrollFileIOService;
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

	public void readData(Scanner sc) {
		System.out.println("Enter Employee ID : ");
		int id = sc.nextInt();
		System.out.println("Enter Employee Name : ");
		String name = sc.next();
		System.out.println("Enter Employee Salary : ");
		double salary = sc.nextDouble();
		employeePayrollList.add(new EmployeePayrollData(id, name, salary));
	}

	public void writeData(IOService iOService) {
		if (iOService.equals(IOService.CONSOLE_IO)) {
			System.out.println("Writing Employee Payroll Details To The Console : ");
			System.out.println(employeePayrollList);
		} else if (iOService.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileIOService().writeDataInFile(employeePayrollList);
		}
	}

	public static void main(String[] args) {
		ArrayList<EmployeePayrollData> empdetailslist = new ArrayList<>();
		EmployeePayrollService EPS = new EmployeePayrollService(empdetailslist);
		Scanner sc = new Scanner(System.in);
		EPS.readData(sc);

	}

	public long countEntries(IOService fileIo) {
		if (fileIo.equals(IOService.CONSOLE_IO)) {
			return employeePayrollList.size();
		} else if (fileIo.equals(IOService.FILE_IO)) {
			return new EmployeePayrollFileIOService().countEntriesFromFile();
		} else
			return 0;
	}

	public void printData(IOService fileIo) {
		if (fileIo.equals(IOService.CONSOLE_IO)) {
			System.out.println("Printing Data From Console Input");
			System.out.println(employeePayrollList);
		} else if (fileIo.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileIOService().printDataFromFile();
		}

	}

}
