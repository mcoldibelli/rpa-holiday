package br.com.vaga_ambiental.Vaga.Ambiental;

import br.com.vaga_ambiental.Vaga.Ambiental.domain.dto.CityAndStateDto;
import br.com.vaga_ambiental.Vaga.Ambiental.services.HolidayService;
import br.com.vaga_ambiental.Vaga.Ambiental.infrastructure.excel.ExcelReader;
import br.com.vaga_ambiental.Vaga.Ambiental.infrastructure.selenium.HolidayScraper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@Slf4j
@SpringBootApplication
public class VagaAmbientalApplication {

	public static void main(String[] args) {
		SpringApplication.run(VagaAmbientalApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(ExcelReader excelReader, HolidayScraper holidayScraper, HolidayService holidayService) {
		return args -> {
			log.info("> Starting RPA process:");
			log.info("> Reading cities from EXCEL:");
			List<CityAndStateDto> cities = excelReader.readCitiesFromFile();
			log.info("> {} Cities read: {}", cities.size(), cities);

			for (CityAndStateDto city : cities) {
				log.info("> Scraping holidays for {}/{}", city.getCity(), city.getState());
				try {
					var holidays = holidayScraper.scrapeHolidays(city, "2024");
					if(!holidays.isEmpty()) {
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
		};
	}
}
