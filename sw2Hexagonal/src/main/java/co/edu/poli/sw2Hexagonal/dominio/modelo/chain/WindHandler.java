package co.edu.poli.sw2Hexagonal.dominio.modelo.chain;

import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;

/**
 * Regla: la velocidad del viento debe ser menor o igual a 40 km/h.
 */
public class WindHandler extends WeatherHandler {

    public static final double MAXIMA = 40.0;

    @Override
    protected double obtenerValor(Weather clima) {
        return clima.getVelocidadViento();
    }

    @Override
    protected boolean cumple(double valor) {
        return valor <= MAXIMA;
    }

    @Override
    public String getRegla() {
        return "Velocidad del viento ≤ 40 km/h";
    }

    @Override
    protected String getNombreMagnitud() {
        return "velocidad del viento";
    }

    @Override
    protected String getUnidad() {
        return "km/h";
    }
}
