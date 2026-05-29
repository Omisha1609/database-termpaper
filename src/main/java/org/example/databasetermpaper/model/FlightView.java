package org.example.databasetermpaper.model;

import java.time.LocalDateTime;

public record FlightView(
        int id,
        String flightNumber,
        String routeCode,
        String aircraft,
        LocalDateTime departure,
        LocalDateTime arrival,
        String status
) {
}
