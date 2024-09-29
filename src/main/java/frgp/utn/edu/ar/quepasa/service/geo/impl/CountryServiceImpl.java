package frgp.utn.edu.ar.quepasa.service.geo.impl;

import frgp.utn.edu.ar.quepasa.model.geo.Country;
import frgp.utn.edu.ar.quepasa.repository.geo.CountryRepository;
import frgp.utn.edu.ar.quepasa.service.geo.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CountryServiceImpl implements CountryService {

    @Autowired
    @Lazy
    private CountryRepository countryRepository;

    @Override
    public Country save(Country country) {
        return countryRepository.save(country);
    }

    @Override
    public List<Country> saveAll(List<Country> countries) {
        return countryRepository.saveAll(countries);
    }

    @Override
    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    @Override
    public Optional<Country> getCountry(String iso3) {
        return countryRepository.findByIso3(iso3);
    }

    @Override
    public void delete(Country country) {
        countryRepository.delete(country);
    }

    @Override
    public Optional<Country> update(Country country) {
        Optional<Country> oc = getCountry(country.getIso3());
        if(oc.isEmpty()) return oc;
        Country old = oc.get();
        if(country.getLabel() != null) old.setLabel(country.getLabel());
        countryRepository.save(old);
        return Optional.of(old);
    }
}
