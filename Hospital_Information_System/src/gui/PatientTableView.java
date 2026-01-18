package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import person.Patient;
import projectJava.Hospital;

public class PatientTableView {
    private final TableView<Patient> table = new TableView<>();
    private final ObservableList<Patient> masterData;
    private final FilteredList<Patient> filteredData;

    public PatientTableView(Hospital hospital) {
        masterData = FXCollections.observableArrayList(hospital.getPatients());
        filteredData = new FilteredList<>(masterData, p -> true);

        TableColumn<Patient, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Patient, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        TableColumn<Patient, String> genderCol = new TableColumn<>("Gender");
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));

        TableColumn<Patient, Double> bmiCol = new TableColumn<>("BMI");
        bmiCol.setCellValueFactory(new PropertyValueFactory<>("bmi"));

        table.getColumns().addAll(idCol, nameCol, genderCol, bmiCol);

        SortedList<Patient> sorted = new SortedList<>(filteredData);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);
    }

    public void setFilter(String query) {
        String q = query == null ? "" : query.trim().toLowerCase();
        filteredData.setPredicate(p -> {
            if (q.isEmpty()) return true;
            return safe(p.getId()).contains(q)
                    || safe(p.getFullName()).contains(q)
                    || safe(p.getGender()).contains(q);
        });
    }

    private String safe(String s) { return s == null ? "" : s.toLowerCase(); }

    public TableView<Patient> getTable() { return table; }
}
