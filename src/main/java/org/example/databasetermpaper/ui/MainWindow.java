package org.example.databasetermpaper.ui;

import org.example.databasetermpaper.model.FlightView;
import org.example.databasetermpaper.model.Passenger;
import org.example.databasetermpaper.model.ReportRow;
import org.example.databasetermpaper.model.TicketView;
import org.example.databasetermpaper.repository.ReferenceRepository;
import org.example.databasetermpaper.repository.ReportRepository;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class MainWindow {
    private final ReferenceRepository referenceRepository;
    private final ReportRepository reportRepository;
    private final BorderPane content = new BorderPane();
    private final ListView<String> menu = new ListView<>();

    private final TableView<Passenger> passengerTable = new TableView<>();
    private final TextField passportField = new TextField();
    private final TextField lastNameField = new TextField();
    private final TextField firstNameField = new TextField();
    private final TextField middleNameField = new TextField();
    private final DatePicker birthDatePicker = new DatePicker();

    public MainWindow(ReferenceRepository referenceRepository, ReportRepository reportRepository) {
        this.referenceRepository = referenceRepository;
        this.reportRepository = reportRepository;
    }

    public Parent createContent() {
        menu.setItems(FXCollections.observableArrayList("Пассажиры", "Рейсы", "Билеты", "Отчеты"));
        menu.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        menu.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showSection(newValue));
        menu.setPrefWidth(180);

        BorderPane root = new BorderPane();
        root.setLeft(menu);
        root.setCenter(content);
        root.setPadding(new Insets(12));

        menu.getSelectionModel().selectFirst();
        return root;
    }

    private void showSection(String section) {
        try {
            switch (section) {
                case "Пассажиры" -> content.setCenter(createPassengersView());
                case "Рейсы" -> content.setCenter(createFlightsView());
                case "Билеты" -> content.setCenter(createTicketsView());
                case "Отчеты" -> content.setCenter(createReportsView());
                default -> content.setCenter(new Label("Раздел не найден"));
            }
        } catch (RuntimeException exception) {
            showError(exception.getMessage());
        }
    }

    private Parent createPassengersView() {
        passengerTable.getColumns().setAll(List.of(
                textColumn("ID", passenger -> String.valueOf(passenger.id()), 70),
                textColumn("Паспорт", Passenger::passportNumber, 130),
                textColumn("Фамилия", Passenger::lastName, 140),
                textColumn("Имя", Passenger::firstName, 140),
                textColumn("Отчество", passenger -> value(passenger.middleName()), 150),
                textColumn("Дата рождения", passenger -> passenger.birthDate().toString(), 140)
        ));
        passengerTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, passenger) -> fillPassengerForm(passenger));
        refreshPassengers();

        Button newButton = new Button("Новый");
        newButton.setOnAction(event -> clearPassengerForm());

        Button saveButton = new Button("Сохранить");
        saveButton.setDefaultButton(true);
        saveButton.setOnAction(event -> savePassenger());

        Button deleteButton = new Button("Удалить");
        deleteButton.setOnAction(event -> deleteSelectedPassenger());

        HBox actions = new HBox(8, newButton, saveButton, deleteButton);
        actions.setAlignment(Pos.CENTER_LEFT);

        VBox form = new VBox(10, new Label("Карточка пассажира"), passengerForm(), actions);
        form.setPadding(new Insets(12));
        form.setMinWidth(360);

        SplitPane splitPane = new SplitPane(passengerTable, form);
        splitPane.setDividerPositions(0.66);
        return withHeader("Пассажиры", splitPane);
    }

    private GridPane passengerForm() {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        addRow(grid, 0, "Паспорт", passportField);
        addRow(grid, 1, "Фамилия", lastNameField);
        addRow(grid, 2, "Имя", firstNameField);
        addRow(grid, 3, "Отчество", middleNameField);
        addRow(grid, 4, "Дата рождения", birthDatePicker);
        return grid;
    }

    private Parent createFlightsView() {
        TableView<FlightView> table = new TableView<>();
        table.getColumns().setAll(List.of(
                textColumn("ID", flight -> String.valueOf(flight.id()), 70),
                textColumn("Рейс", FlightView::flightNumber, 110),
                textColumn("Маршрут", FlightView::routeCode, 130),
                textColumn("Авиалайнер", FlightView::aircraft, 240),
                textColumn("Вылет", flight -> flight.departure().toString(), 180),
                textColumn("Прилет", flight -> flight.arrival().toString(), 180),
                textColumn("Статус", FlightView::status, 110)
        ));
        table.setItems(FXCollections.observableArrayList(referenceRepository.findFlights()));
        return withHeader("Расписание рейсов", table);
    }

    private Parent createTicketsView() {
        TableView<TicketView> table = new TableView<>();
        table.getColumns().setAll(List.of(
                textColumn("ID", ticket -> String.valueOf(ticket.id()), 70),
                textColumn("Билет", TicketView::ticketNumber, 130),
                textColumn("Пассажир", TicketView::passengerName, 180),
                textColumn("Рейс", TicketView::flightNumber, 100),
                textColumn("Маршрут", TicketView::routeCode, 110),
                textColumn("Кассир", TicketView::cashierName, 160),
                textColumn("Дата продажи", ticket -> ticket.saleTime().toString(), 180),
                textColumn("Место", TicketView::seatNumber, 80),
                textColumn("Цена", ticket -> ticket.price().toString(), 110)
        ));
        table.setItems(FXCollections.observableArrayList(referenceRepository.findTickets()));
        return withHeader("Проданные билеты", table);
    }

    private Parent createReportsView() {
        DatePicker fromPicker = new DatePicker(LocalDate.of(2026, 5, 1));
        DatePicker toPicker = new DatePicker(LocalDate.of(2026, 6, 1));
        ComboBox<String> reportType = new ComboBox<>(FXCollections.observableArrayList(
                "Перевозки по маршрутам",
                "Перевозки по странам",
                "Перевозки по пунктам вылета",
                "Перевозки по пунктам назначения",
                "Перевозки по типам самолетов",
                "Зарплата работников"
        ));
        reportType.getSelectionModel().selectFirst();

        TableView<ReportRow> reportTable = new TableView<>();
        Button runButton = new Button("Сформировать");
        runButton.setDefaultButton(true);
        runButton.setOnAction(event -> {
            List<ReportRow> rows = switch (reportType.getValue()) {
                case "Перевозки по странам" -> reportRepository.transportationByCountry(fromPicker.getValue(), toPicker.getValue());
                case "Перевозки по пунктам вылета" -> reportRepository.transportationByDeparturePoint(fromPicker.getValue(), toPicker.getValue());
                case "Перевозки по пунктам назначения" -> reportRepository.transportationByArrivalPoint(fromPicker.getValue(), toPicker.getValue());
                case "Перевозки по типам самолетов" -> reportRepository.transportationByAircraftType(fromPicker.getValue(), toPicker.getValue());
                case "Зарплата работников" -> reportRepository.salary(fromPicker.getValue(), toPicker.getValue().minusDays(1));
                default -> reportRepository.transportationByRoute(fromPicker.getValue(), toPicker.getValue());
            };
            fillReportTable(reportTable, rows);
        });

        HBox filters = new HBox(8,
                new Label("От"), fromPicker,
                new Label("До"), toPicker,
                reportType,
                runButton
        );
        filters.setAlignment(Pos.CENTER_LEFT);
        VBox box = new VBox(12, filters, reportTable);
        VBox.setVgrow(reportTable, Priority.ALWAYS);
        runButton.fire();
        return withHeader("Отчеты", box);
    }

    private void fillReportTable(TableView<ReportRow> table, List<ReportRow> rows) {
        table.getColumns().clear();
        if (rows.isEmpty()) {
            table.setItems(FXCollections.observableArrayList());
            return;
        }
        for (String columnName : rows.get(0).values().keySet()) {
            TableColumn<ReportRow, Object> column = new TableColumn<>(columnName);
            column.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().values().get(columnName)));
            column.setPrefWidth(170);
            table.getColumns().add(column);
        }
        table.setItems(FXCollections.observableArrayList(rows));
    }

    private Parent withHeader(String title, Parent body) {
        Label label = new Label(title);
        label.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        VBox wrapper = new VBox(12, label, body);
        wrapper.setPadding(new Insets(0, 0, 0, 12));
        VBox.setVgrow(body, Priority.ALWAYS);
        return wrapper;
    }

    private void refreshPassengers() {
        passengerTable.setItems(FXCollections.observableArrayList(referenceRepository.findPassengers()));
    }

    private void fillPassengerForm(Passenger passenger) {
        if (passenger == null) {
            return;
        }
        passportField.setText(passenger.passportNumber());
        lastNameField.setText(passenger.lastName());
        firstNameField.setText(passenger.firstName());
        middleNameField.setText(value(passenger.middleName()));
        birthDatePicker.setValue(passenger.birthDate());
    }

    private void clearPassengerForm() {
        passengerTable.getSelectionModel().clearSelection();
        passportField.clear();
        lastNameField.clear();
        firstNameField.clear();
        middleNameField.clear();
        birthDatePicker.setValue(null);
    }

    private void savePassenger() {
        Passenger selected = passengerTable.getSelectionModel().getSelectedItem();
        if (passportField.getText().isBlank() || lastNameField.getText().isBlank()
                || firstNameField.getText().isBlank() || birthDatePicker.getValue() == null) {
            showError("Заполните паспорт, фамилию, имя и дату рождения.");
            return;
        }
        Passenger passenger = new Passenger(
                selected == null ? 0 : selected.id(),
                passportField.getText().trim(),
                lastNameField.getText().trim(),
                firstNameField.getText().trim(),
                middleNameField.getText().isBlank() ? null : middleNameField.getText().trim(),
                birthDatePicker.getValue()
        );
        referenceRepository.savePassenger(passenger);
        refreshPassengers();
        clearPassengerForm();
    }

    private void deleteSelectedPassenger() {
        Passenger passenger = passengerTable.getSelectionModel().getSelectedItem();
        if (passenger == null) {
            showError("Выберите пассажира для удаления.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Удалить выбранного пассажира?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(button -> {
            if (button == ButtonType.YES) {
                referenceRepository.deletePassenger(passenger.id());
                refreshPassengers();
                clearPassengerForm();
            }
        });
    }

    private <T> TableColumn<T, String> textColumn(String title, ValueProvider<T> provider, int width) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> new SimpleStringProperty(provider.get(data.getValue())));
        column.setPrefWidth(width);
        return column;
    }

    private void addRow(GridPane grid, int row, String labelText, javafx.scene.Node field) {
        Label label = new Label(labelText);
        grid.add(label, 0, row);
        grid.add(field, 1, row);
        GridPane.setHgrow(field, Priority.ALWAYS);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setHeaderText("Ошибка");
        alert.showAndWait();
    }

    private String value(String text) {
        return text == null ? "" : text;
    }

    @FunctionalInterface
    private interface ValueProvider<T> {
        String get(T value);
    }
}
