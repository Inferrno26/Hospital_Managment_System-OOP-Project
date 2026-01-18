package gui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import projectJava.FileManager;
import projectJava.Hospital;
import projectJava.ParseUtil;
import exceptions.DataAccessException;
import exceptions.ValidationException;
import rooms.*;

public class RoomManagementView {
    private final VBox view = new VBox(10);

    public RoomManagementView(Stage stage, Hospital hospital) {
        view.setPadding(new Insets(20));

        TextField number = new TextField();
        TextField price = new TextField();
        ComboBox<RoomTypes> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(RoomTypes.values());

        Button add = new Button("Add Room");
        Button back = new Button("Back");

        add.setOnAction(e -> {
            try {
                String rn = ParseUtil.requireNonEmpty(number.getText(), "Room Number");
                double dp = ParseUtil.parseDouble(price.getText(), "Daily Price");
                if (dp <= 0) throw new ValidationException("Daily price must be > 0.");
                if (typeBox.getValue() == null) throw new ValidationException("Room type is required.");

                Room room;
                switch (typeBox.getValue()) {
                    case ICU: room = new IntensiveCareUnit(rn, dp); break;
                    case PRIVATE: room = new PrivateRoom(rn, dp); break;
                    default: room = new PatientRoom(rn, dp);
                }

                hospital.addRoom(room);
                FileManager.saveRooms(hospital.getRooms());

                Alert ok = new Alert(Alert.AlertType.INFORMATION, "Room saved.");
                ok.showAndWait();

            } catch (ValidationException ex) {
                ErrorDialog.show("Validation Error", "Invalid room data", ex.getMessage());
            } catch (DataAccessException ex) {
                ErrorDialog.show("File Error", "Could not save room", ex.getMessage());
            } catch (Exception ex) {
                ErrorDialog.showException("Unexpected Error", "Something went wrong", ex);
            }
        });

        // Use DashboardRouter for consistent back navigation
        back.setOnAction(e -> DashboardRouter.goHome(stage, hospital));

        view.getChildren().addAll(
                new Label("Room Number"), number,
                new Label("Daily Price"), price,
                new Label("Room Type"), typeBox,
                add, back
        );
    }

    public VBox getView() { return view; }
}