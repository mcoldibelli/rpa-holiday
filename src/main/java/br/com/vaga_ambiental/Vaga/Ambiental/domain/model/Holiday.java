package br.com.vaga_ambiental.Vaga.Ambiental.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Data
@NoArgsConstructor
public class Holiday {
    private LocalDate date;
    private String name;
    private HolidayType type;

    public Holiday(String date, String name, HolidayType type) {
        this.date = parseDate(date);
        this.name = name;
        this.type = type;
    }

    private LocalDate parseDate(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(date, formatter);
        } catch(DateTimeParseException e) {
            throw new RuntimeException("Invalid date format: " + date, e);
        }
    }
}
