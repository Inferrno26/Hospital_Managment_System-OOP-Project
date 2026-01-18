package person;

import projectJava.Department;

import java.time.LocalDate;

public abstract class Staff extends Person {
    protected String employeeId;
    protected double salary;
    protected Department department;

    public Staff(String id, String employeeId, String fullName, String gender, LocalDate dateOfBirth,
                 ContactInfo contactInfo, double salary, Department department) {
        super(id, fullName, gender, dateOfBirth, contactInfo);
        this.employeeId = employeeId;
        this.salary = salary;
        this.department = department;
    }

    public String getEmployeeId() { return employeeId; }
    public double getSalary() { return salary; }
    public Department getDepartment() { return department; }
}
