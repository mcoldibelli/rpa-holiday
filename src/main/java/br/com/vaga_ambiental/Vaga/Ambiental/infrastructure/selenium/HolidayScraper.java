package br.com.vaga_ambiental.Vaga.Ambiental.infrastructure.selenium;

import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class HolidayScraper {
    private WebDriver driver;
    private static final String BASE_URL = "https://www.feriados.com.br/feriados";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);

    public HolidayScraper() {
        WebDriverManager.chromedriver().setup();
    }

    private void setupWebDriver() {
        driver = new ChromeDriver();
    }

    private void teardownWebDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    public List<Holiday> scrapeHolidays(City city, String year) {
        List<Holiday> holidays = new ArrayList<>();

        try {
            setupWebDriver();

            // Go to base page
            driver.get(BASE_URL);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Select the state and city
            selectDropdownOptionByVisibleText(wait, "estado", city.getState());
            selectDropdownOptionByVisibleText(wait, "cidade", city.getName());

            // Scrape the holidays
            String xpath = String.format("//*[@id=\"Feriados %s %s\"]/ul", city.getName().toUpperCase(), year);
            WebElement holidayList = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));

            holidays = extractHolidays(holidayList);
            log.info("{} has {} holiday(s) in {}.", city.getName(), holidays.size(), year);

        } catch (Exception e) {
            log.error("Error occurred while scraping holidays for {}: {}", city.getName(), e.getMessage(), e);
        } finally {
            teardownWebDriver();
        }

        return holidays;
    }

    private List<Holiday> extractHolidays(WebElement holidayList) {
        List<Holiday> holidays = new ArrayList<>();
        List<WebElement> holidayItems = holidayList.findElements(By.tagName("li"));

        for (WebElement item : holidayItems) {
            // Is shown in screen as DATE - HOLIDAY_NAME
            String[] parts = item.getText().split("-");

            if (parts.length == 2) {
                String date = parts[0].trim();
                String name = parts[1].trim();

                // Ensure correct date format
                LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);

                // Determine the holiday type
                WebElement divElement = item.findElement(By.tagName("div"));
                String titleAttribute = divElement.getAttribute("title");

                HolidayType holidayType = determineHolidayType(titleAttribute);

                // Create holiday only if holidayType is MUNICIPAL or NACIONAL
                if(holidayType != null) {
                    Holiday holiday = new Holiday();

                    holiday.setName(name);
                    holiday.setDate(localDate);
                    holiday.setType(holidayType);
                    holidays.add(holiday);
                } else {
                    log.warn("Skipping holiday with unknown type: {} on {}", name, date);
                }
            }
        }
        return holidays;
    }

    private void selectDropdownOptionByVisibleText(WebDriverWait wait, String selectElementId, String visibleText) {
        Select dropdown = new Select(
                wait.until(ExpectedConditions
                        .visibilityOfElementLocated(By.id(selectElementId))
                )
        );
        dropdown.selectByVisibleText(visibleText);
    }

    private HolidayType determineHolidayType(String titleAttribute) {
        if (titleAttribute != null) {
            if (titleAttribute.contains("Feriado Nacional")) {
                return HolidayType.NACIONAL;
            } else if (titleAttribute.contains("Municipal")) {
                return HolidayType.MUNICIPAL;
            }
        }
        return null; // Return null if it's neither MUNICIPAL nor NACIONAL
    }
}
