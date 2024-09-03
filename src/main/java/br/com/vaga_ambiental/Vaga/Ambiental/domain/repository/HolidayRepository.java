package br.com.vaga_ambiental.Vaga.Ambiental.domain.repository;

import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.HolidayModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HolidayRepository extends JpaRepository<HolidayModel, Long> {

  boolean existsByName(String name);

  @Query("SELECT h FROM HolidayModel h WHERE h.city IS NULL")
  List<HolidayModel> findAllNationalHolidays();
}
