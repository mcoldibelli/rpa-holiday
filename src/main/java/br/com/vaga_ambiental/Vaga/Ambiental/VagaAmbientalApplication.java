package br.com.vaga_ambiental.Vaga.Ambiental;

import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.City;
import br.com.vaga_ambiental.Vaga.Ambiental.infrastructure.excel.ExcelReader;
import br.com.vaga_ambiental.Vaga.Ambiental.infrastructure.selenium.HolidayScraper;
import lombok.Value;
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
	public CommandLineRunner run(ExcelReader excelReader, HolidayScraper holidayScraper) {
		return args -> {
			List<City> cities = excelReader.readCitiesFromFile();
			log.info("Reading cities from EXCEL: {}", cities);

			for (City city : cities) {
				try {
					var holidays = holidayScraper.scrapeHolidays(city, "2024");
					log.info("Scraped {} holiday(s) for city {}/{}: {}",
							holidays.size(), city.getName(), city.getState(), holidays);
				} catch (Exception e) {
					log.error("Error scraping holidays for city {}/{}: {}",
							city.getName(), city.getState(), e.getMessage(), e);
				}
			}
		};
	}
}
