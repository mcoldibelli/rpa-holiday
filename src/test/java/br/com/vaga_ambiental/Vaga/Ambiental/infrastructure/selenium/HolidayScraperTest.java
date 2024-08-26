package br.com.vaga_ambiental.Vaga.Ambiental.infrastructure.selenium;

import br.com.vaga_ambiental.Vaga.Ambiental.domain.dto.HolidayDto;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.enums.HolidayType;
import br.com.vaga_ambiental.Vaga.Ambiental.infrastructure.HolidayScraper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HolidayScraperTest {

    @InjectMocks
    private HolidayScraper holidayScraper;

    @BeforeEach
    void setUp() {
        holidayScraper = new HolidayScraper();
    }

    @Test
    void testExtractHolidays() {
        WebElement mockHolidayList = mock(WebElement.class);

        WebElement mockHolidayItem = mock(WebElement.class);
        when(mockHolidayList.findElements(By.tagName("li"))).thenReturn(List.of(mockHolidayItem));
        when(mockHolidayItem.getText()).thenReturn("25/12/2024 - Natal");

        WebElement mockDivElement = mock(WebElement.class);
        when(mockHolidayItem.findElement(By.tagName("div"))).thenReturn(mockDivElement);
        when(mockDivElement.getAttribute("title")).thenReturn("Feriado Nacional");

        List<HolidayDto> holidays = holidayScraper.extractHolidays(mockHolidayList);
        assertEquals(1, holidays.size());
        assertEquals(HolidayType.NACIONAL, holidays.get(0).getType());
    }
}