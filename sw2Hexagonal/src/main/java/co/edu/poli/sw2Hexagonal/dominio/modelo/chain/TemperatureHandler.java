package co.edu.poli.sw2Hexagonal.dominio.modelo.chain;

import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;

/**
 * Regla: la temperatura debe estar entre 0 °C y 40 °C (inclusive).
 */
public class TemperatureHandler extends WeatherHandler {

    public static final double MINIMA = 0.0;
    public static final double MAXIMA = 40.0;

    @Override
    protected double obtenerValor(Weather clima) {
        return clima.getTemperatura();
    }

    @Override
    protected boolean cumple(double valor) {
        return valor >= MINIMA && valor <= MAXIMA;
    }

    @Override
    public String getRegla() {
        return "Temperatura entre 0 °C y 40 °C";
    }

    @Override
    protected String getNombreMagnitud() {
        return "temperatura";
    }

    @Override
    protected String getUnidad() {
        return "°C";
    }
}
