package projectJava;

import person.Patient;
import rooms.Room;

import java.time.LocalDate;

public class Admission {
    private Patient patient;
    private Room room;
    private LocalDate startDate;
    private LocalDate endDate;
    private Invoice invoice;

    public Admission(Patient patient, Room room, LocalDate startDate, LocalDate endDate) {
        this.patient = patient;
        this.room = room;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Patient getPatient() { return patient; }
    public Room getRoom() { return room; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public Invoice getInvoice() { return invoice; }

    public double getCost() {
        return room.calculateCost(startDate, endDate);
    }

    public Invoice generateInvoice(String invoiceId, double vatRate) {
        this.invoice = new Invoice(invoiceId, getCost(), vatRate);
        return this.invoice;
    }
}
