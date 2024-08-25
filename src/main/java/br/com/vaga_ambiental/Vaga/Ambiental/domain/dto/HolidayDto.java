package br.com.vaga_ambiental.Vaga.Ambiental.domain.dto;

import br.com.vaga_ambiental.Vaga.Ambiental.domain.enums.HolidayType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HolidayDto {
    private LocalDate date;
    private HolidayType type;
    private String name;
}
