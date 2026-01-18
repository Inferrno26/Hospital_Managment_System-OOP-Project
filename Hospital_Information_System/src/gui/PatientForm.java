package gui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import person.ContactInfo;
import person.Patient;
import projectJava.FileManager;
import projectJava.Hospital;
import projectJava.ParseUtil;
import exceptions.DataAccessException;
import exceptions.ValidationException;

public class PatientForm {
    private final GridPane view = new GridPane();

    public PatientForm(Stage stage, Hospital hospital) {
        view.setPadding(new Insets(20));
        view.setVgap(10);
        view.setHgap(10);

        TextField id = new TextField();
        TextField name = new TextField();
        TextField gender = new TextField();
        DatePicker dob = new DatePicker();

        TextField height = new TextField();
        TextField weight = new TextField();

        TextField phone = new TextField();
        TextField email = new TextField();
        TextField address = new TextField();

        Button save = new Button("Save");
        Button back = new Button("Back");

        save.setOnAction(e -> {
            try {
                String pid = ParseUtil.requireNonEmpty(id.getText(), "Patient ID");
                String pname = ParseUtil.requireNonEmpty(name.getText(), "Full Name");
                String pgender = ParseUtil.requireNonEmpty(gender.getText(), "Gender");
                if (dob.getValue() == null) throw new ValidationException("Date of birth is required.");

                double h = ParseUtil.parseDouble(height.getText(), "Height");
                double w = ParseUtil.parseDouble(weight.getText(), "Weight");
                if (h <= 0) throw new ValidationException("Height must be > 0.");
                if (w <= 0) throw new ValidationException("Weight must be > 0.");

                ContactInfo c = new ContactInfo(phone.getText(), email.getText(), address.getText());
                Patient patient = new Patient(pid, pname, pgender, dob.getValue(), c, h, w);

                hospital.registerPatient(patient);
                FileManager.savePatients(hospital.getPatients());

                Alert ok = new Alert(Alert.AlertType.INFORMATION, "Patient saved.");
                ok.showAndWait();

            } catch (ValidationException ex) {
                ErrorDialog.show("Validation Error", "Invalid patient data", ex.getMessage());
            } catch (DataAccessException ex) {
                ErrorDialog.show("File Error", "Could not save patient", ex.getMessage());
            } catch (Exception ex) {
                ErrorDialog.showException("Unexpected Error", "Something went wrong", ex);
            }
        });

        // Use DashboardRouter for consistent back navigation
        back.setOnAction(e -> DashboardRouter.goHome(stage, hospital));

        int r = 0;
        view.addRow(r++, new Label("ID"), id);
        view.addRow(r++, new Label("Full Name"), name);
        view.addRow(r++, new Label("Gender"), gender);
        view.addRow(r++, new Label("Date of Birth"), dob);
        view.addRow(r++, new Label("Height (m)"), height);
        view.addRow(r++, new Label("Weight (kg)"), weight);
        view.addRow(r++, new Label("Phone"), phone);
        view.addRow(r++, new Label("Email"), email);
        view.addRow(r++, new Label("Address"), address);
        view.addRow(r, save, back);
    }

    public GridPane getView() { return view; }
}