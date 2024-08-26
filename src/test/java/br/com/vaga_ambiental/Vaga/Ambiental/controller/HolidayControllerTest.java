package br.com.vaga_ambiental.Vaga.Ambiental.controller;

import br.com.vaga_ambiental.Vaga.Ambiental.domain.dto.HolidayRequestDto;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.enums.HolidayType;
import br.com.vaga_ambiental.Vaga.Ambiental.infrastructure.HolidayScraper;
import br.com.vaga_ambiental.Vaga.Ambiental.infrastructure.excel.ExcelReader;
import br.com.vaga_ambiental.Vaga.Ambiental.services.ApiService;
import br.com.vaga_ambiental.Vaga.Ambiental.services.HolidayService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HolidayController.class)
class HolidayControllerTest {

    @MockBean
    private ApiService apiService;

    @MockBean
    private HolidayService holidayService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExcelReader excelReader;

    @MockBean
    private HolidayScraper holidayScraper;

    @Test
    void testPreviewHolidaysPayload() throws Exception {
        List<HolidayRequestDto.FeriadoDto> feriados = new ArrayList<>();
        feriados.add(new HolidayRequestDto.FeriadoDto("2024-01-01", HolidayType.NACIONAL, "Confraternização Universal"));

        List<HolidayRequestDto> mockHolidays = new ArrayList<>();
        mockHolidays.add(new HolidayRequestDto("SP", "Bauru", feriados));

        when(holidayService.getAllHolidaysForAllCities()).thenReturn(mockHolidays);

        mockMvc.perform(get("/api/holidays/preview")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{'estado':'SP','cidade':'Bauru','feriados':[{'data':'2024-01-01','tipo':'NACIONAL','feriado':'Confraternização Universal'}]}]"));
    }

    @Test
    void testSendHolidaysToApi() throws Exception {
        List<HolidayRequestDto.FeriadoDto> feriados = new ArrayList<>();
        feriados.add(new HolidayRequestDto.FeriadoDto("2024-01-01", HolidayType.NACIONAL, "Confraternização Universal"));

        List<HolidayRequestDto> mockHolidays = new ArrayList<>();
        mockHolidays.add(new HolidayRequestDto("SP", "Bauru", feriados));

        String token = "sampleToken";
        String bearerToken = "Bearer " + token;

        when(holidayService.getAllHolidaysForAllCities()).thenReturn(mockHolidays);
        when(apiService.sendDataFromDbToApi(mockHolidays, token))
                .thenReturn(ResponseEntity.ok("Data sent successfully"));

        mockMvc.perform(post("/api/holidays/send")
                        .header("Authorization", bearerToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Data sent successfully"));
    }

}