package gui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import projectJava.Hospital;

public class DoctorDashboard {

    private final BorderPane view = new BorderPane();

    public DoctorDashboard(Stage stage, Hospital hospital) {
        HBox header = new HBox();
        header.getStyleClass().add("header");

        Label title = new Label("Doctor Dashboard");
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

        Button viewPatients = primary("View Patients");
        Button invoices = secondary("Invoices");

        viewPatients.setOnAction(e -> stage.setScene(SceneUtil.create(
                new PatientListView(stage, hospital).getView(), 1000, 560)));

        invoices.setOnAction(e -> stage.setScene(SceneUtil.create(
                new InvoicesView(stage, hospital).getView(), 1100, 560)));

        card.getChildren().addAll(new Label("Doctor actions"), viewPatients, invoices);

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
