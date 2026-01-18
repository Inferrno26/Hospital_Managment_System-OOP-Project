package gui;

import exceptions.ValidationException;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import projectJava.Hospital;
import projectJava.Role;
import projectJava.UserAccount;

public class LoginView {

    private final VBox view = new VBox(12);

    public LoginView(Stage stage, Hospital hospital) {
        view.setPadding(new Insets(20));
        view.getStyleClass().add("card");

        Label title = new Label("Hospital Admissions System");
        title.getStyleClass().add("header-title");

        TextField username = new TextField();
        username.setPromptText("Username");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        ComboBox<Role> roleBox = new ComboBox<>();
        roleBox.getItems().addAll(Role.values());
        roleBox.setPromptText("Select role");

        Button loginBtn = new Button("Login");
        loginBtn.getStyleClass().add("btn-primary");
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        Button createAccountBtn = new Button("Create Account");
        createAccountBtn.getStyleClass().add("btn-secondary");
        createAccountBtn.setMaxWidth(Double.MAX_VALUE);

        Label hint = new Label("Create an account (role key required), then log in.");
        hint.setStyle("-fx-opacity: 0.85;");

        loginBtn.setOnAction(e -> {
            try {
                UserAccount account = hospital.login(
                        username.getText(),
                        password.getText(),
                        roleBox.getValue()
                );

                hospital.setCurrentUser(account);

                switch (account.getRole()) {
                    case RECEPTIONIST ->
                            stage.setScene(SceneUtil.create(new ReceptionistDashboard(stage, hospital).getView(), 820, 560));
                    case ADMIN ->
                            stage.setScene(SceneUtil.create(new AdminDashboard(stage, hospital).getView(), 860, 600));
                    case DOCTOR ->
                            stage.setScene(SceneUtil.create(new DoctorDashboard(stage, hospital).getView(), 860, 600));
                    case NURSE ->
                            stage.setScene(SceneUtil.create(new NurseDashboard(stage, hospital).getView(), 860, 600));
                    default ->
                            ErrorDialog.show("Access Limited", "No dashboard",
                                    "No dashboard implemented for role " + account.getRole());
                }

            } catch (ValidationException ex) {
                ErrorDialog.show("Login Failed", "Cannot login", ex.getMessage());
            } catch (Exception ex) {
                ErrorDialog.showException("Unexpected Error", "Login error", ex);
            }
        });

        createAccountBtn.setOnAction(e -> {
            try {
                new CreateAccountDialog().show(stage, hospital);
            } catch (Exception ex) {
                ErrorDialog.showException("Unexpected Error", "Could not open Create Account dialog", ex);
            }
        });

        view.getChildren().addAll(
                title,
                new Label("Username"), username,
                new Label("Password"), password,
                new Label("Role"), roleBox,
                loginBtn,
                createAccountBtn,
                hint
        );
    }

    public VBox getView() {
        return view;
    }
}
