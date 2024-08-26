package br.com.vaga_ambiental.Vaga.Ambiental.controller;

import br.com.vaga_ambiental.Vaga.Ambiental.domain.dto.HolidayRequestDto;
import br.com.vaga_ambiental.Vaga.Ambiental.services.ApiService;
import br.com.vaga_ambiental.Vaga.Ambiental.services.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/holidays")
public class HolidayController {

    private final HolidayService holidayService;
    private final ApiService apiService;

    @Autowired
    public HolidayController(HolidayService service, ApiService api) {
        this.holidayService = service;
        this.apiService = api;
    }

    @GetMapping("/preview")
    public ResponseEntity<List<HolidayRequestDto>> previewHolidaysPayload() {
        List<HolidayRequestDto> holidayPayload = holidayService.getAllHolidaysForAllCities();
        return ResponseEntity.ok(holidayPayload);
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendHolidaysToApi(@RequestHeader("Authorization") String bearerToken) {
        String token = bearerToken.replace("Bearer ", "");
        List<HolidayRequestDto> holidayPayload = holidayService.getAllHolidaysForAllCities();
        return apiService.sendDataFromDbToApi(holidayPayload, token);
    }
}
