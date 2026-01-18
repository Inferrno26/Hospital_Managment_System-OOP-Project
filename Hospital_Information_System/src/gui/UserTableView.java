package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import projectJava.Hospital;
import projectJava.UserAccount;

import java.util.List;

public class UserTableView {
    private final TableView<UserAccount> table = new TableView<>();
    private final ObservableList<UserAccount> masterData;
    private final FilteredList<UserAccount> filteredData;

    public UserTableView(Hospital hospital) {
        // Prefer to use the Hospital's ObservableList if available so the UI auto-updates
        List<UserAccount> accs = hospital.getAccounts();
        if (accs instanceof ObservableList) {
            masterData = (ObservableList<UserAccount>) accs;
        } else {
            masterData = FXCollections.observableArrayList(accs);
        }
        filteredData = new FilteredList<>(masterData, p -> true);

        TableColumn<UserAccount, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsername()));

        TableColumn<UserAccount, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRole().toString()));

        TableColumn<UserAccount, String> staffCol = new TableColumn<>("Staff");
        staffCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getStaffProfile() == null ? "" : c.getValue().getStaffProfile().getFullName()
        ));

        table.getColumns().addAll(usernameCol, roleCol, staffCol);

        SortedList<UserAccount> sorted = new SortedList<>(filteredData);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);
    }

    public void setFilter(String query) {
        String q = query == null ? "" : query.trim().toLowerCase();
        filteredData.setPredicate(u -> {
            if (q.isEmpty()) return true;
            return safe(u.getUsername()).contains(q)
                    || safe(u.getRole().toString()).contains(q)
                    || safe(u.getStaffProfile() == null ? "" : u.getStaffProfile().getFullName()).contains(q);
        });
    }

    private String safe(String s) { return s == null ? "" : s.toLowerCase(); }

    public TableView<UserAccount> getTable() { return table; }

    /**
     * Ensure the table's backing data matches the provided accounts list.
     * This will replace the contents of the masterData observable list so
     * the FilteredList/SortedList and TableView update automatically.
     */
    public void refresh(java.util.List<UserAccount> accounts) {
        if (accounts == null) return;
        // masterData is an ObservableList; setAll will update listeners
        masterData.setAll(accounts);
    }
}