package projectJava;

import person.Staff;

import java.util.ArrayList;
import java.util.List;

public class Department {
    private String name;
    private List<Staff> staff = new ArrayList<>();

    public Department(String name) { this.name = name; }

    public String getName() { return name; }
    public List<Staff> getStaff() { return staff; }

    public void addStaff(Staff s) { staff.add(s); }
}
