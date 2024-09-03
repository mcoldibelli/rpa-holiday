package br.com.vaga_ambiental.Vaga.Ambiental.domain.repository;

import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.StateModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StateRepository extends JpaRepository<StateModel, Long> {

  Optional<StateModel> findByAbbreviation(String name);
}
