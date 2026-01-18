package gui;

import gui.InvoiceRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import projectJava.FileManager;
import projectJava.Hospital;

public class InvoicesView {

    private final BorderPane view = new BorderPane();

    private final TableView<InvoiceRecord> table = new TableView<>();
    private final ObservableList<InvoiceRecord> masterData;
    private final FilteredList<InvoiceRecord> filteredData;

    public InvoicesView(Stage stage, Hospital hospital) {
        view.setPadding(new Insets(15));

        masterData = FXCollections.observableArrayList(FileManager.loadInvoices());
        filteredData = new FilteredList<>(masterData, inv -> true);

        TableColumn<InvoiceRecord, String> invIdCol = new TableColumn<>("Invoice ID");
        invIdCol.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));

        TableColumn<InvoiceRecord, String> patientIdCol = new TableColumn<>("Patient ID");
        patientIdCol.setCellValueFactory(new PropertyValueFactory<>("patientId"));

        TableColumn<InvoiceRecord, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        TableColumn<InvoiceRecord, String> startCol = new TableColumn<>("Start");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        TableColumn<InvoiceRecord, String> endCol = new TableColumn<>("End");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        TableColumn<InvoiceRecord, Double> baseCol = new TableColumn<>("Base");
        baseCol.setCellValueFactory(new PropertyValueFactory<>("baseAmount"));

        TableColumn<InvoiceRecord, Double> vatRateCol = new TableColumn<>("VAT Rate");
        vatRateCol.setCellValueFactory(new PropertyValueFactory<>("vatRate"));

        TableColumn<InvoiceRecord, Double> vatCol = new TableColumn<>("VAT Amount");
        vatCol.setCellValueFactory(new PropertyValueFactory<>("vatAmount"));

        TableColumn<InvoiceRecord, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        table.getColumns().addAll(invIdCol, patientIdCol, roomCol, startCol, endCol, baseCol, vatRateCol, vatCol, totalCol);
        // Replace deprecated CONSTRAINED_RESIZE_POLICY with explicit percentage-based column bindings.
        // Percentages chosen to reflect approximate relative widths (sum ~ 1.0). Adjust as needed.
        double[] colPercents = {0.12, 0.12, 0.08, 0.10, 0.10, 0.12, 0.10, 0.12, 0.14};
        for (int i = 0; i < table.getColumns().size(); i++) {
            final int idx = i;
            TableColumn<?, ?> col = table.getColumns().get(i);
            col.prefWidthProperty().bind(table.widthProperty().multiply(colPercents[idx]));
        }
        table.setFixedCellSize(32);

        TextField search = new TextField();
        search.setPromptText("Search by invoiceId, patientId, room, date, amount...");

        search.textProperty().addListener((obs, oldV, newV) -> {
            String q = (newV == null) ? "" : newV.trim().toLowerCase();
            filteredData.setPredicate(inv -> {
                if (q.isEmpty()) return true;
                return inv.getInvoiceId().toLowerCase().contains(q)
                        || inv.getPatientId().toLowerCase().contains(q)
                        || inv.getRoomNumber().toLowerCase().contains(q)
                        || inv.getStartDate().toLowerCase().contains(q)
                        || inv.getEndDate().toLowerCase().contains(q)
                        || String.valueOf(inv.getBaseAmount()).contains(q)
                        || String.valueOf(inv.getTotalAmount()).contains(q);
            });
        });

        SortedList<InvoiceRecord> sorted = new SortedList<>(filteredData);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);

        Button refresh = new Button("Refresh");
        refresh.getStyleClass().add("btn-secondary");
        refresh.setOnAction(e -> masterData.setAll(FileManager.loadInvoices()));

        Button showDetails = new Button("Show Invoice");
        showDetails.getStyleClass().add("btn-primary");
        showDetails.setOnAction(e -> {
            InvoiceRecord selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invoice Details");
            alert.setHeaderText("Invoice ID: " + selected.getInvoiceId());
            alert.setContentText(
                    "Patient ID: " + selected.getPatientId() + "\n" +
                    "Room: " + selected.getRoomNumber() + "\n" +
                    "Stay: " + selected.getStartDate() + " to " + selected.getEndDate() + "\n\n" +
                    "Base: " + selected.getBaseAmount() + "\n" +
                    "VAT Rate: " + (selected.getVatRate() * 100) + "%\n" +
                    "VAT Amount: " + selected.getVatAmount() + "\n" +
                    "Total: " + selected.getTotalAmount()
            );
            alert.showAndWait();
        });

        HBox topBar = new HBox(10, new Label("Search:"), search, refresh, showDetails);
        topBar.setPadding(new Insets(0, 0, 10, 0));

        Button back = new Button("Back");
        back.getStyleClass().add("btn-secondary");
        back.setOnAction(e -> DashboardRouter.goHome(stage, hospital));

        HBox bottomBar = new HBox(back);
        bottomBar.setPadding(new Insets(10, 0, 0, 0));

        view.setTop(topBar);
        view.setCenter(table);
        view.setBottom(bottomBar);
    }

    public BorderPane getView() { return view; }
}