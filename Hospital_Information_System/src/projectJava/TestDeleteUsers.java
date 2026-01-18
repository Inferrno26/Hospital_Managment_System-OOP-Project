package projectJava;

public class TestDeleteUsers {
    public static void main(String[] args) {
        Hospital hospital = new Hospital();
        hospital.setAccounts(FileManager.loadAccounts());

        System.out.println("Loaded accounts:");
        for (UserAccount a : hospital.getAccounts()) {
            System.out.println(" - " + a.getUsername() + " (" + a.getRole() + ")");
        }

        UserAccount admin = null;
        for (UserAccount a : hospital.getAccounts()) {
            if (a.getRole() == Role.ADMIN) { admin = a; break; }
        }

        if (admin == null) {
            System.out.println("No admin account found.");
            return;
        }

        hospital.setCurrentUser(admin);
        System.out.println("Current user set to: " + hospital.getCurrentUser().getUsername());

        try {
            // attempt to delete the doctor user (Dion) — change based on current accounts.txt
            hospital.deleteAccount(hospital.getCurrentUser(), "Dion");
            FileManager.saveAccounts(hospital.getAccounts());
            System.out.println("Delete succeeded. Remaining accounts:");
            for (UserAccount a : hospital.getAccounts()) {
                System.out.println(" - " + a.getUsername() + " (" + a.getRole() + ")");
            }
        } catch (Exception ex) {
            System.out.println("Delete failed: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}