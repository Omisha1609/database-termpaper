package org.example.databasetermpaper.model;

import java.time.LocalDate;

public record Passenger(
        int id,
        String passportNumber,
        String lastName,
        String firstName,
        String middleName,
        LocalDate birthDate
) {
}
