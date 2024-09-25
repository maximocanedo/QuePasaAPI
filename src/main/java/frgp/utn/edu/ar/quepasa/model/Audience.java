package frgp.utn.edu.ar.quepasa.model;

/**
 * Audiencia o alcance de recursos.
 */
public enum Audience {
    /**
     * <b>Público. </b> Sin restricciones de audiencia.
     */
    PUBLIC,
    /**
     * <b>Nacional. </b> Alcance sólamente a usuarios residiendo en el país.
     */
    NATIONAL,
    /**
     * <b>Subnacional. </b> Alcance sólamente a usuarios de una división subnacional específica.
     */
    SUBNATIONAL,
    /**
     * <b>Ciudad. </b> Alcance sólamente a usuarios de una ciudad específica.
     */
    CITY,
    /**
     * <b>Barrial. </b> Acceso restringido a usuarios residentes de un barrio en específico.
     */
    NEIGHBORHOOD
}
