package frgp.utn.edu.ar.quepasa.service.geo.impl;

import frgp.utn.edu.ar.quepasa.model.geo.Country;
import frgp.utn.edu.ar.quepasa.model.geo.SubnationalDivision;
import frgp.utn.edu.ar.quepasa.repository.geo.CountryRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.SubnationalDivisionRepository;
import frgp.utn.edu.ar.quepasa.service.geo.SubnationalDivisionService;
import frgp.utn.edu.ar.quepasa.service.validators.geo.subnationaldivision.SubnationalDivisionCountryObjectValidatorBuilder;
import frgp.utn.edu.ar.quepasa.service.validators.geo.subnationaldivision.SubnationalDivisionISO3ValidatorBuilder;
import frgp.utn.edu.ar.quepasa.service.validators.geo.subnationaldivision.SubnationalDivisionLabelValidatorBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        String iso3 = new SubnationalDivisionISO3ValidatorBuilder(file.getIso3())
                .isNotNullOrEmpty()
                .isValidISO31662()
                .isAvailable(repository)
                .build();
        String label = new SubnationalDivisionLabelValidatorBuilder(file.getLabel())
                .isNotNullOrEmpty()
                .isValidLabel()
                .hasValidLength()
                .build();
        Country country = new SubnationalDivisionCountryObjectValidatorBuilder(file.getCountry())
                .exists(countryRepository)
                .build();
        file.setIso3(iso3);
        file.setLabel(label);
        file.setCountry(country);
        file.setActive(true);
        var saved = repository.save(file);
        return saved;
    }
}
