package br.com.vaga_ambiental.Vaga.Ambiental.domain.dto;

import br.com.vaga_ambiental.Vaga.Ambiental.domain.enums.HolidayType;
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
    private List<FeriadoDto> feriados;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeriadoDto {
        private String data;
        private HolidayType tipo;
        private String feriado;
    }
}
