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
        StateEntity stateEntity = findOrCreateState(city.getState());
        CityEntity cityEntity = findOrCreateCity(city.getName(), stateEntity);

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

    private CityEntity findOrCreateCity(String name, StateEntity stateEntity) {
        Optional<CityEntity> optionalCity = cityRepository.findByNameAndState(name, stateEntity);
        return optionalCity.orElseGet(() -> {
            CityEntity newCity = new CityEntity();
            newCity.setName(name);
            newCity.setState(stateEntity);
            return cityRepository.save(newCity);
        });
    }

    private StateEntity findOrCreateState(String state) {
        Optional<StateEntity> optionalState = stateRepository.findByAbbreviation(state);
        return optionalState.orElseGet(() -> {
            StateEntity newState = new StateEntity();
            newState.setAbbreviation(state);
            return stateRepository.save(newState);
        });
    }
}
