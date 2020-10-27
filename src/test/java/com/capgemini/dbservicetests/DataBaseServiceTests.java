package com.capgemini.dbservicetests;

import static org.junit.Assert.fail;

import java.util.List;

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
		Assert.assertEquals(3, empList.size());
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
		employeePayrollService.updateSalaryOfAnEmployeeInDB("Terisa", 4000000.00);
		boolean result = employeePayrollService.isEmployeeSyncWithDatabase("Terisa");
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
		Assert.assertEquals(1, empList.size());
	}

	@Test
	public void givenEmployeePayrollDataBase_whenDoneCURDOperationsAndUpdated_shouldReturnTrue() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		boolean result = false;
		try {
			result = employeePayrollService.updateGenderColumn();
			employeePayrollService.readData(IOService.DB_IO);
		} catch (DataBaseSQLException e) {
			e.printStackTrace();
			fail();
		}
		List<EmployeePayrollData> empList = employeePayrollService.employeePayrollList;
		System.out.println(empList);
		Assert.assertEquals(true, result);
	}

}
