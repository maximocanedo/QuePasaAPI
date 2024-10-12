package frgp.utn.edu.ar.quepasa.service.geo;

import frgp.utn.edu.ar.quepasa.model.geo.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CountryService {

    Country save(Country country);
    List<Country> saveAll(List<Country> countries);
    Page<Country> search(String q, Pageable pageable);
    Optional<Country> getCountry(String iso3);
    void delete(Country country);
    Optional<Country> update(Country country);

}
