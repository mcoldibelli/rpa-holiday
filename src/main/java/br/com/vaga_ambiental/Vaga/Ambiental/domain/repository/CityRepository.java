package br.com.vaga_ambiental.Vaga.Ambiental.domain.repository;

import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.CityModel;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.StateModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<CityModel, Long> {
    Optional<CityModel> findByNameAndState(String cityName, StateModel state);

    @Query("SELECT c FROM CityModel c " +
            "JOIN FETCH c.state s " +
            "LEFT JOIN FETCH c.holidays h")
    List<CityModel> findAllWithStateAndHolidays();
}
