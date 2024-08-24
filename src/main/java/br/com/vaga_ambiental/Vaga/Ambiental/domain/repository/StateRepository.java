package br.com.vaga_ambiental.Vaga.Ambiental.domain.repository;

import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.City;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.Entity.StateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StateRepository extends JpaRepository<StateEntity, Long> {
    Optional<StateEntity> findByName(String name);
}
