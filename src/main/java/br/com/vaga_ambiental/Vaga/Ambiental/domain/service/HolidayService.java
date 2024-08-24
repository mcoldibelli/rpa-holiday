package br.com.vaga_ambiental.Vaga.Ambiental.domain.service;


import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.City;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.Entity.CityEntity;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.Entity.HolidayEntity;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.Entity.StateEntity;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.Holiday;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.repository.CityRepository;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.repository.HolidayRepository;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.repository.StateRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private final CityRepository cityRepository;
    private final StateRepository stateRepository;

    public HolidayService(HolidayRepository holidayRepository, CityRepository cityRepository, StateRepository stateRepository) {
        this.holidayRepository = holidayRepository;
        this.cityRepository = cityRepository;
        this.stateRepository = stateRepository;
    }

    @Transactional
    public void saveHolidays(City city, List<Holiday> holidays) {
        // Find or create the StateEntity
        Optional<StateEntity> optionalState = stateRepository.findByName(city.getState());
        StateEntity stateEntity = optionalState.orElseGet(() -> {
            StateEntity newState = new StateEntity();
            newState.setName(city.getState());
            return stateRepository.save(newState);
        });

        // Find or create the CityEntity
        Optional<CityEntity> optionalCity = cityRepository.findByNameAndState(city.getName(), stateEntity);
        CityEntity cityEntity = optionalCity.orElseGet(() -> {
            CityEntity newCity = new CityEntity();
            newCity.setName(city.getName());
            newCity.setState(stateEntity);
            return cityRepository.save(newCity);
        });

        // Convert and save the HolidayEntities
        List<HolidayEntity> holidayEntities = new ArrayList<>();
        for (Holiday holiday : holidays) {
            HolidayEntity holidayEntity = new HolidayEntity();
            holidayEntity.setDate(holiday.getDate());
            holidayEntity.setName(holiday.getName());
            holidayEntity.setType(holiday.getType());
            holidayEntity.setCity(cityEntity);
            holidayEntities.add(holidayEntity);
        }

        holidayRepository.saveAll(holidayEntities);
    }
}
