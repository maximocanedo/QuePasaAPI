package frgp.utn.edu.ar.quepasa.service.validators.geo.subnationaldivision;

import frgp.utn.edu.ar.quepasa.model.geo.Country;
import frgp.utn.edu.ar.quepasa.repository.geo.CountryRepository;
import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;

public class SubnationalDivisionCountryValidator extends ValidatorBuilder<SubnationalDivisionCountryValidator, Country> {
    public SubnationalDivisionCountryValidator(Country value) {
        super(value, "country");
    }
    public SubnationalDivisionCountryValidator exists(CountryRepository repository) {
        if(getValue() == null) {
            super.invalidate("El objeto de la propiedad 'country' no puede ser nulo. ");
        }
        if(!repository.existsByIso3(getValue().getIso3())) {
            super.invalidate("El país al que se hace referencia no existe. ");
        }
        return this;
    }
}
