package co.edu.poli.sw2Hexagonal.dominio.modelo.chain;

import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;

/**
 * Primer eslabón de la cadena.
 * <p>
 * Regla: la temperatura debe estar entre 0 °C y 40 °C (inclusive).
 * </p>
 */
public class TemperatureHandler extends WeatherHandler {

    /** Temperatura mínima permitida, en °C. */
    public static final double MINIMA = 0.0;

    /** Temperatura máxima permitida, en °C. */
    public static final double MAXIMA = 40.0;

    /**
     * {@inheritDoc}
     */
    @Override
    protected double obtenerValor(Weather clima) {
        return clima.getTemperatura();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean cumple(double valor) {
        return valor >= MINIMA && valor <= MAXIMA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRegla() {
        return "Temperatura entre 0 °C y 40 °C";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getNombreMagnitud() {
        return "temperatura";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getUnidad() {
        return "°C";
    }
}