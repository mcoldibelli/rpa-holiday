package br.com.vaga_ambiental.Vaga.Ambiental.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CityAndStateDto {

  private String city;
  private String state;
}
