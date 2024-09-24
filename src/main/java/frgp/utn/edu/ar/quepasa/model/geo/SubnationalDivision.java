package frgp.utn.edu.ar.quepasa.model.geo;

import jakarta.persistence.*;

@Entity
public class SubnationalDivision {
    private String iso3;
    private String label;
    private SubnationalDivisionDenomination denomination;
    private Country country;
    private boolean active = true;

    public SubnationalDivision() {}

    @Id
    public String getIso3() { return iso3; }
    public void setIso3(String iso3) { this.iso3 = iso3; }

    @Column
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    @Column
    @Enumerated(EnumType.STRING)
    public SubnationalDivisionDenomination getDenomination() { return denomination; }
    public void setDenomination(SubnationalDivisionDenomination denomination) { this.denomination = denomination; }

    @ManyToOne
    @JoinColumn(name = "country_iso3")
    public Country getCountry() { return country; }
    public void setCountry(Country country) { this.country = country; }

    @Column
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

}
