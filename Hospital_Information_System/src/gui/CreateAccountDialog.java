package gui;

import exceptions.DataAccessException;
import exceptions.ValidationException;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import projectJava.FileManager;
import projectJava.Hospital;
import projectJava.Role;

public class CreateAccountDialog {

    public void show(Stage owner, Hospital hospital) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Create Account");

        GridPane root = new GridPane();
        root.setPadding(new Insets(15));
        root.setHgap(10);
        root.setVgap(10);
        root.getStyleClass().add("card");

        TextField username = new TextField();
        username.setPromptText("Username");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        ComboBox<Role> roleBox = new ComboBox<>();
        roleBox.getItems().addAll(Role.values());
        roleBox.setPromptText("Select role");

        PasswordField roleKey = new PasswordField();
        roleKey.setPromptText("Secondary key for selected role");

        Label info = new Label(
                "A role key is required to create privileged accounts.\n" +
                "Ask the hospital/admin for the correct key."
        );
        info.setStyle("-fx-opacity: 0.85;");

        Button createBtn = new Button("Create");
        createBtn.getStyleClass().add("btn-primary");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("btn-secondary");

        createBtn.setOnAction(e -> {
            try {
                hospital.createAccount(
                        username.getText(),
                        password.getText(),
                        roleBox.getValue(),
                        roleKey.getText()
                );

                FileManager.saveAccounts(hospital.getAccounts());

                Alert ok = new Alert(Alert.AlertType.INFORMATION);
                ok.setTitle("Success");
                ok.setHeaderText("Account created");
                ok.setContentText("You can now log in with your new credentials.");
                ok.showAndWait();

                dialog.close();

            } catch (ValidationException ex) {
                ErrorDialog.show("Validation Error", "Cannot create account", ex.getMessage());
            } catch (DataAccessException ex) {
                ErrorDialog.show("File Error", "Could not save account", ex.getMessage());
            } catch (Exception ex) {
                ErrorDialog.showException("Unexpected Error", "Account creation failed", ex);
            }
        });

        cancelBtn.setOnAction(e -> dialog.close());

        int r = 0;
        root.addRow(r++, new Label("Username:"), username);
        root.addRow(r++, new Label("Password:"), password);
        root.addRow(r++, new Label("Role:"), roleBox);
        root.addRow(r++, new Label("Role Key:"), roleKey);
        root.add(info, 0, r++, 2, 1);
        root.addRow(r, createBtn, cancelBtn);

        Scene scene = new Scene(root, 460, 300);
        scene.getStylesheets().add(getClass().getResource("/gui/style.css").toExternalForm());
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}
