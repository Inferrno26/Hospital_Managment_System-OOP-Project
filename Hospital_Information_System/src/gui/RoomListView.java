package gui;

import exceptions.DataAccessException;
import exceptions.ValidationException;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import projectJava.FileManager;
import projectJava.Hospital;
import rooms.Room;

public class RoomListView {

    private final BorderPane view = new BorderPane();

    public RoomListView(Stage stage, Hospital hospital) {
        view.setPadding(new Insets(15));

        RoomTableView tableView = new RoomTableView();
        tableView.setRooms(hospital.getRooms());
        tableView.getTable().setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getTable().setFixedCellSize(32);

        TextField search = new TextField();
        search.setPromptText("Search by room number, type, price...");
        search.textProperty().addListener((obs, o, n) -> tableView.setFilter(n));

        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.getStyleClass().add("btn-secondary");

        deleteBtn.setOnAction(e -> {
            Room selected = tableView.getTable().getSelectionModel().getSelectedItem();
            if (selected == null) {
                ErrorDialog.show("Delete Room", "No selection", "Please select a room to delete.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setHeaderText("Delete room: " + selected.getRoomNumber());
            confirm.setContentText("This action cannot be undone.");
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

            try {
                hospital.deleteRoom(selected);
                FileManager.saveRooms(hospital.getRooms());

                tableView.setRooms(hospital.getRooms());
                tableView.setFilter(search.getText());

            } catch (ValidationException ex) {
                ErrorDialog.show("Cannot Delete", "Room deletion blocked", ex.getMessage());
            } catch (DataAccessException ex) {
                ErrorDialog.show("File Error", "Could not save rooms", ex.getMessage());
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
