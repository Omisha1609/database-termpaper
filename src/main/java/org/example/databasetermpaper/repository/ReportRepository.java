package org.example.databasetermpaper.repository;

import org.example.databasetermpaper.db.Database;
import org.example.databasetermpaper.model.ReportRow;

import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ReportRepository {
    private final Database database;

    public ReportRepository(Database database) {
        this.database = database;
    }

    public List<ReportRow> transportationByRoute(LocalDate from, LocalDate to) {
        return runPeriodReport("""
                SELECT r.route_code AS "Маршрут",
                       dep_city.name AS "Пункт вылета",
                       arr_city.name AS "Пункт назначения",
                       COUNT(t.ticket_id) AS "Пассажиров",
                       SUM(t.price) AS "Сумма"
                FROM tickets t
                JOIN flights f ON f.flight_id = t.flight_id
                JOIN routes r ON r.route_id = f.route_id
                JOIN airports dep_airport ON dep_airport.airport_id = r.departure_airport_id
                JOIN airports arr_airport ON arr_airport.airport_id = r.arrival_airport_id
                JOIN cities dep_city ON dep_city.city_id = dep_airport.city_id
                JOIN cities arr_city ON arr_city.city_id = arr_airport.city_id
                WHERE f.scheduled_departure >= ? AND f.scheduled_departure < ?
                GROUP BY r.route_code, dep_city.name, arr_city.name
                ORDER BY "Сумма" DESC
                """, from, to);
    }

    public List<ReportRow> transportationByCountry(LocalDate from, LocalDate to) {
        return runPeriodReport("""
                SELECT arr_country.name AS "Страна назначения",
                       COUNT(t.ticket_id) AS "Пассажиров",
                       SUM(t.price) AS "Сумма"
                FROM tickets t
                JOIN flights f ON f.flight_id = t.flight_id
                JOIN routes r ON r.route_id = f.route_id
                JOIN airports arr_airport ON arr_airport.airport_id = r.arrival_airport_id
                JOIN cities arr_city ON arr_city.city_id = arr_airport.city_id
                JOIN countries arr_country ON arr_country.country_id = arr_city.country_id
                WHERE f.scheduled_departure >= ? AND f.scheduled_departure < ?
                GROUP BY arr_country.name
                ORDER BY "Сумма" DESC
                """, from, to);
    }

    public List<ReportRow> transportationByAircraftType(LocalDate from, LocalDate to) {
        return runPeriodReport("""
                SELECT at.manufacturer AS "Производитель",
                       at.model AS "Модель",
                       COUNT(t.ticket_id) AS "Пассажиров",
                       SUM(t.price) AS "Сумма"
                FROM tickets t
                JOIN flights f ON f.flight_id = t.flight_id
                JOIN aircraft a ON a.aircraft_id = f.aircraft_id
                JOIN aircraft_types at ON at.aircraft_type_id = a.aircraft_type_id
                WHERE f.scheduled_departure >= ? AND f.scheduled_departure < ?
                GROUP BY at.manufacturer, at.model
                ORDER BY "Сумма" DESC
                """, from, to);
    }

    public List<ReportRow> transportationByDeparturePoint(LocalDate from, LocalDate to) {
        return runPeriodReport("""
                SELECT dep_airport.iata_code AS "Аэропорт вылета",
                       dep_city.name AS "Пункт вылета",
                       COUNT(t.ticket_id) AS "Пассажиров",
                       SUM(t.price) AS "Сумма"
                FROM tickets t
                JOIN flights f ON f.flight_id = t.flight_id
                JOIN routes r ON r.route_id = f.route_id
                JOIN airports dep_airport ON dep_airport.airport_id = r.departure_airport_id
                JOIN cities dep_city ON dep_city.city_id = dep_airport.city_id
                WHERE f.scheduled_departure >= ? AND f.scheduled_departure < ?
                GROUP BY dep_airport.iata_code, dep_city.name
                ORDER BY "Пассажиров" DESC, "Сумма" DESC
                """, from, to);
    }

    public List<ReportRow> transportationByArrivalPoint(LocalDate from, LocalDate to) {
        return runPeriodReport("""
                SELECT arr_airport.iata_code AS "Аэропорт назначения",
                       arr_city.name AS "Пункт назначения",
                       COUNT(t.ticket_id) AS "Пассажиров",
                       SUM(t.price) AS "Сумма"
                FROM tickets t
                JOIN flights f ON f.flight_id = t.flight_id
                JOIN routes r ON r.route_id = f.route_id
                JOIN airports arr_airport ON arr_airport.airport_id = r.arrival_airport_id
                JOIN cities arr_city ON arr_city.city_id = arr_airport.city_id
                WHERE f.scheduled_departure >= ? AND f.scheduled_departure < ?
                GROUP BY arr_airport.iata_code, arr_city.name
                ORDER BY "Пассажиров" DESC, "Сумма" DESC
                """, from, to);
    }

    public List<ReportRow> salary(LocalDate from, LocalDate to) {
        return runPeriodReport("""
                SELECT e.personnel_number AS "Табельный номер",
                       e.role AS "Должность",
                       e.last_name || ' ' || e.first_name AS "Работник",
                       sp.base_amount AS "Оклад",
                       sp.hours_amount AS "За часы",
                       sp.bonus_amount AS "Премия",
                       sp.total_amount AS "Итого"
                FROM salary_payments sp
                JOIN employees e ON e.employee_id = sp.employee_id
                WHERE sp.period_start >= ? AND sp.period_end <= ?
                ORDER BY sp.total_amount DESC
                """, from, to);
    }

    private List<ReportRow> runPeriodReport(String sql, LocalDate from, LocalDate to) {
        try (var connection = database.getConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(from));
            statement.setDate(2, Date.valueOf(to));
            try (var resultSet = statement.executeQuery()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                List<ReportRow> rows = new ArrayList<>();
                while (resultSet.next()) {
                    LinkedHashMap<String, Object> values = new LinkedHashMap<>();
                    for (int column = 1; column <= metaData.getColumnCount(); column++) {
                        values.put(metaData.getColumnLabel(column), resultSet.getObject(column));
                    }
                    rows.add(ReportRow.of(values));
                }
                return rows;
            }
        } catch (SQLException exception) {
            throw new RepositoryException("Cannot run report", exception);
        }
    }
}
