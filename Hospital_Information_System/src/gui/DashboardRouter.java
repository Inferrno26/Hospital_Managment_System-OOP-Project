package gui;

import javafx.stage.Stage;
import projectJava.Hospital;
import projectJava.Role;
import projectJava.UserAccount;

public class DashboardRouter {

    public static void goHome(Stage stage, Hospital hospital) {
        UserAccount u = hospital.getCurrentUser();

        if (u == null) {
            stage.setScene(SceneUtil.create(new LoginView(stage, hospital).getView(), 520, 390));
            return;
        }

        Role r = u.getRole();
        switch (r) {
            case RECEPTIONIST ->
                    stage.setScene(SceneUtil.create(new ReceptionistDashboard(stage, hospital).getView(), 820, 560));
            case ADMIN ->
                    stage.setScene(SceneUtil.create(new AdminDashboard(stage, hospital).getView(), 860, 600));
            case DOCTOR ->
                    stage.setScene(SceneUtil.create(new DoctorDashboard(stage, hospital).getView(), 860, 600));
            case NURSE ->
                    stage.setScene(SceneUtil.create(new NurseDashboard(stage, hospital).getView(), 860, 600));
            default ->
                    stage.setScene(SceneUtil.create(new LoginView(stage, hospital).getView(), 520, 390));
        }
    }
}
