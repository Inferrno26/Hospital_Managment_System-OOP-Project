package projectJava;

import exceptions.ValidationException;
import person.Patient;
import rooms.Room;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import person.Doctor;
import person.Staff;
import projectJava.Appointment;

/**
 * Hospital class manages patients, rooms, admissions, and user accounts.
 */
public class Hospital {

    // -------------------- Core Data --------------------
    private List<Patient> patients = new ArrayList<>();
    private List<Room> rooms = new ArrayList<>();
    private List<Admission> admissions = new ArrayList<>();

    // -------------------- Accounts / Auth --------------------
    private List<UserAccount> accounts = new ArrayList<>();

    // Role keys for privileged account creation
    private final Map<Role, String> roleKeys = new EnumMap<>(Role.class);

    {
        roleKeys.put(Role.ADMIN, "ADMIN-2026");
        roleKeys.put(Role.DOCTOR, "DOC-2026");
        roleKeys.put(Role.NURSE, "NURSE-2026");
        roleKeys.put(Role.RECEPTIONIST, "REC-2026");
    }
    
    private UserAccount currentUser;

    public Hospital() {}

    // -------------------- Patients --------------------
    public void registerPatient(Patient p) {
        if (p == null) throw new ValidationException("Patient cannot be null.");
        if (p.getId() == null || p.getId().isBlank())
            throw new ValidationException("Patient ID is required.");

        if (findPatientById(p.getId()) != null)
            throw new ValidationException("Patient ID already exists.");

        patients.add(p);
    }

    public List<Patient> getPatients() { return patients; }

    public void setPatients(List<Patient> list) {
        this.patients = (list == null) ? new ArrayList<>() : list;
    }

    public Patient findPatientById(String id) {
        if (id == null) return null;
        for (Patient p : patients) {
            if (id.equals(p.getId())) return p;
        }
        return null;
    }

    // -------------------- Rooms --------------------
    public void addRoom(Room r) {
        if (r == null) throw new ValidationException("Room cannot be null.");
        if (r.getRoomNumber() == null || r.getRoomNumber().isBlank())
            throw new ValidationException("Room number is required.");

        if (findRoomByNumber(r.getRoomNumber()) != null)
            throw new ValidationException("Room number already exists.");

        rooms.add(r);
    }

    public List<Room> getRooms() { return rooms; }

    public void setRooms(List<Room> list) {
        this.rooms = (list == null) ? new ArrayList<>() : list;
    }

    public Room findRoomByNumber(String number) {
        if (number == null) return null;
        for (Room r : rooms) {
            if (number.equals(r.getRoomNumber())) return r;
        }
        return null;
    }

    // -------------------- Admissions --------------------
    public List<Admission> getAdmissions() { return admissions; }

    public void setAdmissions(List<Admission> list) {
        this.admissions = (list == null) ? new ArrayList<>() : list;
    }

    /**
     * Booking intervals are treated as [start, end).
     */
    public Admission admit(Patient p, Room r, LocalDate start, LocalDate end) {
        if (p == null) throw new ValidationException("No patient selected.");
        if (r == null) throw new ValidationException("No room selected.");
        if (start == null || end == null) throw new ValidationException("Start and end dates are required.");
        if (!end.isAfter(start)) throw new ValidationException("End date must be after start date.");

        if (!isRoomAvailable(r, start, end))
            throw new ValidationException("Room is not available for selected dates.");

        Admission a = new Admission(p, r, start, end);
        admissions.add(a);
        return a;
    }

    public boolean isRoomAvailable(Room room, LocalDate start, LocalDate end) {
        if (room == null || start == null || end == null) return false;

        for (Admission a : admissions) {
            if (a.getRoom().getRoomNumber().equals(room.getRoomNumber())) {
                if (datesOverlap(start, end, a.getStartDate(), a.getEndDate())) return false;
            }
        }
        return true;
    }

    public List<Room> findAvailableRooms(LocalDate start, LocalDate end) {
        if (start == null || end == null || !end.isAfter(start)) {
            return new ArrayList<>(rooms);
        }

        List<Room> available = new ArrayList<>();
        for (Room r : rooms) {
            if (isRoomAvailable(r, start, end)) available.add(r);
        }
        return available;
    }

    private boolean datesOverlap(LocalDate s1, LocalDate e1, LocalDate s2, LocalDate e2) {
        return s1.isBefore(e2) && e1.isAfter(s2);
    }

    // -------------------- Accounts --------------------
    public void setAccounts(List<UserAccount> accountsParam) {
        if (accountsParam == null) {
            this.accounts = new ArrayList<>();
            return;
        }

        // Try to create a JavaFX ObservableList at runtime so GUI can observe changes.
        try {
            Class<?> fxCollections = Class.forName("javafx.collections.FXCollections");
            java.lang.reflect.Method obsListMethod = fxCollections.getMethod("observableList", java.util.List.class);
            Object obs = obsListMethod.invoke(null, accountsParam);
            if (obs instanceof List) {
                // Use the ObservableList as our internal list (it implements java.util.List)
                //noinspection unchecked
                this.accounts = (List<UserAccount>) obs;
                return;
            }
        } catch (Exception ignored) {
            // JavaFX not available or reflection failed; fall back to ArrayList below
        }

        // Fallback: copy into a plain ArrayList
        this.accounts = new ArrayList<>(accountsParam);
    }

