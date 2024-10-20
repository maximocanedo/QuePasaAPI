package frgp.utn.edu.ar.quepasa.data.request.geo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import frgp.utn.edu.ar.quepasa.model.enums.SubnationalDivisionDenomination;
import frgp.utn.edu.ar.quepasa.model.geo.Country;

public class SubnationalDivisionUpdateRequest {

    private String iso3;
    private String label;
    private SubnationalDivisionDenomination denomination;
    private Country country;

    public SubnationalDivisionUpdateRequest() {}

    public String getIso3() { return iso3; }
    public void setIso3(String iso3) { this.iso3 = iso3; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public SubnationalDivisionDenomination getDenomination() { return denomination; }
    public void setDenomination(SubnationalDivisionDenomination denomination) { this.denomination = denomination; }

    public Country getCountry() { return country; }
    public void setCountry(Country country) { this.country = country; }

    @JsonIgnore
    public boolean hasIso3() {
        return iso3 != null;
    }

    @JsonIgnore
    public boolean hasLabel() {
        return label != null;
    }

    @JsonIgnore
    public boolean hasDenomination() {
        return denomination != null;
    }

    @JsonIgnore
    public boolean hasCountry() {
        return country != null && country.getIso3() != null;
    }

}
