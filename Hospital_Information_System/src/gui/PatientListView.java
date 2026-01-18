package gui;

import exceptions.DataAccessException;
import exceptions.ValidationException;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import person.Patient;
import projectJava.FileManager;
import projectJava.Hospital;

public class PatientListView {

    private final BorderPane view = new BorderPane();

    public PatientListView(Stage stage, Hospital hospital) {
        view.setPadding(new Insets(15));

        PatientTableView tableView = new PatientTableView(hospital);
        tableView.getTable().setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getTable().setFixedCellSize(32);

        TextField search = new TextField();
        search.setPromptText("Search by ID, name, gender...");
        search.textProperty().addListener((obs, o, n) -> tableView.setFilter(n));

        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.getStyleClass().add("btn-secondary");

        deleteBtn.setOnAction(e -> {
            Patient selected = tableView.getTable().getSelectionModel().getSelectedItem();
            if (selected == null) {
                ErrorDialog.show("Delete Patient", "No selection", "Please select a patient to delete.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setHeaderText("Delete patient: " + selected.getFullName());
            confirm.setContentText("This action cannot be undone.");
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

            try {
                hospital.deletePatient(selected);
                FileManager.savePatients(hospital.getPatients());

                tableView.getTable().getItems().setAll(hospital.getPatients());
                tableView.setFilter(search.getText());

            } catch (ValidationException ex) {
                ErrorDialog.show("Cannot Delete", "Patient deletion blocked", ex.getMessage());
            } catch (DataAccessException ex) {
                ErrorDialog.show("File Error", "Could not save patients", ex.getMessage());
            } catch (Exception ex) {
                ErrorDialog.showException("Unexpected Error", "Delete failed", ex);
            }
        });

        HBox topBar = new HBox(10, new Label("Search:"), search, deleteBtn);
        topBar.setPadding(new Insets(0, 0, 10, 0));

        Button back = new Button("Back");
        back.getStyleClass().add("btn-secondary");
        back.setOnAction(e -> DashboardRouter.goHome(stage, hospital));

        HBox bottomBar = new HBox(back);
        bottomBar.setPadding(new Insets(10, 0, 0, 0));

        view.setTop(topBar);
        view.setCenter(tableView.getTable());
        view.setBottom(bottomBar);
    }

    public BorderPane getView() { return view; }
}
