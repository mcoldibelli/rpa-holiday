package br.com.vaga_ambiental.Vaga.Ambiental.services;

import br.com.vaga_ambiental.Vaga.Ambiental.domain.dto.CityAndStateDto;
import br.com.vaga_ambiental.Vaga.Ambiental.infrastructure.HolidayScraper;
import br.com.vaga_ambiental.Vaga.Ambiental.infrastructure.excel.ExcelReader;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RpaService {

  private final ExcelReader excelReader;
  private final HolidayScraper holidayScraper;
  private final HolidayService holidayService;

  @Autowired
  public RpaService(ExcelReader excelReader, HolidayScraper holidayScraper,
      HolidayService holidayService) {
    this.excelReader = excelReader;
    this.holidayScraper = holidayScraper;
    this.holidayService = holidayService;
  }

  public void runRpaProcess() {
    log.info("> Starting RPA process:");
    log.info("> Reading cities from EXCEL:");
    List<CityAndStateDto> cities = excelReader.readCitiesFromFile();
    log.info("> {} Cities read: {}", cities.size(), cities);

    for (CityAndStateDto city : cities) {
      log.info("> Scraping holidays for {}/{}", city.getCity(), city.getState());
      try {
        String yearToScrap = "2024";
        
        var holidays = holidayScraper.scrapeHolidays(city, yearToScrap);
        if (!holidays.isEmpty()) {
          log.info(">> Scraped {} holiday(s): {}", holidays.size(), holidays);

          log.info(">> Saving holidays in database.");
          holidayService.saveHolidays(city, holidays);
          log.info(">> Saved with success.");
        } else {
          log.error(">> No holidays found. Possible name mismatch in {}.", city.getCity());
        }
      } catch (Exception e) {
        log.error(">> Error scraping holidays for city {}/{}: {}",
            city.getCity(), city.getState(), e.getMessage());
      }
    }

    log.info("> Finished RPA process.");
  }
}