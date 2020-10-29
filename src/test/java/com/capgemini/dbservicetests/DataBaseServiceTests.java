package com.capgemini.dbservicetests;

import static org.junit.Assert.fail;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.capgemini.exceptions.DataBaseSQLException;
import com.capgemini.payrolldata.EmployeePayrollData;
import com.capgemini.payrollservice.EmployeePayrollService;
import com.capgemini.payrollservice.EmployeePayrollService.IOService;

public class DataBaseServiceTests {

	@Test
	public void givenEmployeePayrollDataBase_whenRetrieved_shouldReturnNumberOfEntries() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		try {
			employeePayrollService.readData(IOService.DB_IO);
		} catch (DataBaseSQLException e) {
			e.printStackTrace();
			fail();
		}
		List<EmployeePayrollData> empList = employeePayrollService.employeePayrollList;
		System.out.println(empList);
		Assert.assertEquals(4, empList.size());
	}

	@Test
	public void givenEmployeePayrollDataBase_whenUpdateAValueForAnEmployee_shouldSyncWithDatabase() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		try {
			employeePayrollService.readData(IOService.DB_IO);
		} catch (DataBaseSQLException e) {
			e.printStackTrace();
			fail();
		}
		employeePayrollService.updateSalaryOfAnEmployeeInDB("Rita", 7000000.00);
		boolean result = false;
		try {
			result = employeePayrollService.isEmployeeSyncWithDatabase("Rita");
		} catch (DataBaseSQLException e) {
			fail();
			e.printStackTrace();
		}
		Assert.assertEquals(true, result);

	}

	@Test
	public void givenEmployeePayrollDataBase_whenRetrievedDataForACondition_shouldReturnCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		try {
			employeePayrollService.readDataForACondition();
		} catch (DataBaseSQLException e) {
			e.printStackTrace();
			fail();
		}
		List<EmployeePayrollData> empList = employeePayrollService.employeePayrollList;
		System.out.println(empList);
		Assert.assertEquals(2, empList.size());
	}

	@Test
	public void givenEmployeePayrollDataBase_whenCalculatedAvgSalariesByGender_shouldReturnAvgSalaries() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		Map<Character, Double> result = new HashMap<>();
		try {
			employeePayrollService.readData(IOService.DB_IO);
			result = employeePayrollService.readAVGSalaries();
		} catch (DataBaseSQLException e) {
			e.printStackTrace();
			fail();
		}
		for (Map.Entry<Character, Double> entry : result.entrySet()) {
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
		}
		Assert.assertEquals(3500000, result.get('M'), 0.01);
	}

	@Test
	public void givenEmployeePayrollDataBase_whenAddedAnEmployee_shouldSyncTheEntryWithDatabase() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		try {
			employeePayrollService.readData(IOService.DB_IO);
			employeePayrollService.addEmployeeInDatabase(1, "Mark", 'M', 3500000, LocalDate.now(),
					Arrays.asList(new Integer[] { 4 }));
		} catch (DataBaseSQLException e) {
			e.printStackTrace();
			fail();
		}
		try {
			Assert.assertEquals(true, employeePayrollService.isEmployeeSyncWithDatabase("Mark"));
		} catch (DataBaseSQLException e) {
			fail();
			e.printStackTrace();
		}
	}

	@Test
	public void givenEmployeePayrollDataBase_whenAddedEmployeeAndUpdatedAllTables_shouldSyncTheEntryWithDatabase() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		try {
			employeePayrollService.readData(IOService.DB_IO);
			employeePayrollService.addEmployeeInDatabase(1, "Mina", 'F', 5500000, LocalDate.now(),
					Arrays.asList(new Integer[] { 1, 2 }));
		} catch (DataBaseSQLException e) {
			e.printStackTrace();
			fail();
		}
		try {
			Assert.assertEquals(true, employeePayrollService.isEmployeeSyncWithDatabase("Mina"));
		} catch (DataBaseSQLException e) {
			fail();
			e.printStackTrace();
		}
	}

	@Test
	public void givenEmployeePayrollDataBase_whenDeletedEmployeeAndUpdatedAllTables_shouldReturnSize() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		try {
			employeePayrollService.readData(IOService.DB_IO);
			employeePayrollService.deleteEmployeeInDatabase("Mina");
		} catch (DataBaseSQLException e) {
			e.printStackTrace();
			fail();
		}
		System.out.println(employeePayrollService.employeePayrollList);
		Assert.assertEquals(4, employeePayrollService.employeePayrollList.size());
	}

	@Test
	public void givenEmployeePayrollDatabase_whenAddedListOfEmployee_shouldReturnNoOfEntries()
			throws DataBaseSQLException {
		EmployeePayrollData[] empArr = new EmployeePayrollData[] {
				new EmployeePayrollData("Capgemini", "Ratan", 5500000, LocalDate.now(), 'M',
						Arrays.asList(new Integer[] { 1, 2 })),
				new EmployeePayrollData("Capgemini", "Rinki", 1500000, LocalDate.now(), 'M',
						Arrays.asList(new Integer[] { 2 })),
				new EmployeePayrollData("Capgemini", "Alok", 5000000, LocalDate.now(), 'M',
						Arrays.asList(new Integer[] { 3 })),
				new EmployeePayrollData("Capgemini", "Fatima", 5300000, LocalDate.now(), 'F',
						Arrays.asList(new Integer[] { 1, 4 })),
				new EmployeePayrollData("Capgemini", "Raja", 5800000, LocalDate.now(), 'F',
						Arrays.asList(new Integer[] { 4 })) };
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readData(IOService.DB_IO);
		Instant start = Instant.now();
		employeePayrollService.addListOfEmployee(Arrays.asList(empArr));
		Instant end = Instant.now();
		System.out.println("Duration Without Thread :" + Duration.between(start, end));
		Assert.assertEquals(9, employeePayrollService.employeePayrollList.size());
	}

}
