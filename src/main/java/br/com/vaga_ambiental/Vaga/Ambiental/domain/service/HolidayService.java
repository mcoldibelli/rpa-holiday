package br.com.vaga_ambiental.Vaga.Ambiental.domain.service;


import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.City;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.dto.HolidayRequestDto;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.entity.CityEntity;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.entity.HolidayEntity;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.entity.StateEntity;
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
        for (Holiday holiday : holidays) {
            boolean isNationalHoliday = "Nacional".equalsIgnoreCase(holiday.getType().toString());
            boolean existingHoliday = holidayRepository.existsByName(holiday.getName());

            if (!existingHoliday) {
                HolidayEntity holidayEntity = new HolidayEntity();

                holidayEntity.setDate(holiday.getDate());
                holidayEntity.setName(holiday.getName());
                holidayEntity.setType(holiday.getType());

                // No specific city and state for national holidays
                if (isNationalHoliday) {
                    holidayEntity.setCity(null);
                    holidayEntity.setState(null);
                } else {
                    holidayEntity.setState(stateEntity);
                    holidayEntity.setCity(cityEntity);
                }

                holidayRepository.save(holidayEntity);
            }
        }
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
