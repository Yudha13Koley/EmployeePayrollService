package com.capgemini.payrolldata;

import java.time.LocalDate;
import java.util.List;

public class EmployeePayrollData {
	private int id;
	private String company_name;
	private String name;
	private String address;
	private char gender;
	private double salary;
	private LocalDate startDate;
	private List<Integer> department_ids;

	public EmployeePayrollData(int id, String name, double salary) {
		super();
		this.id = id;
		this.name = name;
		this.salary = salary;
	}

	public EmployeePayrollData(int id2, String company_name, String name2, String address, double salary2,
			LocalDate startdate, char gender, List<Integer> department_ids) {
		this(id2, name2, salary2);
		this.startDate = startdate;
		this.gender = gender;
		this.company_name = company_name;
		this.address = address;
		this.department_ids = department_ids;
	}

	public char getGender() {
		return gender;
	}

	public void setGender(char gender) {
		this.gender = gender;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public List<Integer> getDepartment_ids() {
		return department_ids;
	}

	public void setDepartment_ids(List<Integer> department_ids) {
		this.department_ids = department_ids;
	}

	public EmployeePayrollData(String company_name, String name, int salary, LocalDate now, char gender,
			List<Integer> departments) {
		this.company_name = company_name;
		this.name = name;
		this.salary = salary;
		this.startDate = now;
		this.gender = gender;
		this.department_ids = departments;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	@Override
	public String toString() {
		return "EmployeePayrollData [id=" + id + ", company_name=" + company_name + ", name=" + name + ", address="
				+ address + ", gender=" + gender + ", salary=" + salary + ", startDate=" + startDate
				+ ", department_ids=" + department_ids + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmployeePayrollData other = (EmployeePayrollData) obj;
		if (gender != other.gender)
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Double.doubleToLongBits(salary) != Double.doubleToLongBits(other.salary))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}

}
