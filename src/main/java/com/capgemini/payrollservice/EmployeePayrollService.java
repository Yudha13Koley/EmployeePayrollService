package com.capgemini.payrollservice;

import java.time.LocalDate;
import java.util.*;

import com.capgemini.databaseservice.EmployeePayrollDBService;
import com.capgemini.exceptions.DataBaseSQLException;
import com.capgemini.fileioservice.EmployeePayrollFileIOService;
import com.capgemini.payrolldata.EmployeePayrollData;

public class EmployeePayrollService {

	private EmployeePayrollDBService employeePayrollDBService;
	public List<EmployeePayrollData> employeePayrollList;

	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	public EmployeePayrollService() {
		this.employeePayrollDBService = EmployeePayrollDBService.getDBServiceInstance();
	}

	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
		this();
		this.employeePayrollList = new ArrayList<>(employeePayrollList);
	}

	public EmployeePayrollData getEmployee(List<EmployeePayrollData> list, String name) {
		return list.stream().filter(n -> n.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
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
			this.employeePayrollList = employeePayrollDBService.readData();
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

	public void updateSalaryOfAnEmployeeInDB(String name, double salary, IOService ioservice) {
		if (ioservice.equals(IOService.DB_IO)) {
			try {
				int result = employeePayrollDBService.setSalaryOfEmployee(name, salary);
				System.out.println("No of rows updated : " + result);
			} catch (DataBaseSQLException e) {
				e.printStackTrace();
			}
			EmployeePayrollData empData = this.getEmployee(this.employeePayrollList, name);
			if (empData != null) {
				empData.setSalary(salary);
			}
		} else {
			EmployeePayrollData empData = this.getEmployee(this.employeePayrollList, name);
			if (empData != null) {
				empData.setSalary(salary);
			}
		}
	}

	public boolean isEmployeeSyncWithDatabase(String name) throws DataBaseSQLException {
		EmployeePayrollData empData = null;
		try {
			empData = employeePayrollDBService.getEmployeeFromDatabase(name);
		} catch (DataBaseSQLException e) {
			throw new DataBaseSQLException(e.getMessage());
		}
		return empData.equals(getEmployee(employeePayrollList, name));
	}

	public void readDataForACondition() throws DataBaseSQLException {
		this.employeePayrollList = employeePayrollDBService.readDataForJoiningDates();
	}

	public Map<Character, Double> readAVGSalaries() throws DataBaseSQLException {
		return employeePayrollDBService.readAVGSalariesByGender();
	}

	public void addEmployeeInDatabase(int company_id, String name, char gender, double salary, LocalDate start,
			List<Integer> departmentList) throws DataBaseSQLException {
		this.employeePayrollList.add(
				employeePayrollDBService.addEmployeeToPayroll(company_id, name, gender, salary, start, departmentList));
	}

	public void deleteEmployeeInDatabase(String name) throws DataBaseSQLException {
		boolean result = this.employeePayrollDBService.deleteEmployee(name);
		System.out.println(result);
		if (result) {
			EmployeePayrollData emp = getEmployee(employeePayrollList, name);
			employeePayrollList.remove(emp);
		}
	}

	public void addListOfEmployee(List<EmployeePayrollData> empList) {
		empList.forEach(emp -> {
			System.out.println("Employee Being Added : " + emp.getName());
			try {
				this.addEmployeeInDatabase(1, emp.getName(), emp.getGender(), emp.getSalary(), emp.getStartDate(),
						emp.getDepartment_ids());
			} catch (DataBaseSQLException e) {
				e.printStackTrace();
			}
			System.out.println("Employee Added : " + emp.getName());
		});
		System.out.println(this.employeePayrollList);
	}

	public void addListOfEmployeeWithThreads(List<EmployeePayrollData> empList) {
		Map<Integer, Boolean> employeeAdditionalStatus = new HashMap<>();
		empList.forEach(emp -> {
			employeeAdditionalStatus.put(emp.hashCode(), false);
		});
		empList.forEach(emp -> {
			Runnable task = () -> {
				System.out.println("Employee Being Added : " + Thread.currentThread().getName());
				try {
					this.addEmployeeInDatabase(1, emp.getName(), emp.getGender(), emp.getSalary(), emp.getStartDate(),
							emp.getDepartment_ids());
				} catch (DataBaseSQLException e) {
					e.printStackTrace();
				}
				employeeAdditionalStatus.put(emp.hashCode(), true);
				System.out.println("Employee Added : " + Thread.currentThread().getName());
			};
			Thread thread = new Thread(task, emp.getName());
			thread.start();
		});
		while (employeeAdditionalStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(employeePayrollList);
	}

	public boolean updateSalaryOfListOfEmployee(List<EmployeePayrollData> empList) {
		Map<Integer, Boolean> employeeAdditionalStatus = new HashMap<>();
		Map<Integer, Boolean> employeeisSyncStatus = new HashMap<>();
		empList.forEach(emp -> {
			Runnable task = () -> {
				employeeAdditionalStatus.put(emp.hashCode(), false);
				System.out.println("Employee Being Updated : " + Thread.currentThread().getName());
				this.updateSalaryOfAnEmployeeInDB(emp.getName(), emp.getSalary(), IOService.DB_IO);
				employeeAdditionalStatus.put(emp.hashCode(), true);
				System.out.println("Employee Updated : " + Thread.currentThread().getName());
				try {
					employeeisSyncStatus.put(emp.hashCode(), isEmployeeSyncWithDatabase(emp.getName()));
				} catch (DataBaseSQLException e) {
					e.printStackTrace();
				}
			};
			Thread thread = new Thread(task, emp.getName());
			thread.start();
		});
		while (employeeAdditionalStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		for (Map.Entry<Integer, Boolean> entry : employeeisSyncStatus.entrySet()) {
			System.out.println(entry.getKey() + "  " + entry.getValue());
		}
		if (employeeisSyncStatus.containsValue(false))
			return false;
		else
			return true;
	}
}
