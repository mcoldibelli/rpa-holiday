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
import java.util.stream.Collectors;


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

        holidays.stream()
                .filter(holiday -> !holidayRepository.existsByName(holiday.getName()))
                .map(holiday -> buildHolidayModel(holiday, cityEntity))
                .forEach(holidayRepository::save);
    }

    private HolidayModel buildHolidayModel(HolidayDto holiday, CityModel city) {
        HolidayModel holidayModel = new HolidayModel();
        holidayModel.setDate(holiday.getDate());
        holidayModel.setName(holiday.getName());
        holidayModel.setType(holiday.getType());

        if (!"NACIONAL".equalsIgnoreCase(holiday.getType().toString())) {
            holidayModel.setCity(city);
        }

        return holidayModel;
    }

    @Transactional
    public List<HolidayRequestDto> getAllHolidaysForAllCities() {
        List<CityModel> cities = cityRepository.findAllWithStateAndHolidays();
        List<HolidayModel> nationalHolidays = holidayRepository.findAllNationalHolidays();

        return cities.stream()
                .map(city -> buildHolidayRequestDto(city, nationalHolidays))
                .collect(Collectors.toList());
    }

    private HolidayRequestDto buildHolidayRequestDto(CityModel city, List<HolidayModel> nationalHolidays) {
        List<HolidayRequestDto.FeriadoDto> feriados = new ArrayList<>();

        feriados.addAll(city.getHolidays().stream()
                .map(this::mapToFeriadoDto)
                .toList());

        feriados.addAll(nationalHolidays.stream()
                .map(this::mapToFeriadoDto)
                .toList());

        return new HolidayRequestDto(city.getState().getAbbreviation(), city.getName(), feriados);
    }

    private HolidayRequestDto.FeriadoDto mapToFeriadoDto(HolidayModel holiday) {
        return new HolidayRequestDto.FeriadoDto(holiday.getDate(), holiday.getType(), holiday.getName());
    }
}
