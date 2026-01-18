package person;

import projectJava.Department;
import projectJava.Permission;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Set;

public class Administrator extends Staff implements HasPermissions {

    public Administrator(String id, String employeeId, String fullName, String gender, LocalDate dob,
                         ContactInfo contactInfo, double salary, Department department) {
        super(id, employeeId, fullName, gender, dob, contactInfo, salary, department);
    }

    @Override public String getRole() { return "Administrator"; }

    @Override
    public Set<Permission> getPermissions() {
        return EnumSet.of(
                Permission.MANAGE_ROOMS,
                Permission.MANAGE_USERS,
                Permission.VIEW_PATIENTS,
                Permission.VIEW_ROOMS,
                Permission.VIEW_INVOICES
        );
    }
}
