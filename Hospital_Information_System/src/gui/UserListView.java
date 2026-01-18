package gui;

import exceptions.DataAccessException;
import exceptions.ValidationException;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import projectJava.FileManager;
import projectJava.Hospital;
import projectJava.UserAccount;
import projectJava.Role;

public class UserListView {

    private final BorderPane view = new BorderPane();

    public UserListView(Stage stage, Hospital hospital) {
        view.setPadding(new Insets(15));

        UserTableView tableView = new UserTableView(hospital);
        tableView.getTable().setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getTable().setFixedCellSize(32);

        TextField search = new TextField();
        search.setPromptText("Search by username, role, staff...");
        search.textProperty().addListener((obs, o, n) -> tableView.setFilter(n));

        // Show current logged-in user info so admins can verify they're signed in
        Label userInfo = new Label();
        UserAccount cu = hospital.getCurrentUser();
        userInfo.setText("Logged in as: " + (cu == null ? "<none>" : cu.getUsername() + " (" + cu.getRole() + ")"));

        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.getStyleClass().add("btn-secondary");

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #b00; -fx-font-size: 11px;");

        // Helper to update delete button and status based on selection and requester
        Runnable updateDeleteState = () -> {
            UserAccount requester = hospital.getCurrentUser();
            UserAccount sel = tableView.getTable().getSelectionModel().getSelectedItem();

            if (requester == null || requester.getRole() != Role.ADMIN) {
                deleteBtn.setDisable(true);
                statusLabel.setText("Requires admin login to delete users.");
                return;
            }

            if (sel == null) {
                deleteBtn.setDisable(true);
                statusLabel.setText("Select a user to delete.");
                return;
            }

            if (sel.getUsername().equalsIgnoreCase(requester.getUsername())) {
                deleteBtn.setDisable(true);
                statusLabel.setText("Cannot delete your own account.");
                return;
            }

            // count admins
            int admins = 0;
            for (UserAccount a : hospital.getAccounts()) if (a.getRole() == Role.ADMIN) admins++;
            if (sel.getRole() == Role.ADMIN && admins <= 1) {
                deleteBtn.setDisable(true);
                statusLabel.setText("Cannot delete the last admin account.");
                return;
            }

            deleteBtn.setDisable(false);
            statusLabel.setText("");
        };

        // Tooltip on delete button to display current reason even when disabled
        javafx.scene.control.Tooltip deleteTip = new javafx.scene.control.Tooltip();
        javafx.scene.control.Tooltip.install(deleteBtn, deleteTip);

        // update when selection changes
        tableView.getTable().getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> updateDeleteState.run());

        // update tooltip text whenever state changes
        // we wrap updateDeleteState so we can also update tooltip text
        Runnable updateAllState = () -> {
            updateDeleteState.run();
            deleteTip.setText(statusLabel.getText());
        };

        // use updateAllState initially and for selection changes
        tableView.getTable().getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> updateAllState.run());

        // update when view is constructed or later (cover case where user logged in after view created)
        updateAllState.run();
        // ensure initial label reflects live state
        UserAccount nowInit = hospital.getCurrentUser();
        userInfo.setText("Logged in as: " + (nowInit == null ? "<none>" : nowInit.getUsername() + " (" + nowInit.getRole() + ")"));

        deleteBtn.setOnAction(e -> {
            UserAccount selected = tableView.getTable().getSelectionModel().getSelectedItem();
            if (selected == null) {
                ErrorDialog.show("Delete User", "No selection", "Please select a user to delete.");
                return;
            }

            UserAccount requester = hospital.getCurrentUser();
            if (requester == null || requester.getRole() != Role.ADMIN) {
                ErrorDialog.show("Not allowed", "Admin privileges required",
                        "You must be logged in as an admin to delete users.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setHeaderText("Delete user: " + selected.getUsername());
            confirm.setContentText("This action cannot be undone.");
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

            try {
                hospital.deleteAccount(requester, selected.getUsername());
                FileManager.saveAccounts(hospital.getAccounts());

                // If Hospital provides an ObservableList, the table will auto-update when the model changes.
                // Re-apply filter (keeps current query) and re-evaluate UI state.
                tableView.setFilter(search.getText());
                updateAllState.run();

                // Refresh current user label (in case accounts file changed)
                UserAccount now = hospital.getCurrentUser();
                userInfo.setText("Logged in as: " + (now == null ? "<none>" : now.getUsername() + " (" + now.getRole() + ")"));

                // Update delete state after change
                updateDeleteState.run();

            } catch (ValidationException ex) {
                ErrorDialog.show("Cannot Delete", "User deletion blocked", ex.getMessage());
            } catch (DataAccessException ex) {
                ErrorDialog.show("File Error", "Could not save users", ex.getMessage());
            } catch (Exception ex) {
                ErrorDialog.showException("Unexpected Error", "Delete failed", ex);
            }
        });

        Button refreshBtn = new Button("Refresh");
        refreshBtn.getStyleClass().add("btn-secondary");
        refreshBtn.setOnAction(e -> {
            // ensure table is in sync with hospital accounts and re-evaluate state
            tableView.refresh(hospital.getAccounts());
            UserAccount now = hospital.getCurrentUser();
            userInfo.setText("Logged in as: " + (now == null ? "<none>" : now.getUsername() + " (" + now.getRole() + ")"));
            updateAllState.run();
        });

        Button debugBtn = new Button("Debug Info");
        debugBtn.getStyleClass().add("btn-secondary");
        debugBtn.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();
            UserAccount now = hospital.getCurrentUser();
            sb.append("Current user: ").append(now == null ? "<none>" : now.getUsername() + " (" + now.getRole() + ")").append("\n\n");
            sb.append("Accounts:\n");
            for (UserAccount a : hospital.getAccounts()) {
                sb.append(" - ").append(a.getUsername()).append(" (").append(a.getRole()).append(")\n");
            }
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Debug Info");
            info.setHeaderText("Current session state");
            info.setContentText(sb.toString());
            info.showAndWait();
        });

        HBox topBar = new HBox(10, new Label("Search:"), search, deleteBtn, refreshBtn, debugBtn);
        topBar.setPadding(new Insets(0, 0, 10, 0));

        VBox topContainer = new VBox(6, userInfo, topBar, statusLabel);
        topContainer.setPadding(new Insets(0,0,6,0));

        Button back = new Button("Back");
        back.getStyleClass().add("btn-secondary");
        back.setOnAction(e -> DashboardRouter.goHome(stage, hospital));

        HBox bottomBar = new HBox(back);
        bottomBar.setPadding(new Insets(10, 0, 0, 0));

        view.setTop(topContainer);
        view.setCenter(tableView.getTable());
        view.setBottom(bottomBar);
    }

    public BorderPane getView() { return view; }
}