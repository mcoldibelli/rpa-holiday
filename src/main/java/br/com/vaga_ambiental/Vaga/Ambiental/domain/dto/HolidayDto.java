package br.com.vaga_ambiental.Vaga.Ambiental.domain.dto;

import br.com.vaga_ambiental.Vaga.Ambiental.domain.enums.HolidayType;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HolidayDto {

  private LocalDate date;
  private HolidayType type;
  private String name;
}
