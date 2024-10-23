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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "Trend{" +
                "tag='" + tag + '\'' +
                ", cantidad=" + cantidad +
                '}';
    }
}







