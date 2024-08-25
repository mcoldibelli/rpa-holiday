package br.com.vaga_ambiental.Vaga.Ambiental.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

public class DateUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    public static String format(LocalDate date) {
        return DATE_FORMATTER.format(date);
    }

    public static String format(LocalDateTime dateTime) {
        return DATE_FORMATTER.format(dateTime);
    }

    public static LocalDate parse(String dateString) {
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }
}
