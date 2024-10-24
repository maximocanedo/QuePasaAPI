package frgp.utn.edu.ar.quepasa.model;

import java.io.Serializable;

public class Trend implements Serializable {

    private String tag;
    private int cantidad;

    public Trend() {}

    public Trend(String tag, int cantidad) {
        this.tag = tag;
        this.cantidad = cantidad;
    }

    /**
     * Devuelve la etiqueta asociada al trend.
     *
     * @return la etiqueta del trend.
     */
    public String getTag() {
        return tag;
    }

    /**
     * Establece la etiqueta asociada al trend.
     *
     * @param tag la nueva etiqueta del trend.
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Devuelve la cantidad de veces que ha sido etiquetada
     * la etiqueta asociada al trend.
     *
     * @return la cantidad de veces etiquetada.
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad de veces que ha sido etiquetada
     * la etiqueta asociada al trend.
     *
     * @param cantidad la nueva cantidad de veces etiquetada.
     */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

/**
 * Retorna al objeto Trend como una cadena de caracteres.
 *
 * @return la cadena de caracteres.
 */
    @Override
    public String toString() {
        return "Trend{" +
                "tag='" + tag + '\'' +
                ", cantidad=" + cantidad +
                '}';
    }
}







