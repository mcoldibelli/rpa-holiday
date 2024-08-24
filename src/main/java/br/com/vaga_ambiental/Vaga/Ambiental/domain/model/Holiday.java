package br.com.vaga_ambiental.Vaga.Ambiental.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Holiday {
    private LocalDate date;
    private String name;
    private HolidayType type;
}
