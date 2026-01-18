package gui;

import javafx.scene.control.Alert;
import projectJava.Admission;
import projectJava.Invoice;

public class InvoiceDialog {
    public static Invoice showAndReturn(Admission admission) {
        Invoice invoice = admission.generateInvoice("INV-" + System.currentTimeMillis(), 0.18);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Invoice");
        alert.setHeaderText("Invoice ID: " + invoice.getInvoiceId());
        alert.setContentText(
                "Patient: " + admission.getPatient().getFullName() + "\n" +
                "Room: " + admission.getRoom().getRoomNumber() + "\n" +
                "Stay: " + admission.getStartDate() + " to " + admission.getEndDate() + "\n\n" +
                "Base Amount: " + invoice.getBaseAmount() + "\n" +
                "VAT (" + (invoice.getVatRate() * 100) + "%): " + invoice.getVatAmount() + "\n" +
                "Total: " + invoice.getTotalAmount()
        );
        alert.showAndWait();

        return invoice;
    }
}
