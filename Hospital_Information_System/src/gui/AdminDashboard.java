package gui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import projectJava.Hospital;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import projectJava.FileManager;
import projectJava.UserAccount;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class AdminDashboard {

    private final BorderPane view = new BorderPane();

    public AdminDashboard(Stage stage, Hospital hospital) {
        HBox header = new HBox();
        header.getStyleClass().add("header");

        Label title = new Label("Admin Dashboard");
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

        Button manageRooms = primary("Manage Rooms");
        Button invoices = primary("Invoices");
        Button manageUsers = primary("Manage Users");

        Button viewPatients = secondary("View Patients");
        Button viewRooms = secondary("View Rooms");

        manageUsers.setOnAction(e -> stage.setScene(SceneUtil.create(
                new UserListView(stage, hospital).getView(), 760, 520)));

        manageRooms.setOnAction(e -> stage.setScene(SceneUtil.create(
                new RoomManagementView(stage, hospital).getView(), 760, 520)));

        invoices.setOnAction(e -> stage.setScene(SceneUtil.create(
                new InvoicesView(stage, hospital).getView(), 1100, 560)));

        viewPatients.setOnAction(e -> stage.setScene(SceneUtil.create(
                new PatientListView(stage, hospital).getView(), 1000, 560)));

        viewRooms.setOnAction(e -> stage.setScene(SceneUtil.create(
                new RoomListView(stage, hospital).getView(), 1000, 560)));

        card.getChildren().addAll(
                new Label("Administration"),
                manageRooms, invoices, manageUsers,
                new Separator(),
                viewPatients, viewRooms
        );

        // Easter egg: "NUKE" button for user "Dion"
        UserAccount current = hospital.getCurrentUser();
        if (current != null && current.getUsername() != null && current.getUsername().equalsIgnoreCase("Dion")) {
            Button nuke = new Button("NUKE");
            nuke.setStyle("-fx-background-color: #c82333; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px; -fx-padding: 12px 20px;");
            nuke.setMaxWidth(Double.MAX_VALUE);
            nuke.setOnAction(evt -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirm System Reset");
                confirm.setHeaderText("Danger: This will permanently reset the system and wipe all persisted data.");
                confirm.setContentText("Type YES to confirm irreversible reset.");

                // Custom confirmation: require user to type YES in the dialog's headerless text area is cumbersome with simple Alert, so prompt a second confirmation.
                Optional<ButtonType> res = confirm.showAndWait();
                if (res.isPresent() && res.get() == ButtonType.OK) {
                    // second confirmation
                    Alert confirm2 = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm2.setTitle("Double-check");
                    confirm2.setHeaderText("Are you absolutely sure? This action cannot be undone.");
                    Optional<ButtonType> res2 = confirm2.showAndWait();
                    if (res2.isPresent() && res2.get() == ButtonType.OK) {
                        // Perform wipe: clear in-memory data
                        hospital.setPatients(new ArrayList<>());
                        hospital.setRooms(new ArrayList<>());
                        hospital.setAdmissions(new ArrayList<>());
                        hospital.setAppointments(new ArrayList<>());
                        hospital.setAccounts(new ArrayList<>());

                        // Persist empty state using FileManager where available
                        try {
                            FileManager.savePatients(new ArrayList<>());
                        } catch (Exception ex) {
                            System.err.println("Failed to clear patients file: " + ex.getMessage());
                        }
                        try {
                            FileManager.saveRooms(new ArrayList<>());
                        } catch (Exception ex) {
                            System.err.println("Failed to clear rooms file: " + ex.getMessage());
                        }
                        try {
                            FileManager.saveAdmissions(new ArrayList<>());
                        } catch (Exception ex) {
                            System.err.println("Failed to clear admissions file: " + ex.getMessage());
                        }
                        try {
                            FileManager.saveAppointments(new ArrayList<>());
                        } catch (Exception ex) {
                            System.err.println("Failed to clear appointments file: " + ex.getMessage());
                        }
                        try {
                            FileManager.saveAccounts(new ArrayList<>());
                        } catch (Exception ex) {
                            System.err.println("Failed to clear accounts file: " + ex.getMessage());
                        }

                        // invoices.txt has only append in FileManager; truncate it here
                        try (FileWriter fw = new FileWriter(new File("invoices.txt"), false)) {
                            // truncate by opening with false and closing
                        } catch (IOException ex) {
                            System.err.println("Failed to clear invoices file: " + ex.getMessage());
                        }

                        Alert done = new Alert(Alert.AlertType.INFORMATION);
                        done.setTitle("Reset Complete");
                        done.setHeaderText(null);
                        done.setContentText("System reset complete. The application will return to the login screen.");
                        done.showAndWait();

                        hospital.setCurrentUser(null);
                        stage.setScene(SceneUtil.create(new LoginView(stage, hospital).getView(), 520, 390));
                    }
                }
            });

            VBox nukeCard = new VBox(12);
            nukeCard.getStyleClass().add("card");
            nukeCard.setMaxWidth(460);
            nukeCard.getChildren().addAll(new Label("Special Access"), nuke);

            StackPane center = new StackPane(nukeCard);
            center.setPadding(new Insets(24));

            view.setTop(header);
            view.setCenter(center);
            return;
        }

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