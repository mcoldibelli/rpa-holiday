package br.com.vaga_ambiental.Vaga.Ambiental.infrastructure.excel;

import br.com.vaga_ambiental.Vaga.Ambiental.domain.dto.CityAndStateDto;
import br.com.vaga_ambiental.Vaga.Ambiental.exceptions.ExcelProcessingException;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExcelReaderTest {

    @Mock
    private Workbook workbook;

    @Mock
    private Sheet sheet;

    @InjectMocks
    private ExcelReader excelReader;

    @BeforeEach
    void setUp() {
        String excelFilePath = "src/test/resources/teste_ambiental.xlsx";
        ReflectionTestUtils.setField(excelReader, "path", excelFilePath);
    }

    @Test
    void testReadCitiesFromFile_contentType() {
        setupSheetWithRows(1);

        try (MockedStatic<WorkbookFactory> workbookFactoryMockedStatic = mockStatic(WorkbookFactory.class)) {
            workbookFactoryMockedStatic.when(() -> WorkbookFactory.create(any(FileInputStream.class)))
                    .thenReturn(workbook);

            List<CityAndStateDto> cities = excelReader.readCitiesFromFile();

            assertEquals(1, cities.size(), "Expected exactly one city-state pair");
            verifyCityAndState(cities.get(0));
        }
    }

    @Test
    void testReadCitiesFromFile_contentSize() {
        setupSheetWithRows(3);

        try (MockedStatic<WorkbookFactory> workbookFactoryMockedStatic = mockStatic(WorkbookFactory.class)) {
            workbookFactoryMockedStatic.when(() -> WorkbookFactory.create(any(FileInputStream.class)))
                    .thenReturn(workbook);

            List<CityAndStateDto> cities = excelReader.readCitiesFromFile();

            assertEquals(3, cities.size(), "Expected three city-state pairs");
            cities.forEach(this::verifyCityAndState);
        }
    }

    @Test
    void testReadCitiesFromFile_ioException() {
        try (MockedStatic<WorkbookFactory> workbookFactoryMockedStatic = mockStatic(WorkbookFactory.class)) {
            workbookFactoryMockedStatic
                    .when(() -> WorkbookFactory.create(any(FileInputStream.class)))
                    .thenThrow(new IOException());

            assertThrows(ExcelProcessingException.class, () -> excelReader.readCitiesFromFile());
        }
    }

    private void setupSheetWithRows(int rowCount) {
        when(workbook.getSheetAt(0)).thenReturn(sheet);
        when(sheet.getLastRowNum()).thenReturn(rowCount);

        for (int i = 1; i <= rowCount; i++) {
            Row row = mock(Row.class);
            when(sheet.getRow(i)).thenReturn(row);

            // Ensure these stubbings are completed without issue
            Cell stateCell = mockCell("SP");
            Cell cityCell = mockCell("Bauru");

            when(row.getCell(0)).thenReturn(stateCell);
            when(row.getCell(1)).thenReturn(cityCell);
        }
    }

    private Cell mockCell(String value) {
        Cell cell = mock(Cell.class);
        when(cell.getStringCellValue()).thenReturn(value);
        return cell;
    }

    private void verifyCityAndState(CityAndStateDto cityAndState) {
        assertEquals("SP", cityAndState.getState(), "State should match expected value");
        assertEquals("Bauru", cityAndState.getCity(), "City should match expected value");
    }
}
