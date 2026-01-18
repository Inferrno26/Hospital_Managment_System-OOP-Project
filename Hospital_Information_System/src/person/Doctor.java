package person;

import projectJava.Department;
import projectJava.Permission;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Set;

public class Doctor extends Staff implements HasPermissions {

    public Doctor(String id, String employeeId, String fullName, String gender, LocalDate dob,
                  ContactInfo contactInfo, double salary, Department department) {
        super(id, employeeId, fullName, gender, dob, contactInfo, salary, department);
    }

    @Override public String getRole() { return "Doctor"; }

    @Override
    public Set<Permission> getPermissions() {
        return EnumSet.of(
                Permission.VIEW_PATIENTS,
                Permission.VIEW_ROOMS
        );
    }
}
