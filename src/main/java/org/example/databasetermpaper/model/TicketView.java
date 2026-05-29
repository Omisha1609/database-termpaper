package org.example.databasetermpaper.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TicketView(
        int id,
        String ticketNumber,
        String passengerName,
        String flightNumber,
        String routeCode,
        String cashierName,
        LocalDateTime saleTime,
        String seatNumber,
        BigDecimal price
) {
}
