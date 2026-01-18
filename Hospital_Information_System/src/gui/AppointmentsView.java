package gui;

import exceptions.DataAccessException;
import exceptions.ValidationException;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import person.ContactInfo;
import person.Doctor;
import projectJava.*;

import java.time.LocalDate;

public class AppointmentsView {
    private final BorderPane view = new BorderPane();

    public AppointmentsView(Stage stage, Hospital hospital) {
        view.setPadding(new Insets(15));

        TableView<Appointment> table = new TableView<>();
        table.getItems().setAll(hospital.getAppointments());

        TableColumn<Appointment, String> docCol = new TableColumn<>("Doctor");
        docCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDoctor().getFullName()));

        TableColumn<Appointment, String> patCol = new TableColumn<>("Patient ID");
        patCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPatient().getId()));

        TableColumn<Appointment, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDate().toString()));

        table.getColumns().addAll(docCol, patCol, dateCol);

        TextField patientId = new TextField();
        patientId.setPromptText("Patient ID");

        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(1));

        Button add = new Button("Add Appointment");
        add.setOnAction(e -> {
            try {
                String pid = patientId.getText();
                if (pid == null || pid.isBlank()) throw new ValidationException("Patient ID required.");
                if (datePicker.getValue() == null) throw new ValidationException("Date required.");

                person.Patient p = hospital.findPatientById(pid.trim());
                if (p == null) throw new ValidationException("Patient not found.");

                // Doctor identity = current username for demo
                UserAccount u = hospital.getCurrentUser();
                String docName = (u == null) ? "Doctor" : u.getUsername();

                Doctor doc = new Doctor("DOC-" + docName, "E-" + docName,
                        docName, "N/A", null, new ContactInfo("", "", ""), 0, new Department("Doctors"));

                Appointment appt = new Appointment(doc, p, datePicker.getValue());
                hospital.addAppointment(appt);

                FileManager.saveAppointments(hospital.getAppointments());
                table.getItems().setAll(hospital.getAppointments());

                patientId.clear();

            } catch (ValidationException ex) {
                ErrorDialog.show("Validation Error", "Cannot create appointment", ex.getMessage());
            } catch (DataAccessException ex) {
                ErrorDialog.show("File Error", "Could not save appointments", ex.getMessage());
            } catch (Exception ex) {
                ErrorDialog.showException("Unexpected Error", "Appointment failed", ex);
            }
        });

        Button back = new Button("Back");
        back.setOnAction(e -> DashboardRouter.goHome(stage, hospital));

        HBox controls = new HBox(10,
                new Label("Patient ID:"), patientId,
                new Label("Date:"), datePicker,
                add, back
        );
        controls.setPadding(new Insets(10));

        view.setCenter(table);
        view.setBottom(controls);
    }

    public BorderPane getView() { return view; }
}