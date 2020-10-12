package com.capgemini.payrollservice;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.capgemini.payrolldata.EmployeePayrollData;
import com.capgemini.payrollservice.EmployeePayrollService.IOService;

public class PayrollServiceTests {

	@Test
	public void given3EmployeesWhenWrittenToFilesShouldMatchTheGivenEntries() {
	EmployeePayrollData[] arrEmp= {new EmployeePayrollData(1, "Yudha", 100.67),
			new EmployeePayrollData(2,"Raman",456.78),
			new EmployeePayrollData(3,"Tapan",78.35)};
	EmployeePayrollService EPS=new EmployeePayrollService(Arrays.asList(arrEmp));
	EPS.writeData(IOService.FILE_IO);
	long count=EPS.countEntries(IOService.FILE_IO);
	System.out.println(count);
	Assert.assertEquals(3,count);
	}

}
