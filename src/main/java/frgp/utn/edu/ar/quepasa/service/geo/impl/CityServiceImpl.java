package frgp.utn.edu.ar.quepasa.service.geo.impl;

import frgp.utn.edu.ar.quepasa.data.request.geo.CityUpdateRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.geo.City;
import frgp.utn.edu.ar.quepasa.model.geo.Country;
import frgp.utn.edu.ar.quepasa.model.geo.SubnationalDivision;
import frgp.utn.edu.ar.quepasa.repository.geo.CityRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.CountryRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.SubnationalDivisionRepository;
import frgp.utn.edu.ar.quepasa.service.geo.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityServiceImpl implements CityService {

    private CityRepository cityRepository;
    private CountryRepository countryRepository;
    private SubnationalDivisionRepository subnationalDivisionRepository;

    @Autowired @Lazy
    public void setCityRepository(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Autowired @Lazy
    public void setCountryRepository(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Autowired @Lazy
    public void setSubnationalDivisionRepository(SubnationalDivisionRepository subnationalDivisionRepository) {
        this.subnationalDivisionRepository = subnationalDivisionRepository;
    }

    @Override
    public Page<City> search(String q, Pageable pageable, boolean active) {
        return cityRepository.search(q, pageable, active);
    }

    @Override
    public List<City> getAll(boolean activeOnly) {
        if(activeOnly) {
            return cityRepository.findAllActive();
        }
        return cityRepository.findAll();
    }

    @Override
    public City getById(long id, boolean activeOnly) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new Fail("City not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public List<City> getByCountry(String iso3) {
        Country country = countryRepository.findByIso3(iso3)
                .orElseThrow(() -> new Fail("Country not found", HttpStatus.NOT_FOUND));

        return cityRepository.findByCountry(iso3);
    }

    @Override
    public List<City> getBySubnationalDivision(String iso3) {
        SubnationalDivision subdivision = subnationalDivisionRepository.findByIso3(iso3)
                .orElseThrow(() -> new Fail("Subdivision not found", HttpStatus.NOT_FOUND));

        return cityRepository.findBySubdivision(subdivision);
    }

    @Override
    public City create(CityUpdateRequest request) {
        City city = new City();

        return city;
    }

    @Override
    public City update(CityUpdateRequest request) {
        // TODO: Not implemented yet
        return null;
    }

    @Override
    public void delete(long id) {
        // TODO: Not implemented yet
    }

}
