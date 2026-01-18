package gui;

import exceptions.DataAccessException;
import javafx.application.Application;
import javafx.stage.Stage;
import projectJava.FileManager;
import projectJava.Hospital;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Hospital hospital = new Hospital();

        // LOAD DATA
        try {
            hospital.setPatients(FileManager.loadPatients());
            hospital.setRooms(FileManager.loadRooms());
            hospital.setAdmissions(FileManager.loadAdmissions(hospital));
            hospital.setAccounts(FileManager.loadAccounts());
        } catch (DataAccessException ex) {
            ErrorDialog.show("File Error", "Failed to load saved data", ex.getMessage());
        } catch (Exception ex) {
            ErrorDialog.showException("Unexpected Error", "Startup failed", ex);
        }

        // SAVE ON EXIT
        primaryStage.setOnCloseRequest(e -> {
            try {
                FileManager.savePatients(hospital.getPatients());
                FileManager.saveRooms(hospital.getRooms());
                FileManager.saveAdmissions(hospital.getAdmissions());
                FileManager.saveAccounts(hospital.getAccounts());
            } catch (DataAccessException ex) {
                ErrorDialog.show("File Error", "Failed to save data on exit", ex.getMessage());
            } catch (Exception ex) {
                ErrorDialog.showException("Unexpected Error", "Shutdown save failed", ex);
            }
        });

        primaryStage.setTitle("Hospital Admissions System");
        primaryStage.setScene(SceneUtil.create(new LoginView(primaryStage, hospital).getView(), 520, 390));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
