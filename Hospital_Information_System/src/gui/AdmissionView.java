package gui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import person.Patient;
import projectJava.Admission;
import projectJava.FileManager;
import projectJava.Hospital;
import exceptions.DataAccessException;
import exceptions.ValidationException;
import rooms.Room;

import java.time.LocalDate;

public class AdmissionView {
    private final BorderPane view = new BorderPane();

    public AdmissionView(Stage stage, Hospital hospital) {
        PatientTableView patientTable = new PatientTableView(hospital);
        RoomTableView roomTable = new RoomTableView();

        TextField patientSearch = new TextField();
        patientSearch.setPromptText("Search patients (id/name/gender)...");

        TextField roomSearch = new TextField();
        roomSearch.setPromptText("Search rooms (number/type/price)...");

        DatePicker startDate = new DatePicker(LocalDate.now());
        DatePicker endDate = new DatePicker(LocalDate.now().plusDays(1));

        Label status = new Label();
        Button admitBtn = new Button("Admit + Invoice");
        Button back = new Button("Back");

        Runnable refreshAvailableRooms = () -> {
            LocalDate s = startDate.getValue();
            LocalDate e = endDate.getValue();

            if (s == null || e == null || !e.isAfter(s)) {
                roomTable.setRooms(hospital.getRooms());
                roomTable.setFilter(roomSearch.getText());
                status.setText("Pick valid dates (end date after start date) to filter availability.");
                return;
            }

            roomTable.setRooms(hospital.findAvailableRooms(s, e));
            roomTable.setFilter(roomSearch.getText());
            status.setText("Available rooms updated.");
        };

        patientSearch.textProperty().addListener((obs, o, n) -> patientTable.setFilter(n));
        roomSearch.textProperty().addListener((obs, o, n) -> roomTable.setFilter(n));
        startDate.valueProperty().addListener((obs, o, n) -> refreshAvailableRooms.run());
        endDate.valueProperty().addListener((obs, o, n) -> refreshAvailableRooms.run());

        refreshAvailableRooms.run();

        admitBtn.setOnAction(e -> {
            try {
                Patient selectedPatient = patientTable.getTable().getSelectionModel().getSelectedItem();
                Room selectedRoom = roomTable.getTable().getSelectionModel().getSelectedItem();

                LocalDate s = startDate.getValue();
                LocalDate ed = endDate.getValue();

                Admission admission = hospital.admit(selectedPatient, selectedRoom, s, ed);

                // Show invoice popup + get invoice
                projectJava.Invoice invoice = InvoiceDialog.showAndReturn(admission);

                // Persist
                FileManager.saveAdmissions(hospital.getAdmissions());
                FileManager.appendInvoice(admission, invoice);

                refreshAvailableRooms.run();
                status.setText("Admitted and invoiced successfully.");

            } catch (ValidationException ex) {
                ErrorDialog.show("Validation Error", "Cannot admit patient", ex.getMessage());
            } catch (DataAccessException ex) {
                ErrorDialog.show("File Error", "Could not save admission/invoice", ex.getMessage());
            } catch (Exception ex) {
                ErrorDialog.showException("Unexpected Error", "Something went wrong", ex);
            }
        });

        // Use DashboardRouter for consistent back navigation
        back.setOnAction(e -> DashboardRouter.goHome(stage, hospital));

        HBox searchBar = new HBox(10,
                new Label("Patient Search:"), patientSearch,
                new Label("Room Search:"), roomSearch
        );
        searchBar.setPadding(new Insets(10));

        SplitPane split = new SplitPane(patientTable.getTable(), roomTable.getTable());
        split.setDividerPositions(0.5);

        GridPane controls = new GridPane();
        controls.setPadding(new Insets(10));
        controls.setVgap(10);
        controls.setHgap(10);
        controls.addRow(0, new Label("Start Date:"), startDate);
        controls.addRow(1, new Label("End Date:"), endDate);
        controls.addRow(2, admitBtn, back);
        controls.add(status, 1, 3);

        view.setTop(searchBar);
        view.setCenter(split);
        view.setBottom(controls);
    }

    public BorderPane getView() { return view; }
}