package gui;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import projectJava.Admission;
import projectJava.Hospital;

public class AdmissionsHistoryView {
    private final BorderPane view = new BorderPane();

    public AdmissionsHistoryView(Stage stage, Hospital hospital) {
        view.setPadding(new Insets(15));

        TableView<Admission> table = new TableView<>();
        table.getItems().setAll(hospital.getAdmissions());

        TableColumn<Admission, String> patientCol = new TableColumn<>("Patient ID");
        patientCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPatient().getId()));

        TableColumn<Admission, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRoom().getRoomNumber()));

        TableColumn<Admission, String> startCol = new TableColumn<>("Start");
        startCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStartDate().toString()));

        TableColumn<Admission, String> endCol = new TableColumn<>("End");
        endCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEndDate().toString()));

        TableColumn<Admission, Double> costCol = new TableColumn<>("Cost");
        costCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getCost()).asObject());

        table.getColumns().addAll(patientCol, roomCol, startCol, endCol, costCol);

        Button back = new Button("Back");
        back.setOnAction(e -> DashboardRouter.goHome(stage, hospital));

        HBox bottom = new HBox(10, back);
        bottom.setPadding(new Insets(10));

        view.setCenter(table);
        view.setBottom(bottom);
    }

    public BorderPane getView() { return view; }
}