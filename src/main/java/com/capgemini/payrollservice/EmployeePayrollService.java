package com.capgemini.payrollservice;

import java.util.*;

import com.capgemini.databaseservice.EmployeePayrollDBService;
import com.capgemini.exceptions.DataBaseSQLException;
import com.capgemini.fileioservice.EmployeePayrollFileIOService;
import com.capgemini.payrolldata.EmployeePayrollData;

public class EmployeePayrollService {
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	public List<EmployeePayrollData> employeePayrollList;

	public EmployeePayrollService() {
		// TODO Auto-generated constructor stub
	}

	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
		this.employeePayrollList = employeePayrollList;
	}

	public void readData(IOService fileIo) throws DataBaseSQLException {
		if (fileIo.equals(IOService.CONSOLE_IO)) {
			Scanner sc = new Scanner(System.in);
			System.out.println("Enter Employee ID : ");
			int id = sc.nextInt();
			System.out.println("Enter Employee Name : ");
			String name = sc.next();
			System.out.println("Enter Employee Salary : ");
			double salary = sc.nextDouble();
			sc.close();
			employeePayrollList.add(new EmployeePayrollData(id, name, salary));
		} else if (fileIo.equals(IOService.FILE_IO)) {
			this.employeePayrollList = new EmployeePayrollFileIOService().readDataFromFile(employeePayrollList);
		} else if (fileIo.equals(IOService.DB_IO)) {
			this.employeePayrollList = new EmployeePayrollDBService().readData();
		}
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

	public void updateSalaryOfAnEmployeeInDB(String name, double salary) {
		try {
			int result = new EmployeePayrollDBService().setSalaryOfEmployee(name, salary);
			System.out.println("No of rows updated : " + result);
		} catch (DataBaseSQLException e) {
			e.printStackTrace();
		}
		EmployeePayrollData empData = this.getEmployee(this.employeePayrollList, name);
		if (empData != null) {
			empData.setSalary(salary);
		}

	}

	private EmployeePayrollData getEmployee(List<EmployeePayrollData> list, String name) {
		return list.stream().filter(n -> n.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public boolean isEmployeeSyncWithDatabase(String name) {
		List<EmployeePayrollData> empList = new ArrayList<>();
		try {
			empList = new EmployeePayrollDBService().readData();
		} catch (DataBaseSQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getEmployee(empList, name).equals(getEmployee(employeePayrollList, name));
	}

}
