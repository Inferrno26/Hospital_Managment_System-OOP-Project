package projectJava;

import person.Doctor;
import person.Patient;

import java.time.LocalDate;

public class Appointment {
    private Doctor doctor;
    private Patient patient;
    private LocalDate date;

    public Appointment(Doctor doctor, Patient patient, LocalDate date) {
        this.doctor = doctor;
        this.patient = patient;
        this.date = date;
    }

    public Doctor getDoctor() { return doctor; }
    public Patient getPatient() { return patient; }
    public LocalDate getDate() { return date; }
}
