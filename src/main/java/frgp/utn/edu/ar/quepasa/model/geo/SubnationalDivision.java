package frgp.utn.edu.ar.quepasa.model.geo;

import frgp.utn.edu.ar.quepasa.model.enums.SubnationalDivisionDenomination;
import jakarta.persistence.*;

/**
 * Entidad que representa las <a href="https://es.wikipedia.org/wiki/Demarcaci%C3%B3n_administrativa">divisiones administrativas de segundo orden</a> vinculadas a un país.
 * Se usa el código ISO3 como clave primaria.
 */
@Entity
public class SubnationalDivision {
    private String iso3;
    private String label;
    private SubnationalDivisionDenomination denomination;
    private Country country;
    private boolean active = true;

    public SubnationalDivision() {}

    /**
     * Devuelve el código ISO 3166-2 de la división administrativa.
     * <p>
     *     <i>Por ejemplo, para "Córdoba" sería "AR-C". Para "Texas" sería "US-TX".</i>
     * </p>
     */
    @Id
    public String getIso3() { return iso3; }
    public void setIso3(String iso3) { this.iso3 = iso3; }

    /**
     * Devuelve el nombre en español de la división administrativa.
     */
    @Column
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    /**
     * Devuelve la denominación de la división administrativa.
     * <p>
     *     <i>
     *         Por ejemplo, "Buenos Aires" tendría asignada la denominación "Provincia", mientras que California tendría "Estado" como denominación asociada.
     *     </i>
     * </p>
     */
    @Column
    @Enumerated(EnumType.STRING)
    public SubnationalDivisionDenomination getDenomination() { return denomination; }
    public void setDenomination(SubnationalDivisionDenomination denomination) { this.denomination = denomination; }

    /**
     * Devuelve el país asociado a la división administrativa.
     * <p>
     *     <i>Por ejemplo, para "Chubut" sería "Argentina", para "Colonia" sería "Uruguay".</i>
     * </p>
     */
    @ManyToOne
    @JoinColumn(name = "country_iso3")
    public Country getCountry() { return country; }
    public void setCountry(Country country) { this.country = country; }

    /**
     * Devuelve el estado lógico del registro.
     */
    @Column
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

}
