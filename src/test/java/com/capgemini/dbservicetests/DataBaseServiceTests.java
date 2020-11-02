package com.capgemini.dbservicetests;

import static org.junit.Assert.fail;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.capgemini.exceptions.DataBaseSQLException;
import com.capgemini.payrolldata.EmployeePayrollData;
import com.capgemini.payrollservice.EmployeePayrollService;
import com.capgemini.payrollservice.EmployeePayrollService.IOService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

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
		employeePayrollService.updateSalaryOfAnEmployeeInDB("Rita", 7800000.00);
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
		Instant startWithThread = Instant.now();
		employeePayrollService.addListOfEmployeeWithThreads(Arrays.asList(empArr));
		Instant endWithThread = Instant.now();
		System.out.println("Duration With Thread :" + Duration.between(startWithThread, endWithThread));
		for (EmployeePayrollData emp : employeePayrollService.employeePayrollList) {
			System.out.println(emp);
		}
		Assert.assertEquals(14, employeePayrollService.employeePayrollList.size());
	}

	@Test
	public void givenEmployeePayrollDatabase_whenUpdatedAListOfEmployee_shouldcheckisSyncWithDatabase()
			throws DataBaseSQLException {
		EmployeePayrollData[] empArr = new EmployeePayrollData[] { new EmployeePayrollData(0, "Ratan", 5000000),
				new EmployeePayrollData(0, "Rinki", 5100000), new EmployeePayrollData(0, "Alok", 5200000),
				new EmployeePayrollData(0, "Fatima", 5300000), new EmployeePayrollData(0, "Raja", 5400000) };
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readData(IOService.DB_IO);
		Instant startWithThread = Instant.now();
		boolean b = employeePayrollService.updateSalaryOfListOfEmployee(Arrays.asList(empArr));
		Instant endWithThread = Instant.now();
		System.out.println("Duration With Thread :" + Duration.between(startWithThread, endWithThread));
		Assert.assertEquals(true, b);
	}

	@Test
	public void givenEmployeePayrollDatabase_whenRetrievedData_givesDBJsonFiles() throws DataBaseSQLException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readData(IOService.DB_IO);
		try {
			FileWriter writer = new FileWriter("./empDB.json");
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String str = gson.toJson(employeePayrollService.employeePayrollList);
			writer.write(str);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}

	public EmployeePayrollData[] getEmployee() {
		Response response = RestAssured.get("/employee_details");
		System.out.println("Employee Payroll entries in Json Server : \n" + response.asString());
		EmployeePayrollData[] empdata = new Gson().fromJson(response.asString(), EmployeePayrollData[].class);
		return empdata;
	}

	private Response addEmployeeToJsonServer(EmployeePayrollData newEmp) {
		String empJson = new GsonBuilder().setPrettyPrinting().create().toJson(newEmp);
		RequestSpecification request = RestAssured.given();
		request.header("Content-type", "application/json");
		request.body(empJson);
		return request.post("/employee_details");
	}

	@Test
	public void givenEmployeeDetailsInJsonServer_whenRetrieved_shouldReturnNoOfCounts() {
		EmployeePayrollData[] empData = getEmployee();
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(empData));
		long entries = employeePayrollService.countEntries(IOService.CONSOLE_IO);
		Assert.assertEquals(4, entries);
	}

	@Test
	public void givenEmployeeDetailsInJsonServer_whenAddedEnEmployee_shouldReturnNoOfCountsAndResponseCode() {
		EmployeePayrollData[] empData = getEmployee();
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(empData));
		EmployeePayrollData newEmp = new EmployeePayrollData("Capgemini", "Ratan", 5500000, LocalDate.now(), 'M',
				Arrays.asList(new Integer[] { 1, 2 }));
		Response response = addEmployeeToJsonServer(newEmp);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(201, statusCode);
		EmployeePayrollData empDataFromResponse = new Gson().fromJson(response.asString(), EmployeePayrollData.class);
		employeePayrollService.employeePayrollList.add(empDataFromResponse);
		System.out.println(employeePayrollService.employeePayrollList);
		long count = employeePayrollService.countEntries(IOService.CONSOLE_IO);
		Assert.assertEquals(5, count);
	}

}
