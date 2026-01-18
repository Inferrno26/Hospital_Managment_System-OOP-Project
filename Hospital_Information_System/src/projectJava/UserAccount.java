package projectJava;

import person.Staff;

public class UserAccount {
    private final String username;
    private final String password; // plain text is acceptable for course project
    private final Role role;
    private final Staff staffProfile; // can be null

    public UserAccount(String username, String password, Role role, Staff staffProfile) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.staffProfile = staffProfile;
    }

    public String getUsername() { return username; }
    public Role getRole() { return role; }
    public Staff getStaffProfile() { return staffProfile; }

    public boolean checkPassword(String input) {
        return password != null && password.equals(input);
    }

    // Needed for FileManager persistence
    public String getPasswordRaw() { return password; }
}
