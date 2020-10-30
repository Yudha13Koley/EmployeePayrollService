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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((company_name == null) ? 0 : company_name.hashCode());
		result = prime * result + ((department_ids == null) ? 0 : department_ids.hashCode());
		result = prime * result + gender;
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		long temp;
		temp = Double.doubleToLongBits(salary);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		return result;
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
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (company_name == null) {
			if (other.company_name != null)
				return false;
		} else if (!company_name.equals(other.company_name))
			return false;
		if (department_ids == null) {
			if (other.department_ids != null)
				return false;
		} else if (!department_ids.equals(other.department_ids))
			return false;
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
