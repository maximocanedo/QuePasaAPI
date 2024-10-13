package frgp.utn.edu.ar.quepasa.controller.geo;

import frgp.utn.edu.ar.quepasa.model.geo.Country;
import frgp.utn.edu.ar.quepasa.model.geo.SubnationalDivision;
import frgp.utn.edu.ar.quepasa.service.geo.CountryService;
import frgp.utn.edu.ar.quepasa.service.geo.SubnationalDivisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/countries")
public class CountryController {

    private CountryService countryService;
    private SubnationalDivisionService subnationalDivisionService;

    @Lazy @Autowired
    public void setCountryService(CountryService countryService) {
        this.countryService = countryService;
    }

    @Lazy @Autowired
    public void setSubnationalDivisionService(SubnationalDivisionService subnationalDivisionService) {
        this.subnationalDivisionService = subnationalDivisionService;
    }

    @GetMapping("/{iso}")
    public ResponseEntity<Optional<Country>> getCountry(@PathVariable String iso) {
        Optional<Country> country = countryService.getCountry(iso);
        if (country.isPresent()) {
            return ResponseEntity.ok(country);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{iso}/states")
    public ResponseEntity<List<SubnationalDivision>> getStates(@PathVariable String iso) {
        var list = subnationalDivisionService.listFrom(iso);
        return ResponseEntity.ok(list);
    }

    @GetMapping
    public ResponseEntity<Page<Country>> getAllCountries(String q, Pageable pageable) {
        Page<Country> countries = countryService.search(q, pageable);
        return ResponseEntity.ok(countries);
    }

}
