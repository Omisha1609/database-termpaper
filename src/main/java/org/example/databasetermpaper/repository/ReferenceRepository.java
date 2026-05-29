package org.example.databasetermpaper.repository;

import org.example.databasetermpaper.db.Database;
import org.example.databasetermpaper.model.FlightView;
import org.example.databasetermpaper.model.Passenger;
import org.example.databasetermpaper.model.TicketView;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ReferenceRepository {
    private final Database database;

    public ReferenceRepository(Database database) {
        this.database = database;
    }

    public List<Passenger> findPassengers() {
        String sql = """
                SELECT passenger_id, passport_number, last_name, first_name, middle_name, birth_date
                FROM passengers
                ORDER BY last_name, first_name
                """;
        try (var connection = database.getConnection();
             var statement = connection.prepareStatement(sql);
             var resultSet = statement.executeQuery()) {
            List<Passenger> passengers = new ArrayList<>();
            while (resultSet.next()) {
                passengers.add(new Passenger(
                        resultSet.getInt("passenger_id"),
                        resultSet.getString("passport_number"),
                        resultSet.getString("last_name"),
                        resultSet.getString("first_name"),
                        resultSet.getString("middle_name"),
                        resultSet.getDate("birth_date").toLocalDate()
                ));
            }
            return passengers;
        } catch (SQLException exception) {
            throw new RepositoryException("Cannot load passengers", exception);
        }
    }

    public void savePassenger(Passenger passenger) {
        String insertSql = """
                INSERT INTO passengers (passport_number, last_name, first_name, middle_name, birth_date)
                VALUES (?, ?, ?, ?, ?)
                """;
        String updateSql = """
                UPDATE passengers
                SET passport_number = ?, last_name = ?, first_name = ?, middle_name = ?, birth_date = ?
                WHERE passenger_id = ?
                """;
        try (var connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(passenger.id() == 0 ? insertSql : updateSql)) {
            statement.setString(1, passenger.passportNumber());
            statement.setString(2, passenger.lastName());
            statement.setString(3, passenger.firstName());
            statement.setString(4, passenger.middleName());
            statement.setDate(5, Date.valueOf(passenger.birthDate()));
            if (passenger.id() != 0) {
                statement.setInt(6, passenger.id());
            }
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RepositoryException("Cannot save passenger", exception);
        }
    }

    public void deletePassenger(int passengerId) {
        try (var connection = database.getConnection();
             var statement = connection.prepareStatement("DELETE FROM passengers WHERE passenger_id = ?")) {
            statement.setInt(1, passengerId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RepositoryException("Cannot delete passenger. Check linked tickets.", exception);
        }
    }

    public List<FlightView> findFlights() {
        String sql = """
                SELECT f.flight_id,
                       f.flight_number,
                       r.route_code,
                       at.manufacturer || ' ' || at.model || ' / ' || a.registration_number AS aircraft,
                       f.scheduled_departure,
                       f.scheduled_arrival,
                       f.status
                FROM flights f
                JOIN routes r ON r.route_id = f.route_id
                JOIN aircraft a ON a.aircraft_id = f.aircraft_id
                JOIN aircraft_types at ON at.aircraft_type_id = a.aircraft_type_id
                ORDER BY f.scheduled_departure
                """;
        try (var connection = database.getConnection();
             var statement = connection.prepareStatement(sql);
             var resultSet = statement.executeQuery()) {
            List<FlightView> flights = new ArrayList<>();
            while (resultSet.next()) {
                flights.add(new FlightView(
                        resultSet.getInt("flight_id"),
                        resultSet.getString("flight_number"),
                        resultSet.getString("route_code"),
                        resultSet.getString("aircraft"),
                        resultSet.getTimestamp("scheduled_departure").toLocalDateTime(),
                        resultSet.getTimestamp("scheduled_arrival").toLocalDateTime(),
                        resultSet.getString("status")
                ));
            }
            return flights;
        } catch (SQLException exception) {
            throw new RepositoryException("Cannot load flights", exception);
        }
    }

    public List<TicketView> findTickets() {
        String sql = """
                SELECT t.ticket_id,
                       t.ticket_number,
                       p.last_name || ' ' || p.first_name AS passenger_name,
                       f.flight_number,
                       r.route_code,
                       e.last_name || ' ' || e.first_name AS cashier_name,
                       t.sale_timestamp,
                       t.seat_number,
                       t.price
                FROM tickets t
                JOIN passengers p ON p.passenger_id = t.passenger_id
                JOIN flights f ON f.flight_id = t.flight_id
                JOIN routes r ON r.route_id = f.route_id
                JOIN employees e ON e.employee_id = t.cashier_id
                ORDER BY t.sale_timestamp DESC
                """;
        try (var connection = database.getConnection();
             var statement = connection.prepareStatement(sql);
             var resultSet = statement.executeQuery()) {
            List<TicketView> tickets = new ArrayList<>();
            while (resultSet.next()) {
                tickets.add(readTicket(resultSet));
            }
            return tickets;
        } catch (SQLException exception) {
            throw new RepositoryException("Cannot load tickets", exception);
        }
    }

    private TicketView readTicket(ResultSet resultSet) throws SQLException {
        Timestamp saleTimestamp = resultSet.getTimestamp("sale_timestamp");
        BigDecimal price = resultSet.getBigDecimal("price");
        return new TicketView(
                resultSet.getInt("ticket_id"),
                resultSet.getString("ticket_number"),
                resultSet.getString("passenger_name"),
                resultSet.getString("flight_number"),
                resultSet.getString("route_code"),
                resultSet.getString("cashier_name"),
                saleTimestamp.toLocalDateTime(),
                resultSet.getString("seat_number"),
                price
        );
    }
}
