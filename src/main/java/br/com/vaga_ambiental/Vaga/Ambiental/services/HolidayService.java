package br.com.vaga_ambiental.Vaga.Ambiental.services;


import br.com.vaga_ambiental.Vaga.Ambiental.domain.dto.CityAndStateDto;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.dto.HolidayDto;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.dto.HolidayRequestDto;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.CityModel;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.HolidayModel;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.StateModel;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.repository.CityRepository;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.repository.HolidayRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private final CityStateService cityStateService;
    private final CityRepository cityRepository;


    public HolidayService(HolidayRepository holidayRepository, CityStateService cityStateService, CityRepository cityRepository) {
        this.holidayRepository = holidayRepository;
        this.cityStateService = cityStateService;
        this.cityRepository = cityRepository;
    }

    @Transactional
    public void saveHolidays(CityAndStateDto city, List<HolidayDto> holidays) {
        StateModel state = cityStateService.findOrCreateState(city.getState());
        CityModel cityEntity = cityStateService.findOrCreateCity(city.getCity(), state);

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
        List<HolidayModel> nationalHolidays = holidayRepository.findAllNationalHolidays();

        List<HolidayRequestDto> holidayRequests = new ArrayList<>();

        for (CityModel city : cities) {
            List<HolidayRequestDto.FeriadoDto> feriados = new ArrayList<>();

            // Add city holidays
            for (HolidayModel holiday : city.getHolidays()) {
                HolidayRequestDto.FeriadoDto feriadoDto = new HolidayRequestDto.FeriadoDto();
                feriadoDto.setData(holiday.getDate());
                feriadoDto.setTipo(holiday.getType());
                feriadoDto.setFeriado(holiday.getName());

                feriados.add(feriadoDto);
            }

            HolidayRequestDto requestDto = new HolidayRequestDto();
            requestDto.setEstado(city.getState().getAbbreviation());
            requestDto.setCidade(city.getName());
            requestDto.setFeriados(feriados);

            holidayRequests.add(requestDto);
        }

        return holidayRequests;
    }
}
