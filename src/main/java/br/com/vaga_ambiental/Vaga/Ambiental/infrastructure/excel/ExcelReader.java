package br.com.vaga_ambiental.Vaga.Ambiental.infrastructure.excel;

import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.City;
import br.com.vaga_ambiental.Vaga.Ambiental.exceptions.ExcelProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class ExcelReader {

    protected String path = "src/main/java/br/com/vaga_ambiental/Vaga/Ambiental/infrastructure/excel/Projeto vaga Ambiental.xlsx";

    public List<City> readCitiesFromFile() {
        List<City> cities = new ArrayList<>();

        try(FileInputStream fis = new FileInputStream(path);
            Workbook workbook = WorkbookFactory.create(fis)) {

            // Content is only in first sheet
            Sheet sheet = workbook.getSheetAt(0);

            for(int index = 1; index <= sheet.getLastRowNum(); index++) {
                Row row = sheet.getRow(index);

                if(row != null) {
                    String state = row.getCell(0).getStringCellValue(); // State at column A
                    String cityName = row.getCell(1).getStringCellValue();  // City at column B

                    if(state != null && cityName != null) {
                        cities.add(new City(cityName, state));
                    }
                }
            }

        } catch (IOException e) {
            log.error("Error reading the excel file: ", e);
            throw new ExcelProcessingException("Failed to process Excel file: ", e);
        }

        return cities;
    }

}
