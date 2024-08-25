package br.com.vaga_ambiental.Vaga.Ambiental.services;


import br.com.vaga_ambiental.Vaga.Ambiental.domain.dto.CityAndStateDto;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.dto.HolidayDto;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.dto.HolidayRequestDto;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.CityModel;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.HolidayModel;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.StateModel;
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
    public void saveHolidays(CityAndStateDto city, List<HolidayDto> holidays) {
        StateModel state = findOrCreateState(city.getState());
        CityModel cityEntity = findOrCreateCity(city.getCity(), state);

        // Convert and save the HolidayEntities
        for (HolidayDto holiday : holidays) {
            boolean isNationalHoliday = "Nacional".equalsIgnoreCase(holiday.getType().toString());
            boolean existingHoliday = holidayRepository.existsByName(holiday.getName());

            if (!existingHoliday) {
                HolidayModel holidayEntity = new HolidayModel();

                holidayEntity.setDate(holiday.getDate());
                holidayEntity.setName(holiday.getName());
                holidayEntity.setType(holiday.getType());

                // No specific city for national holidays
                if (isNationalHoliday) {
                    holidayEntity.setCity(null);
                } else {
                    holidayEntity.setCity(cityEntity);
                }

                holidayRepository.save(holidayEntity);
            }
        }
    }

    @Transactional
    public List<HolidayRequestDto> getAllHolidaysForAllCities() {
        List<CityModel> cities = cityRepository.findAllWithStateAndHolidays();

        List<HolidayRequestDto> holidayRequests = new ArrayList<>();

        for (CityModel city : cities) {
            List<HolidayDto> feriados = new ArrayList<>();

            for (HolidayModel holiday : city.getHolidays()) {
                HolidayDto holidayDto = new HolidayDto();
                holidayDto.setDate(holiday.getDate());
                holidayDto.setType(holiday.getType());
                holidayDto.setName(holiday.getName());

                feriados.add(holidayDto);
            }

            HolidayRequestDto requestDto = new HolidayRequestDto();
            requestDto.setEstado(city.getState().getAbbreviation());
            requestDto.setCidade(city.getName());
            requestDto.setFeriados(feriados);

            holidayRequests.add(requestDto);
        }

        return holidayRequests;
    }

    private CityModel findOrCreateCity(String name, StateModel state) {
        Optional<CityModel> optionalCity = cityRepository.findByNameAndState(name, state);
        return optionalCity.orElseGet(() -> {
            CityModel newCity = new CityModel();
            newCity.setName(name);
            newCity.setState(state);
            return cityRepository.save(newCity);
        });
    }

    private StateModel findOrCreateState(String state) {
        Optional<StateModel> optionalState = stateRepository.findByAbbreviation(state);
        return optionalState.orElseGet(() -> {
            StateModel newState = new StateModel();
            newState.setAbbreviation(state);
            return stateRepository.save(newState);
        });
    }
}
