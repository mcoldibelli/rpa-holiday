package br.com.vaga_ambiental.Vaga.Ambiental.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class City {
    private String name;
    private String state;
}
