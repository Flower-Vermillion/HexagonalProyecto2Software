package co.edu.poli.sw2Hexagonal.dominio.modelo.chain;

import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;

/**
 * Regla: la humedad relativa debe ser menor o igual a 85 %.
 */
public class HumidityHandler extends WeatherHandler {

    public static final double MAXIMA = 85.0;

    @Override
    protected double obtenerValor(Weather clima) {
        return clima.getHumedad();
    }

    @Override
    protected boolean cumple(double valor) {
        return valor <= MAXIMA;
    }

    @Override
    public String getRegla() {
        return "Humedad ≤ 85 %";
    }

    @Override
    protected String getNombreMagnitud() {
        return "humedad";
    }

    @Override
    protected String getUnidad() {
        return "%";
    }
}
