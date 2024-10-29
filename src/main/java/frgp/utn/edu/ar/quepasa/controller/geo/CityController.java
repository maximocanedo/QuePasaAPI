package frgp.utn.edu.ar.quepasa.controller.geo;

import frgp.utn.edu.ar.quepasa.data.request.geo.CityRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.exception.ValidationError;
import frgp.utn.edu.ar.quepasa.service.geo.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
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
     * Crea una ciudad nueva.
     *
     * @param city Detalles de la ciudad a crear.
     * @return Entidad de respuesta con los detalles de la ciudad creada.
     */
    @PostMapping
    public ResponseEntity<?> createCity(@RequestBody CityRequest city) {
        return ResponseEntity.ok(cityService.create(city));
    }

    /**
     * Obtiene una lista paginada de ciudades activas o inactivas, según sea especificado.
     *
     * @param page Número de páginas a obtener. Comienza en 0.
     * @param size Tamaño de cada página. Comienza en 10.
     * @param activeOnly Si se desean obtener solo las ciudades activas. Valor predeterminado es true.
     * @return Entidad de respuesta con una lista paginada de ciudades encontradas.
     */
    @GetMapping("/all")
    public ResponseEntity<?> getCities(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size, @RequestParam(defaultValue="true") boolean activeOnly) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(cityService.getAll(pageable, activeOnly));
    }

    /**
     * Obtiene una lista paginada de ciudades que coinciden con los criterios de búsqueda especificados.
     *
     * @param q Parámetro de búsqueda que se usará para filtrar las ciudades.
     * @param sort Parámetro de ordenamiento para las ciudades, con un valor predeterminado de "name,asc".
     * @param page Número de páginas a obtener. Comienza en 0.
     * @param size Tamaño de cada página. Comienza en 10.
     * @param active Si se desean obtener solo las ciudades activas. Valor predeterminado es true.
     * @return Entidad de respuesta con una lista paginada de ciudades filtradas.
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
     * Obtiene una ciudad según su ID.
     *
     * @param id ID de la ciudad a buscar.
     * @return Entidad de respuesta que contiene la ciudad buscada.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCityById(@PathVariable long id, @RequestParam(defaultValue="true") boolean active) {
        return ResponseEntity.ok(cityService.getById(id, active));
    }

    /**
     * Obtiene ciudades que pertenecen a un país.
     *
     * @param iso3 Identificador del país.
     * @param page Número de páginas a obtener. Comienza en 0.
     * @param size Tamaño de cada página. Comienza en 10.
     * @return Entidad de respuesta que contiene la lista paginada de ciudades del país especificado.
     */
    @GetMapping("/country/{iso3}")
    public ResponseEntity<?> getCitiesByCountry(@PathVariable String iso3, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(cityService.getByCountry(iso3, pageable));
    }

    /**
     * Obtiene ciudades que pertenecen a una división subnacional.
     *
     * @param iso3 Identificador de la división.
     * @param page Número de páginas a obtener. Comienza en 0.
     * @param size Tamaño de cada página. Comienza en 10.
     * @return Entidad de respuesta que contiene la lista paginada de ciudades de la subdivisión especificada.
     */
    @GetMapping("/subdivision/{iso3}")
    public ResponseEntity<?> getCitiesBySubnationalDivision(@PathVariable String iso3, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(cityService.getBySubnationalDivision(iso3, pageable));
    }

    /**
     * Edita una ciudad.
     * @param id ID de la ciudad a editar.
     * @param city Nueva ciudad.
     * @return Entidad de respuesta que contiene la ciudad editada.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateCity(@PathVariable long id, @RequestBody CityRequest city) {
        return ResponseEntity.ok(cityService.update(id, city));
    }

    /**
     * Elimina una ciudad.
     * @param id ID de la ciudad a eliminar.
     * @return Entidad de respuesta de tipo 204 (No Content).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCity(@PathVariable long id) {
        cityService.delete(id);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

}