    public List<UserAccount> getAccounts() {
        return accounts;
    }

    /**
     * IMPORTANT: This is the exact signature LoginView expects:
     * login(String, String, Role) -> UserAccount
     */
    public UserAccount login(String username, String password, Role role) {
        if (username == null || username.isBlank()) throw new ValidationException("Username required.");
        if (password == null || password.isBlank()) throw new ValidationException("Password required.");
        if (role == null) throw new ValidationException("Role required.");

        String u = username.trim();

        for (UserAccount acc : accounts) {
            if (acc.getUsername().equals(u) && acc.getRole() == role && acc.checkPassword(password)) {
                return acc;
            }
        }
        throw new ValidationException("Invalid credentials.");
    }

    public void createAccount(String username, String password, Role role, String secondaryKey) {
        if (username == null || username.isBlank()) throw new ValidationException("Username required.");
        if (password == null || password.isBlank()) throw new ValidationException("Password required.");
        if (role == null) throw new ValidationException("Role required.");

        String requiredKey = roleKeys.get(role);
        if (requiredKey == null) throw new ValidationException("Role key not configured for " + role + ".");

        if (secondaryKey == null || !requiredKey.equals(secondaryKey.trim())) {
            throw new ValidationException("Invalid role key for " + role + ".");
        }

        String u = username.trim();

        for (UserAccount acc : accounts) {
            if (acc.getUsername().equalsIgnoreCase(u)) {
                throw new ValidationException("Username already exists.");
            }
        }

        accounts.add(new UserAccount(u, password, role, null));
    }

    // Optional: change keys from code if you want
    public void setRoleKey(Role role, String key) {
        if (role == null) throw new ValidationException("Role required.");
        if (key == null || key.isBlank()) throw new ValidationException("Key required.");
        roleKeys.put(role, key.trim());
    }
    
    // -------------------- Admin user management --------------------
    /**
     * Returns a copy of all user accounts. Admin only.
     */
    public List<UserAccount> listUsers(UserAccount requester) {
        if (requester == null || requester.getRole() != Role.ADMIN)
            throw new ValidationException("Admin privileges required.");

        // Return a shallow copy to avoid exposing internal list
        return new ArrayList<>(accounts);
    }

    /**
     * Deletes the user with the given username. Only an admin may perform this.
     * Cannot delete the requesting admin's own account and will prevent removing
     * the last remaining admin account.
     */
    public void deleteAccount(UserAccount requester, String username) {
        if (requester == null || requester.getRole() != Role.ADMIN)
            throw new ValidationException("Admin privileges required.");
        if (username == null || username.isBlank())
            throw new ValidationException("Username required.");

        String u = username.trim();

        UserAccount target = null;
        for (UserAccount acc : accounts) {
            if (acc.getUsername().equalsIgnoreCase(u)) {
                target = acc;
                break;
            }
        }

        if (target == null) throw new ValidationException("User not found.");

        if (target.getUsername().equalsIgnoreCase(requester.getUsername()))
            throw new ValidationException("Cannot delete your own account.");

        // If deleting an admin, ensure at least one admin remains
        if (target.getRole() == Role.ADMIN) {
            int adminCount = 0;
            for (UserAccount acc : accounts) if (acc.getRole() == Role.ADMIN) adminCount++;
            if (adminCount <= 1) throw new ValidationException("Cannot delete the last admin account.");
        }

        accounts.removeIf(acc -> acc.getUsername().equalsIgnoreCase(u));
    }
    
    public void deletePatient(Patient patient) {
        if (patient == null) throw new ValidationException("No patient selected.");

        // Block deletion if patient has admission history
        for (Admission a : admissions) {
            if (a.getPatient().getId().equals(patient.getId())) {
                throw new ValidationException("Cannot delete patient: patient has admissions history.");
            }
        }

        patients.removeIf(p -> p.getId().equals(patient.getId()));
    }
    public void deleteRoom(Room room) {
        if (room == null) throw new ValidationException("No room selected.");

        for (Admission a : admissions) {
            if (a.getRoom().getRoomNumber().equals(room.getRoomNumber())) {
                throw new ValidationException("Cannot delete room: room has admissions history.");
            }
        }

        rooms.removeIf(r -> r.getRoomNumber().equals(room.getRoomNumber()));
    }
    
    //--------------------- Appointments --------------------
    private List<Appointment> appointments = new ArrayList<>();

    public void setCurrentUser(UserAccount user) { this.currentUser = user; }
    public UserAccount getCurrentUser() { return currentUser; }

    public List<Appointment> getAppointments() { return appointments; }
    public void setAppointments(List<Appointment> list) {
        this.appointments = (list == null) ? new ArrayList<>() : list;
    }

    public void addAppointment(Appointment appt) {
        if (appt == null) throw new ValidationException("Appointment cannot be null.");
        appointments.add(appt);
    }

}