package br.com.vaga_ambiental.Vaga.Ambiental.domain.repository;

import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.HolidayModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface HolidayRepository extends JpaRepository<HolidayModel, Long> {
    boolean existsByName(String name);

    @Query("SELECT h FROM HolidayModel h WHERE h.city IS NULL")
    List<HolidayModel> findAllNationalHolidays();
}
