package frgp.utn.edu.ar.quepasa.service.geo.impl;

import frgp.utn.edu.ar.quepasa.data.request.geo.CityRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.geo.City;
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

    /**
     * <b>Devuelve una lista paginada de ciudades según la consulta. </b>
     */
    @Override
    public Page<City> search(String q, Pageable pageable, boolean active) {
        return cityRepository.search(q, pageable, active);
    }

    /**
     * <b>Devuelve una lista con todas las ciudades. </b>
     */
    @Override
    public List<City> getAll(boolean activeOnly) {
        if(activeOnly) {
            return cityRepository.findAllActive();
        }
        return cityRepository.findAll();
    }

    /**
     * <b>Devuelve una ciudad según su ID o, lanza una excepción. </b>
     */
    @Override
    public City getById(long id, boolean activeOnly) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new Fail("City not found", HttpStatus.NOT_FOUND));
    }

    /**
     * <b>Devuelve una lista de ciudades según su país, o lanza una excepción. </b>
     */
    @Override
    public List<City> getByCountry(String iso3) {
        countryRepository.findByIso3(iso3)
                .orElseThrow(() -> new Fail("Country not found", HttpStatus.NOT_FOUND));

        return cityRepository.findByCountry(iso3);
    }

    /**
     * <b>Devuelve una lista de ciudades según su división subnacional, o lanza una excepción. </b>
     */
    @Override
    public List<City> getBySubnationalDivision(String iso3) {
        SubnationalDivision subdivision = subnationalDivisionRepository.findByIso3(iso3)
                .orElseThrow(() -> new Fail("Subdivision not found", HttpStatus.NOT_FOUND));

        return cityRepository.findBySubdivision(subdivision);
    }

    /**
     * <b>Crea una nueva ciudad. </b>
     */
    @Override
    public City create(CityRequest request) {
        City city = new City();
        city.setName(request.getName());
        var subdivision = subnationalDivisionRepository.findByIso3(request.getSubdivision())
                .orElseThrow(() -> new Fail("Subdivision not found", HttpStatus.NOT_FOUND));
        city.setSubdivision(subdivision);

        cityRepository.save(city);
        return city;
    }

    /**
     * <b>Actualiza una ciudad existente. </b>
     */
    @Override
    public City update(long id, CityRequest request) {
        City city = getById(id, true);
        if(request.getName() != null) city.setName(request.getName());
        if(request.getSubdivision() != null) {
            var subdivision = subnationalDivisionRepository.findByIso3(request.getSubdivision())
                    .orElseThrow(() -> new Fail("Subdivision not found", HttpStatus.NOT_FOUND));
            city.setSubdivision(subdivision);
        }

        cityRepository.save(city);
        return city;
    }

    /**
     * <b>Elimina lógicamente una ciudad existente. </b>
     */
    @Override
    public void delete(long id) {
        City city = getById(id, true);
        city.setActive(false);

        cityRepository.save(city);
    }

}
