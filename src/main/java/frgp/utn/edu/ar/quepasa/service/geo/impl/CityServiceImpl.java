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

import java.nio.file.AccessDeniedException;
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
     * Busca todas las ciudades que contengan el texto dado en el nombre,
     * activas o no activas, de acuerdo al parámetro dado.
     * La lista se devuelve paginada según el objeto Pageable dado.
     *
     * @param q El texto que se busca en el nombre de las ciudades
     * @param pageable El objeto que contiene la información de la paginación
     * @param active Indica si se quieren obtener solo las ciudades activas o no
     * @return La lista de ciudades paginada
     */
    @Override
    public Page<City> search(String q, Pageable pageable, boolean active) {
        return cityRepository.search(q, pageable, active);
    }

    /**
     * Busca todas los ciudades, activas o no activas, de acuerdo al parámetro dado.
     *
     * @param pageable El objeto que contiene la información de la paginación
     * @param activeOnly Indica si se quieren obtener solo las ciudades activas o no
     * @return La lista de ciudades
     */
    @Override
    public Page<City> getAll(Pageable pageable, boolean activeOnly) {
        if(activeOnly) {
            return cityRepository.findAllActive(pageable);
        }
        return cityRepository.findAll(pageable);
    }

    /**
     * Busca una ciudad por su ID y la devuelve.
     *
     * @param id El ID de la ciudad que se busca
     * @param activeOnly Indica si se quiere obtener solo una ciudad activa o no
     * @return La ciudad encontrada
     * @throws Fail Si la ciudad no es encontrada
     */
    @Override
    public City getById(long id, boolean activeOnly) {
        if(activeOnly) {
            return cityRepository.findByIdActive(id)
                    .orElseThrow(() -> new Fail("City not found", HttpStatus.NOT_FOUND));
        }
        return cityRepository.findById(id)
                .orElseThrow(() -> new Fail("City not found", HttpStatus.NOT_FOUND));
    }

    /**
     * Obtiene una lista de ciudades que pertenecen a un país específico.
     *
     * @param pageable El objeto que contiene la información de la paginación
     * @param iso3 El identificador del país que se busca
     * @return Una lista de ciudades que pertenecen al país buscado
     * @throws Fail Si el país no es encontrado
     */
    @Override
    public Page<City> getByCountry(String iso3, Pageable pageable) {
        countryRepository.findByIso3(iso3)
                .orElseThrow(() -> new Fail("Country not found", HttpStatus.NOT_FOUND));

        return cityRepository.findByCountry(iso3, pageable);
    }

    /**
     * Obtiene una lista de ciudades que pertenecen a una división subnacional específica.
     *
     * @param pageable El objeto que contiene la información de la paginación
     * @param iso3 El identificador de la división que se busca
     * @return Una lista de ciudades que pertenecen a la división buscada
     * @throws Fail Si la división no es encontrada
     */
    @Override
    public Page<City> getBySubnationalDivision(String iso3, Pageable pageable) {
        SubnationalDivision subdivision = subnationalDivisionRepository.findByIso3(iso3)
                .orElseThrow(() -> new Fail("Subdivision not found", HttpStatus.NOT_FOUND));

        return cityRepository.findBySubdivision(subdivision, pageable);
    }

    /**
     * Crea una nueva ciudad.
     *
     * El usuario actual debe ser administrador.
     *
     * @param request La nueva ciudad
     * @return La ciudad creada
     * @throws Fail Si la división subnacional no es encontrada
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
     * Actualiza una ciudad.
     *
     * El usuario actual debe ser administrador
     *
     * @param id El ID de la ciudad
     * @param request La nueva ciudad
     * @return La ciudad actualizada
     * @throws Fail Si la división subnacional no es encontrada
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
     * Elimina una ciudad.
     *
     * El usuario actual debe ser administrador
     *
     * @param id El ID de la ciudad a eliminar
     */
    @Override
    public void delete(long id) {
        City city = getById(id, true);
        city.setActive(false);

        cityRepository.save(city);
    }

}
