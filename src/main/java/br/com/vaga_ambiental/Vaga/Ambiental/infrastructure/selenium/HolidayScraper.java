package br.com.vaga_ambiental.Vaga.Ambiental.infrastructure.selenium;

import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.City;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.Holiday;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class HolidayScraper {

    private static final String baseUrl = "https://www.feriados.com.br/feriados";
    private static final String year = "2024";

    public HolidayScraper() {
        WebDriverManager.chromedriver().setup();
    }

    public List<Holiday> scrapeHolidays(City city) {
        List<Holiday> holidays = new ArrayList<>();
        WebDriver driver = new ChromeDriver();

        try {
            // Mount URL
            String searchUrl = String.format("%s-%s-%s.php?ano=%s",
                    baseUrl, city.getName().toLowerCase(), city.getState().toLowerCase(), year);

            driver.get(searchUrl);

            // Locate holidays table
            String xpath = String.format("//*[@id=\"Feriados %s 2024\"]/ul", city.getName().toUpperCase());
            WebElement holidayList = driver.findElement(By.xpath(xpath));

            List<WebElement> holidayItems = holidayList.findElements(By.tagName("li"));
            for (WebElement item : holidayItems) {
                // TODO
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return holidays;
    }
}
