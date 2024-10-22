package frgp.utn.edu.ar.quepasa.controller.geo;

import frgp.utn.edu.ar.quepasa.data.request.geo.CityUpdateRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.service.geo.CityService;
import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final CityService cityService;

    @Autowired
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    /**
     * <b>Crea una nueva ciudad. </b>
     */
    @PostMapping
    public ResponseEntity<?> createCity(@RequestBody CityUpdateRequest city) {
        return ResponseEntity.ok(cityService.create(city));
    }

    /**
     * <b>Devuelve una lista con todas las ciudades. </b>
     */
    @GetMapping("/all")
    public ResponseEntity<?> getCities(@RequestParam(defaultValue="true") boolean activeOnly) {
        return ResponseEntity.ok(cityService.getAll(activeOnly));
    }

    /**
     * <b>Devuelve una lista paginada de ciudades según la consulta. </b>
     */
    @GetMapping("/search")
    public ResponseEntity<?> getCities(@RequestParam(defaultValue="") String q, @RequestParam(defaultValue="name,asc") String sort, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size, @RequestParam(defaultValue="true") boolean active) {
        Sort.Direction direction = Sort.Direction.ASC;
        if(sort.contains("desc")) {
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort.split(",")[0]));
        return ResponseEntity.ok(cityService.search(q, pageable, active));
    }

    /**
     * <b>Devuelve una ciudad según su ID. </b>
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCityById(@PathVariable long id, @RequestParam(defaultValue="true") boolean active) {
        return ResponseEntity.ok(cityService.getById(id, active));
    }

    /**
     * <b>Devuelve una lista de ciudades según su país. </b>
     */
    @GetMapping("/country/{iso3}")
    public ResponseEntity<?> getCitiesByCountry(@PathVariable String iso3) {
        return ResponseEntity.ok(cityService.getByCountry(iso3));
    }

    /**
     * <b>Devuelve una lista de ciudades según su división subnacional. </b>
     */
    @GetMapping("/subdivision/{iso3}")
    public ResponseEntity<?> getCitiesBySubnationalDivision(@PathVariable String iso3) {
        return ResponseEntity.ok(cityService.getBySubnationalDivision(iso3));
    }

    /**
     * <b>Actualiza una ciudad existente. </b>
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateCity(@PathVariable long id, @RequestBody CityUpdateRequest city) {
        return ResponseEntity.ok(cityService.update(id, city));
    }

    /**
     * <b>Elimina lógicamente una ciudad existente. </b>
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCity(@PathVariable long id) {
        cityService.delete(id);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(ValidatorBuilder.ValidationError.class)
    public ResponseEntity<ValidatorBuilder.ValidationError> handleValidationError(ValidatorBuilder.ValidationError e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<NoSuchElementException> handleNoSuchElement(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex);
    }

    @ExceptionHandler(Fail.class)
    public ResponseEntity<String> handleFail(Fail ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getStatus());
    }

}
