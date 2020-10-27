package com.capgemini.payrollservice;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.capgemini.exceptions.DataBaseSQLException;
import com.capgemini.payrolldata.EmployeePayrollData;
import com.capgemini.payrollservice.EmployeePayrollService.IOService;

public class PayrollServiceTests {

	@Test
	public void given3EmployeesWhenWrittenToFilesShouldMatchTheGivenEntries() {
		EmployeePayrollData[] arrEmp = { new EmployeePayrollData(1, "Yudha", 100.67),
				new EmployeePayrollData(2, "Raman", 456.78), new EmployeePayrollData(3, "Tapan", 78.35) };
		EmployeePayrollService EPS = new EmployeePayrollService(Arrays.asList(arrEmp));
		EPS.writeData(IOService.FILE_IO);
		long count = EPS.countEntries(IOService.FILE_IO);
		Assert.assertEquals(3, count);
	}

	@Test
	public void given3Employees_WhenWrittenToFiles_ShouldPrintGivenEntries() {
		EmployeePayrollData[] arrEmp = { new EmployeePayrollData(1, "Yudha", 100.67),
				new EmployeePayrollData(2, "Raman", 456.78), new EmployeePayrollData(3, "Tapan", 78.35) };
		EmployeePayrollService EPS = new EmployeePayrollService(Arrays.asList(arrEmp));
		EPS.writeData(IOService.FILE_IO);
		long count = EPS.countEntries(IOService.FILE_IO);
		EPS.printData(IOService.FILE_IO);
		EPS.printData(IOService.CONSOLE_IO);
		Assert.assertEquals(3, count);
	}

	@Test
	public void given3Employees_WhenWrittenToFiles_ShouldReadFromThatFile() throws DataBaseSQLException {
		List<EmployeePayrollData> emp = new LinkedList<>();
		EmployeePayrollService EPS = new EmployeePayrollService(emp);
		EPS.readData(IOService.FILE_IO);
		EPS.printData(IOService.CONSOLE_IO);
		long a = EPS.countEntries(IOService.CONSOLE_IO);
		Assert.assertEquals(3, a);

	}

}
