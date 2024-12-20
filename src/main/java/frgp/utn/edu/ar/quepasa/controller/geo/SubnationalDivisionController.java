package frgp.utn.edu.ar.quepasa.controller.geo;

import frgp.utn.edu.ar.quepasa.data.ResponseError;
import frgp.utn.edu.ar.quepasa.data.request.geo.SubnationalDivisionUpdateRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import quepasa.api.exceptions.ValidationError;
import frgp.utn.edu.ar.quepasa.model.geo.SubnationalDivision;
import frgp.utn.edu.ar.quepasa.service.geo.SubnationalDivisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/states")
public class SubnationalDivisionController {

    private final SubnationalDivisionService subnationalDivisionService;

    @Autowired
    public SubnationalDivisionController(SubnationalDivisionService subnationalDivisionService) {
        this.subnationalDivisionService = subnationalDivisionService;
    }

    @PostMapping
    public ResponseEntity<SubnationalDivision> create(@RequestBody SubnationalDivision subnationalDivision) {
        return ResponseEntity.status(201).body(subnationalDivisionService.save(subnationalDivision));
    }

    @GetMapping("/{iso}")
    public ResponseEntity<SubnationalDivision> findByIso(@PathVariable String iso) {
        return ResponseEntity.ok(subnationalDivisionService.getById(iso));
    }

    @DeleteMapping("/{iso}")
    public ResponseEntity<?> deleteByIso(@PathVariable String iso) {
        subnationalDivisionService.delete(iso);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{iso}")
    public ResponseEntity<SubnationalDivision> update(@PathVariable String iso, @RequestBody SubnationalDivisionUpdateRequest request) {
        var result = subnationalDivisionService.update(request, iso);
        return ResponseEntity.status(200).body(result);
    }

}
