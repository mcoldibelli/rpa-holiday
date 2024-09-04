package br.com.vaga_ambiental.Vaga.Ambiental.controller;

import br.com.vaga_ambiental.Vaga.Ambiental.domain.dto.HolidayRequestDto;
import br.com.vaga_ambiental.Vaga.Ambiental.services.ApiService;
import br.com.vaga_ambiental.Vaga.Ambiental.services.HolidayService;
import br.com.vaga_ambiental.Vaga.Ambiental.services.RpaService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/holidays")
public class HolidayController {

  private final HolidayService holidayService;
  private final ApiService apiService;
  private final RpaService rpaService;

  @Autowired
  public HolidayController(HolidayService service, ApiService api, RpaService rpaService) {
    this.holidayService = service;
    this.apiService = api;
    this.rpaService = rpaService;
  }

  @GetMapping("/preview")
  public ResponseEntity<List<HolidayRequestDto>> previewHolidaysPayload() {
    List<HolidayRequestDto> holidayPayload = holidayService.getAllHolidaysForAllCities();
    return ResponseEntity.ok(holidayPayload);
  }

  @PostMapping("/send")
  public ResponseEntity<String> sendHolidaysToApi(
      @RequestHeader("Authorization") String bearerToken) {
    rpaService.runRpaProcess();

    String token = bearerToken.replace("Bearer ", "");
    List<HolidayRequestDto> holidayPayload = holidayService.getAllHolidaysForAllCities();
    return apiService.sendDataFromDbToApi(holidayPayload, token);
  }
}
