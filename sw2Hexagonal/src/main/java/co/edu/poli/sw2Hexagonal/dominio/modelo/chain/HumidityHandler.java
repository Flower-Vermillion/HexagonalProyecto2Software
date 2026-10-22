package co.edu.poli.sw2Hexagonal.dominio.modelo.chain;

import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;

/**
 * Segundo eslabón de la cadena.
 * <p>
 * Regla: la humedad relativa debe ser menor o igual a 85 %.
 * </p>
 */
public class HumidityHandler extends WeatherHandler {

    /** Humedad máxima permitida, en %. */
    public static final double MAXIMA = 85.0;

    /**
     * {@inheritDoc}
     */
    @Override
    protected double obtenerValor(Weather clima) {
        return clima.getHumedad();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean cumple(double valor) {
        return valor <= MAXIMA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRegla() {
        return "Humedad ≤ 85 %";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getNombreMagnitud() {
        return "humedad";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getUnidad() {
        return "%";
    }
}