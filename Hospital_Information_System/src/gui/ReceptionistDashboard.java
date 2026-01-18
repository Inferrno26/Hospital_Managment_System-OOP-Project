package gui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import projectJava.Hospital;

public class ReceptionistDashboard {

    private final BorderPane view = new BorderPane();

    public ReceptionistDashboard(Stage stage, Hospital hospital) {
        HBox header = new HBox();
        header.getStyleClass().add("header");

        Label title = new Label("Receptionist Dashboard");
        title.getStyleClass().add("header-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logout = new Button("Logout");
        logout.getStyleClass().add("btn-secondary");
        logout.setOnAction(e -> {
            hospital.setCurrentUser(null);
            stage.setScene(SceneUtil.create(new LoginView(stage, hospital).getView(), 520, 390));
        });

        header.getChildren().addAll(title, spacer, logout);

        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setMaxWidth(460);

        Button registerPatient = primary("Register Patient");
        Button admissions = primary("Admissions (Assign Room)");
        Button invoices = primary("Invoices");

        Button manageRooms = secondary("Manage Rooms");
        Button viewPatients = secondary("View Patients");
        Button viewRooms = secondary("View Rooms");

        registerPatient.setOnAction(e -> stage.setScene(SceneUtil.create(
                new PatientForm(stage, hospital).getView(), 720, 520)));

        admissions.setOnAction(e -> stage.setScene(SceneUtil.create(
                new AdmissionView(stage, hospital).getView(), 1100, 650)));

        invoices.setOnAction(e -> stage.setScene(SceneUtil.create(
                new InvoicesView(stage, hospital).getView(), 1100, 560)));

        manageRooms.setOnAction(e -> stage.setScene(SceneUtil.create(
                new RoomManagementView(stage, hospital).getView(), 760, 520)));

        viewPatients.setOnAction(e -> stage.setScene(SceneUtil.create(
                new PatientListView(stage, hospital).getView(), 1000, 560)));

        viewRooms.setOnAction(e -> stage.setScene(SceneUtil.create(
                new RoomListView(stage, hospital).getView(), 1000, 560)));

        card.getChildren().addAll(
                new Label("Quick actions"),
                registerPatient, admissions, invoices,
                new Separator(),
                manageRooms, viewPatients, viewRooms
        );

        StackPane center = new StackPane(card);
        center.setPadding(new Insets(24));

        view.setTop(header);
        view.setCenter(center);
    }

    private Button primary(String text) {
        Button b = new Button(text);
        b.getStyleClass().add("btn-primary");
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }

    private Button secondary(String text) {
        Button b = new Button(text);
        b.getStyleClass().add("btn-secondary");
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }

    public BorderPane getView() { return view; }
}

