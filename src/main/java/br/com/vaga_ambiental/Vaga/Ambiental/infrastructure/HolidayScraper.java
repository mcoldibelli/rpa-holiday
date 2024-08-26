package br.com.vaga_ambiental.Vaga.Ambiental.infrastructure;

import br.com.vaga_ambiental.Vaga.Ambiental.domain.dto.CityAndStateDto;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.dto.HolidayDto;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.enums.HolidayType;
import br.com.vaga_ambiental.Vaga.Ambiental.utils.DateUtils;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class HolidayScraper {
    private WebDriver driver;
    
    @Value("${web.feriados.uri}")
    private String baseUrl;

    public HolidayScraper() {
        WebDriverManager.chromedriver().setup();
    }

    private void setupWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
    }

    private void teardownWebDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    public List<HolidayDto> scrapeHolidays(CityAndStateDto cityAndState, String year) {
        List<HolidayDto> holidays = new ArrayList<>();

        try {
            setupWebDriver();

            // Go to base page
            driver.get(baseUrl);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Select the state and city
            selectDropdownOptionByVisibleText(wait, "estado", cityAndState.getState());
            selectDropdownOptionByVisibleText(wait, "cidade", cityAndState.getCity());

            // Scrape the holidays
            String xpath = String.format("//*[@id=\"Feriados %s %s\"]/ul", cityAndState.getCity().toUpperCase(), year);
            WebElement holidayList = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));

            holidays = extractHolidays(holidayList);

        } catch (Exception e) {
            log.error(">>> Error occurred while scraping holidays for {}/{}\n: {}",
                    cityAndState.getCity(),cityAndState.getState(), e.getMessage());
        } finally {
            teardownWebDriver();
        }

        return holidays;
    }

    public List<HolidayDto> extractHolidays(WebElement holidayList) {
        List<HolidayDto> holidays = new ArrayList<>();
        List<WebElement> holidayItems = holidayList.findElements(By.tagName("li"));
        int skippedHolidaysCounter = 0;

        for (WebElement item : holidayItems) {
            // Is shown in screen as DATE - HOLIDAY_NAME
            String[] parts = item.getText().split("-");

            if (parts.length == 2) {
                String date = parts[0].trim();
                String name = parts[1].trim();

                // Ensure correct date format
                LocalDate localDate = DateUtils.parse(date);

                // Determine the holiday type
                WebElement divElement = item.findElement(By.tagName("div"));
                String titleAttribute = divElement.getAttribute("title");

                HolidayType holidayType = determineHolidayType(titleAttribute);

                // Create holiday only if holidayType is MUNICIPAL or NACIONAL
                if(holidayType != null) {
                    HolidayDto holiday = new HolidayDto();

                    holiday.setName(name);
                    holiday.setDate(localDate);
                    holiday.setType(holidayType);
                    holidays.add(holiday);
                } else {
                    skippedHolidaysCounter++;
                }
            }
        }
        if (skippedHolidaysCounter > 0) {
            log.warn(">> Skipped {} holiday(s) as they are not MUNICIPAL | NACIONAL.", skippedHolidaysCounter);
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
