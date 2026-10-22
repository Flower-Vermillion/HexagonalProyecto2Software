package co.edu.poli.sw2Hexagonal.dominio.modelo.chain;

import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;

/**
 * Cuarto eslabón de la cadena.
 * <p>
 * Regla: la presión atmosférica a nivel del mar debe estar entre 950 hPa y
 * 1050 hPa (inclusive).
 * </p>
 */
public class PressureHandler extends WeatherHandler {

    /** Presión mínima permitida, en hPa. */
    public static final double MINIMA = 950.0;

    /** Presión máxima permitida, en hPa. */
    public static final double MAXIMA = 1050.0;

    /**
     * {@inheritDoc}
     */
    @Override
    protected double obtenerValor(Weather clima) {
        return clima.getPresion();
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
        return "Presión atmosférica entre 950 hPa y 1050 hPa";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getNombreMagnitud() {
        return "presión atmosférica";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getUnidad() {
        return "hPa";
    }
}