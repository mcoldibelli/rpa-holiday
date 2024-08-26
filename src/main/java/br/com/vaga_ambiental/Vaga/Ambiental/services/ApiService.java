package br.com.vaga_ambiental.Vaga.Ambiental.services;

import br.com.vaga_ambiental.Vaga.Ambiental.domain.dto.HolidayRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.StringJoiner;

@Service
public class ApiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public ApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<String> sendDataFromDbToApi(List<HolidayRequestDto> requestBody, String token) {
        String uri = "https://spprev.ambientalqvt.com.br/api/dinamico/avaliacao-vaga/registrar-feriados";
        HttpHeaders headers = createHeaders(token);

        StringJoiner successMessages = new StringJoiner("\n");
        StringJoiner errorMessages = new StringJoiner("\n");

        requestBody.forEach(holidayRequest -> processHolidayRequest(uri, headers, holidayRequest, successMessages, errorMessages));

        return generateFinalResponse(successMessages, errorMessages);
    }

    private void processHolidayRequest(String uri, HttpHeaders headers, HolidayRequestDto holidayRequest, StringJoiner successMessages, StringJoiner errorMessages) {
        try {
            String jsonBody = objectMapper.writeValueAsString(holidayRequest);
            System.out.println("JSON Sent: " + jsonBody);

            ResponseEntity<String> response = sendPostRequest(uri, headers, jsonBody);

            if (response.getStatusCode().is2xxSuccessful()) {
                successMessages.add("Success for: " + holidayRequest.getCidade());
            } else {
                addErrorMessage(errorMessages, holidayRequest, response.getStatusCode().toString(), response.getBody());
            }
        } catch (HttpStatusCodeException e) {
            addErrorMessage(errorMessages, holidayRequest, e.getStatusCode().toString(), e.getResponseBodyAsString());
        } catch (Exception e) {
            errorMessages.add("Unexpected error for: " + holidayRequest.getCidade() + " - " + e.getMessage());
        }
    }

    private void addErrorMessage(StringJoiner errorMessages, HolidayRequestDto holidayRequest, String status, String body) {
        errorMessages.add("Error for: " + holidayRequest.getCidade() + " - Status: " + status + " Body: " + body);
    }

    private HttpHeaders createHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("Content-Type", "application/json");
        return headers;
    }

    private ResponseEntity<String> sendPostRequest(String uri, HttpHeaders headers, String jsonBody) {
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
        return restTemplate.exchange(uri, HttpMethod.POST, requestEntity, String.class);
    }

    private ResponseEntity<String> generateFinalResponse(StringJoiner successMessages, StringJoiner errorMessages) {
        if (errorMessages.length() > 0) {
            return ResponseEntity.badRequest().body(errorMessages.toString());
        } else {
            return ResponseEntity.ok(successMessages.toString());
        }
    }
}
