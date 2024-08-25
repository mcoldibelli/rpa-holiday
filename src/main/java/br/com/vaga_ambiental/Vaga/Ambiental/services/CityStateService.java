package br.com.vaga_ambiental.Vaga.Ambiental.services;

import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.CityModel;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.model.StateModel;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.repository.CityRepository;
import br.com.vaga_ambiental.Vaga.Ambiental.domain.repository.StateRepository;
import org.springframework.stereotype.Service;

@Service
public class CityStateService {

    private final CityRepository cityRepository;
    private final StateRepository stateRepository;

    public CityStateService(CityRepository cityRepository, StateRepository stateRepository) {
        this.cityRepository = cityRepository;
        this.stateRepository = stateRepository;
    }

    public CityModel findOrCreateCity(String name, StateModel state) {
        return cityRepository.findByNameAndState(name, state)
                .orElseGet(() -> {
                    CityModel newCity = new CityModel();
                    newCity.setName(name);
                    newCity.setState(state);
                    return cityRepository.save(newCity);
                });
    }

    public StateModel findOrCreateState(String state) {
        return stateRepository.findByAbbreviation(state)
                .orElseGet(() -> {
                    StateModel newState = new StateModel();
                    newState.setAbbreviation(state);
                    return stateRepository.save(newState);
                });
    }
}

