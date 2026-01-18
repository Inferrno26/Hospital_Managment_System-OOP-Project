package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import rooms.Room;

import java.util.List;

public class RoomTableView {
    private final TableView<Room> table = new TableView<>();
    private final ObservableList<Room> masterData = FXCollections.observableArrayList();
    private final FilteredList<Room> filteredData = new FilteredList<>(masterData, r -> true);

    public RoomTableView() {
        TableColumn<Room, String> numberCol = new TableColumn<>("Room Number");
        numberCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        TableColumn<Room, Double> priceCol = new TableColumn<>("Daily Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("dailyPrice"));

        TableColumn<Room, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getRoomType().toString())
        );

        table.getColumns().addAll(numberCol, priceCol, typeCol);

        SortedList<Room> sorted = new SortedList<>(filteredData);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);
    }

    public void setRooms(List<Room> rooms) { masterData.setAll(rooms); }

    public void setFilter(String query) {
        String q = query == null ? "" : query.trim().toLowerCase();
        filteredData.setPredicate(r -> {
            if (q.isEmpty()) return true;

            String number = safe(r.getRoomNumber());
            String type = safe(r.getRoomType().toString());
            String price = String.valueOf(r.getDailyPrice());

            return number.contains(q) || type.contains(q) || price.contains(q);
        });
    }

    private String safe(String s) { return s == null ? "" : s.toLowerCase(); }

    public TableView<Room> getTable() { return table; }
}
