package com.capgemini.fileioservice;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import com.capgemini.payrolldata.EmployeePayrollData;
import com.google.common.io.Files;

public class EmployeePayrollFileIOService {
	public static String PAYROLL_FILE_NAME = "PayrollFile.txt";

	public void writeDataInFile(List<EmployeePayrollData> employeePayrollList) {
		StringBuffer empBuff = new StringBuffer();
		employeePayrollList.forEach(e -> {
			String str = e.toString();
			empBuff.append(str);
		});
		try {
			Files.write(empBuff.toString().getBytes(), Paths.get(PAYROLL_FILE_NAME).toFile());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public long countEntriesFromFile() {
		long lines = 0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(PAYROLL_FILE_NAME));
			while (reader.readLine() != null) {
				lines++;
			}
			reader.close();
		} catch (IOException e) {
		}
		return lines;

	}

	public void printDataFromFile() {
		System.out.println("Printing Data From The File :");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(PAYROLL_FILE_NAME));
			while (true) {
				String str = reader.readLine();
				if (str != null)
					System.out.println(str);
				else
					break;
			}
			reader.close();
		} catch (IOException e) {
		}

	}

	public List<EmployeePayrollData> readDataFromFile(List<EmployeePayrollData> employeePayrollList) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(PAYROLL_FILE_NAME));
			while (true) {
				String str = reader.readLine();
				if (str != null) {
					int a = str.indexOf(",");
					int b = str.indexOf(",", a + 1);
					int id = Integer.parseInt(str.substring(str.indexOf("id=") + 3, a));
					String name = str.substring(str.indexOf("name=") + 5, b);
					double salary = Double.parseDouble(str.substring(str.indexOf("salary=") + 7, str.indexOf("]")));
					employeePayrollList.add(new EmployeePayrollData(id, name, salary));
				} else
					break;
			}
			reader.close();
		} catch (IOException e) {
		}

		return employeePayrollList;
	}
}
