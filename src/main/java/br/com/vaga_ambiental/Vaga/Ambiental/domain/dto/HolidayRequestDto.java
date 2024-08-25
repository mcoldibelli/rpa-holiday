package br.com.vaga_ambiental.Vaga.Ambiental.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HolidayRequestDto {
    private String estado;
    private String cidade;
    private List<HolidayDto> feriados;
}
