package frgp.utn.edu.ar.quepasa.service.geo;

import frgp.utn.edu.ar.quepasa.data.request.geo.SubnationalDivisionUpdateRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.geo.SubnationalDivision;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubnationalDivisionService {

    SubnationalDivision save(SubnationalDivision subnationalDivision);

    List<SubnationalDivision> listFrom(String countryCode);

    Page<SubnationalDivision> pageFrom(String countryCode, Pageable pageable);

    SubnationalDivision getById(String id) throws Fail;

    Optional<SubnationalDivision> findById(String id);

    SubnationalDivision update(SubnationalDivisionUpdateRequest request, String iso3);

    void delete(String iso3);

}
