package frgp.utn.edu.ar.quepasa.service.geo.impl;

import frgp.utn.edu.ar.quepasa.data.request.geo.SubnationalDivisionUpdateRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.geo.Country;
import frgp.utn.edu.ar.quepasa.model.geo.SubnationalDivision;
import frgp.utn.edu.ar.quepasa.repository.geo.CountryRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.SubnationalDivisionRepository;
import frgp.utn.edu.ar.quepasa.service.geo.SubnationalDivisionService;
import quepasa.api.validators.geo.subnationaldivision.SubnationalDivisionISO3Validator;
import quepasa.api.validators.geo.subnationaldivision.SubnationalDivisionLabelValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import quepasa.api.validators.commons.ObjectValidator;
import quepasa.api.validators.geo.subnationaldivision.CountryIso3Validator;

import java.util.List;
import java.util.Optional;

@Service
public class SubnationalDivisionServiceImpl implements SubnationalDivisionService {

    private final SubnationalDivisionRepository repository;
    private final CountryRepository countryRepository;

    @Autowired
    public SubnationalDivisionServiceImpl(
            SubnationalDivisionRepository repository,
            CountryRepository countryRepository
    ) {
        this.repository = repository;
        this.countryRepository = countryRepository;
    }

    @Override
    public SubnationalDivision save(SubnationalDivision file) {
        String iso3 = new SubnationalDivisionISO3Validator(file.getIso3())
                .isNotNullOrEmpty()
                .isValidISO31662()
                .isAvailable(repository)
                .build();
        String label = new SubnationalDivisionLabelValidator(file.getLabel())
                .isNotNullOrEmpty()
                .isValidLabel()
                .hasValidLength()
                .build();
        Country country = new ObjectValidator<>(file.getCountry(), "country")
                .isNotNull()
                .build();
        String countryCode = new CountryIso3Validator(country.getIso3())
                .exists(countryRepository)
                .build();
        file.setIso3(iso3);
        file.setLabel(label);
        file.setCountry(country);
        file.setActive(true);
        return repository.save(file);
    }

    @Override
    public List<SubnationalDivision> listFrom(String countryCode) {
        return repository.getAllFrom(countryCode);
    }

    @Override
    public Page<SubnationalDivision> pageFrom(String countryCode, Pageable pageable) {
        return repository.pageAllFrom(countryCode, pageable);
    }

    @Override
    public Optional<SubnationalDivision> findById(String id) {
        return repository.findByIso3(id);
    }

    @Override
    public SubnationalDivision getById(String id) throws Fail {
        var search = repository.findByIso3(id);
        if(search.isEmpty()) throw new Fail("State not found. ", HttpStatus.NOT_FOUND);
        return search.get();
    }

    @Override
    public SubnationalDivision update(SubnationalDivisionUpdateRequest request, String iso3) {
        var file = repository.findByIso3(iso3)
                .orElseThrow(() -> new Fail("State not found. ", HttpStatus.NOT_FOUND));
        if(request.hasLabel()) {
            String label = new SubnationalDivisionLabelValidator(request.getLabel())
                    .isNotNullOrEmpty()
                    .isValidLabel()
                    .hasValidLength()
                    .build();
            file.setLabel(label);
        }
        if(request.hasDenomination()) {
            file.setDenomination(request.getDenomination());
        }
        if(request.hasCountry()) {
            Country country = new ObjectValidator<>(request.getCountry(), "country")
                    .isNotNull()
                    .build();
            String countryCode = new CountryIso3Validator(country.getIso3())
                    .exists(countryRepository)
                    .build();
            file.setCountry(country);
        }
        return repository.save(file);
    }

    @Override
    public void delete(String iso3) {
        var file = repository.findByIso3(iso3)
                .orElseThrow(() -> new Fail("State not found. ", HttpStatus.NOT_FOUND));
        file.setActive(false);
        repository.save(file);
    }

}
