package co.edu.poli.sw2Hexagonal.dominio.modelo.chain;

import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;

/**
 * Regla: la presión atmosférica debe estar entre 950 hPa y 1050 hPa (inclusive).
 */
public class PressureHandler extends WeatherHandler {

    public static final double MINIMA = 950.0;
    public static final double MAXIMA = 1050.0;

    @Override
    protected double obtenerValor(Weather clima) {
        return clima.getPresion();
    }

    @Override
    protected boolean cumple(double valor) {
        return valor >= MINIMA && valor <= MAXIMA;
    }

    @Override
    public String getRegla() {
        return "Presión atmosférica entre 950 hPa y 1050 hPa";
    }

    @Override
    protected String getNombreMagnitud() {
        return "presión atmosférica";
    }

    @Override
    protected String getUnidad() {
        return "hPa";
    }
}
