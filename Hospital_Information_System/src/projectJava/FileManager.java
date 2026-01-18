package projectJava;

import gui.InvoiceRecord;
import person.ContactInfo;
import person.Patient;
import exceptions.DataAccessException;
import rooms.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String PATIENT_FILE = "patients.txt";
    private static final String ROOM_FILE = "rooms.txt";
    private static final String ADMISSION_FILE = "admissions.txt";
    private static final String INVOICE_FILE = "invoices.txt";

    // ---------- Patients ----------
    public static void savePatients(List<Patient> patients) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PATIENT_FILE))) {
            for (Patient p : patients) {
                ContactInfo c = p.getContactInfo();
                // id;name;gender;dob;height;weight;phone;email;address
                bw.write(String.join(";",
                        safe(p.getId()),
                        safe(p.getFullName()),
                        safe(p.getGender()),
                        safe(p.getDateOfBirth() == null ? "" : p.getDateOfBirth().toString()),
                        String.valueOf(p.getHeight()),
                        String.valueOf(p.getWeight()),
                        safe(c == null ? "" : c.getPhoneNumber()),
                        safe(c == null ? "" : c.getEmail()),
                        safe(c == null ? "" : c.getAddress())
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new DataAccessException("Failed to save patients to " + PATIENT_FILE, e);
        }
    }

    public static List<Patient> loadPatients() {
        List<Patient> patients = new ArrayList<>();
        File f = new File(PATIENT_FILE);
        if (!f.exists()) return patients;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            int lineNo = 0;
            while ((line = br.readLine()) != null) {
                lineNo++;
                String[] d = line.split(";");
                if (d.length < 9) continue;

                try {
                    Patient p = new Patient(
                            d[0], d[1], d[2],
                            d[3].isEmpty() ? null : LocalDate.parse(d[3]),
                            new ContactInfo(d[6], d[7], d[8]),
                            Double.parseDouble(d[4]),
                            Double.parseDouble(d[5])
                    );
                    patients.add(p);
                } catch (Exception parseErr) {
                    System.err.println("Skipping bad patient line " + lineNo + ": " + parseErr.getMessage());
                }
            }
        } catch (IOException e) {
            throw new DataAccessException("Failed to load patients from " + PATIENT_FILE, e);
        }
        return patients;
    }

    // ---------- Rooms ----------
    public static void saveRooms(List<Room> rooms) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ROOM_FILE))) {
            for (Room r : rooms) {
                // roomNumber;TYPE;dailyPrice
                bw.write(String.join(";",
                        safe(r.getRoomNumber()),
                        safe(r.getRoomType().toString()),
                        String.valueOf(r.getDailyPrice())
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new DataAccessException("Failed to save rooms to " + ROOM_FILE, e);
        }
    }

    public static List<Room> loadRooms() {
        List<Room> rooms = new ArrayList<>();
        File f = new File(ROOM_FILE);
        if (!f.exists()) return rooms;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            int lineNo = 0;
            while ((line = br.readLine()) != null) {
                lineNo++;
                String[] d = line.split(";");
                if (d.length < 3) continue;

                try {
                    String number = d[0];
                    RoomTypes type = RoomTypes.valueOf(d[1]);
                    double price = Double.parseDouble(d[2]);

                    Room room;
                    switch (type) {
                        case ICU: room = new IntensiveCareUnit(number, price); break;
                        case PRIVATE: room = new PrivateRoom(number, price); break;
                        case EMERGENCY:
                            // If you have EmergencyRoom, use it here; otherwise fallback:
                            room = new PatientRoom(number, price);
                            break;
                        default: room = new PatientRoom(number, price);
                    }
                    rooms.add(room);
                } catch (Exception parseErr) {
                    System.err.println("Skipping bad room line " + lineNo + ": " + parseErr.getMessage());
                }
            }
        } catch (IOException e) {
            throw new DataAccessException("Failed to load rooms from " + ROOM_FILE, e);
        }
        return rooms;
    }

    // ---------- Admissions ----------
    public static void saveAdmissions(List<Admission> admissions) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ADMISSION_FILE))) {
            for (Admission a : admissions) {
                // patientId;roomNumber;start;end
                bw.write(String.join(";",
                        safe(a.getPatient().getId()),
                        safe(a.getRoom().getRoomNumber()),
                        safe(a.getStartDate().toString()),
                        safe(a.getEndDate().toString())
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new DataAccessException("Failed to save admissions to " + ADMISSION_FILE, e);
        }
    }

    public static List<Admission> loadAdmissions(Hospital hospital) {
        List<Admission> admissions = new ArrayList<>();
        File f = new File(ADMISSION_FILE);
        if (!f.exists()) return admissions;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            int lineNo = 0;
            while ((line = br.readLine()) != null) {
                lineNo++;
                String[] d = line.split(";");
                if (d.length < 4) continue;

                try {
                    Patient patient = hospital.findPatientById(d[0]);
                    Room room = hospital.findRoomByNumber(d[1]);
                    if (patient == null || room == null) continue;

                    LocalDate start = LocalDate.parse(d[2]);
                    LocalDate end = LocalDate.parse(d[3]);

                    admissions.add(new Admission(patient, room, start, end));
                } catch (Exception parseErr) {
                    System.err.println("Skipping bad admission line " + lineNo + ": " + parseErr.getMessage());
                }
            }
        } catch (IOException e) {
            throw new DataAccessException("Failed to load admissions from " + ADMISSION_FILE, e);
        }
        return admissions;
    }

    // ---------- Invoices ----------
    public static void appendInvoice(Admission admission, Invoice invoice) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(INVOICE_FILE, true))) {
            // invoiceId;patientId;roomNumber;start;end;base;vatRate;vatAmount;total
            bw.write(String.join(";",
                    safe(invoice.getInvoiceId()),
                    safe(admission.getPatient().getId()),
                    safe(admission.getRoom().getRoomNumber()),
                    safe(admission.getStartDate().toString()),
                    safe(admission.getEndDate().toString()),
                    String.valueOf(invoice.getBaseAmount()),
                    String.valueOf(invoice.getVatRate()),
                    String.valueOf(invoice.getVatAmount()),
                    String.valueOf(invoice.getTotalAmount())
            ));
            bw.newLine();
        } catch (IOException e) {
            throw new DataAccessException("Failed to write invoice to " + INVOICE_FILE, e);
        }
    }

    public static List<InvoiceRecord> loadInvoices() {
        List<InvoiceRecord> invoices = new ArrayList<>();
        File f = new File(INVOICE_FILE);
        if (!f.exists()) return invoices;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            int lineNo = 0;
            while ((line = br.readLine()) != null) {
                lineNo++;
                String[] d = line.split(";");
                if (d.length < 9) continue;

                try {
                    invoices.add(new InvoiceRecord(
                            d[0], d[1], d[2], d[3], d[4],
                            Double.parseDouble(d[5]),
                            Double.parseDouble(d[6]),
                            Double.parseDouble(d[7]),
                            Double.parseDouble(d[8])
                    ));
                } catch (Exception parseErr) {
                    System.err.println("Skipping bad invoice line " + lineNo + ": " + parseErr.getMessage());
                }
            }
        } catch (IOException e) {
            throw new DataAccessException("Failed to load invoices from " + INVOICE_FILE, e);
        }
        return invoices;
    }

    private static final String ACCOUNT_FILE = "accounts.txt";
    //username;password;ROLE
    public static void saveAccounts(java.util.List<projectJava.UserAccount> accounts) {
        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(ACCOUNT_FILE))) {
            for (projectJava.UserAccount a : accounts) {
                bw.write(String.join(";",
                        safe(a.getUsername()),
                        safe(a.getPasswordRaw()),   // we’ll add this getter
                        safe(a.getRole().toString())
                ));
                bw.newLine();
            }
        } catch (java.io.IOException e) {
            throw new exceptions.DataAccessException("Failed to save accounts to " + ACCOUNT_FILE, e);
        }
    }

    public static java.util.List<projectJava.UserAccount> loadAccounts() {
        java.util.List<projectJava.UserAccount> accounts = new java.util.ArrayList<>();
        java.io.File f = new java.io.File(ACCOUNT_FILE);
        if (!f.exists()) return accounts;

        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(f))) {
            String line;
            int lineNo = 0;
            while ((line = br.readLine()) != null) {
                lineNo++;
                String[] d = line.split(";");
                if (d.length < 3) continue;

                try {
                    String username = d[0];
                    String password = d[1];
                    projectJava.Role role = projectJava.Role.valueOf(d[2]);
                    // staffProfile can be null for now (you can link later)
                    accounts.add(new projectJava.UserAccount(username, password, role, null));
                } catch (Exception parseErr) {
                    System.err.println("Skipping bad account line " + lineNo + ": " + parseErr.getMessage());
                }
            }
        } catch (java.io.IOException e) {
            throw new exceptions.DataAccessException("Failed to load accounts from " + ACCOUNT_FILE, e);
        }
        return accounts;
    }

    private static final String APPOINTMENT_FILE = "appointments.txt";

    public static void saveAppointments(List<Appointment> appointments) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(APPOINTMENT_FILE))) {
            for (Appointment a : appointments) {
                // doctorName;patientId;date
                bw.write(String.join(";",
                        safe(a.getDoctor().getFullName()),
                        safe(a.getPatient().getId()),
                        safe(a.getDate().toString())
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new exceptions.DataAccessException("Failed to save appointments to " + APPOINTMENT_FILE, e);
        }
    }

    public static List<Appointment> loadAppointments(Hospital hospital) {
        List<Appointment> list = new ArrayList<>();
        File f = new File(APPOINTMENT_FILE);
        if (!f.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] d = line.split(";");
                if (d.length < 3) continue;

                String doctorName = d[0];
                String patientId = d[1];
                LocalDate date = LocalDate.parse(d[2]);

                // minimal doctor stub (no staff directory in this project)
                person.Doctor doc = new person.Doctor("DOC-" + doctorName, "E-" + doctorName,
                        doctorName, "N/A", null, new person.ContactInfo("", "", ""), 0, new projectJava.Department("Doctors"));

                person.Patient p = hospital.findPatientById(patientId);
                if (p == null) continue;

                list.add(new Appointment(doc, p, date));
            }
        } catch (IOException e) {
            throw new exceptions.DataAccessException("Failed to load appointments from " + APPOINTMENT_FILE, e);
        } catch (Exception e) {
            throw new exceptions.DataAccessException("Appointments file corrupted.", e);
        }
        return list;
    }

    private static String safe(String s) {
        return s == null ? "" : s.replace(";", ",").trim();
    }
}
