package br.com.vaga_ambiental.Vaga.Ambiental.controller;

import br.com.vaga_ambiental.Vaga.Ambiental.services.ApiService;
import br.com.vaga_ambiental.Vaga.Ambiental.services.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/holidays")
public class HolidayController {

    @Autowired
    private ApiService apiService;

    @Autowired
    private HolidayService holidayService;

    @GetMapping("/preview")
    public ResponseEntity<Map<String, Object>> previewHolidaysPayload() {

        return null;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendHolidaysToApi() {

        return null;
    }
}
