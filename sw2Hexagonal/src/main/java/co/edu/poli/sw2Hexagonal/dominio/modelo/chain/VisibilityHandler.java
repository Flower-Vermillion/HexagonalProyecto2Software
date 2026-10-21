package co.edu.poli.sw2Hexagonal.dominio.modelo.chain;

import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;

/**
 * Regla: la visibilidad debe ser mayor o igual a 5 km.
 */
public class VisibilityHandler extends WeatherHandler {

    public static final double MINIMA = 5.0;

    @Override
    protected double obtenerValor(Weather clima) {
        return clima.getVisibilidad();
    }

    @Override
    protected boolean cumple(double valor) {
        return valor >= MINIMA;
    }

    @Override
    public String getRegla() {
        return "Visibilidad ≥ 5 km";
    }

    @Override
    protected String getNombreMagnitud() {
        return "visibilidad";
    }

    @Override
    protected String getUnidad() {
        return "km";
    }
}
