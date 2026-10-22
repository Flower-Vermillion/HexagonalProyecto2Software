package co.edu.poli.sw2Hexagonal.dominio.modelo.chain;

import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;

/**
 * Tercer eslabón de la cadena.
 * <p>
 * Regla: la velocidad del viento debe ser menor o igual a 40 km/h.
 * </p>
 */
public class WindHandler extends WeatherHandler {

    /** Velocidad máxima del viento permitida, en km/h. */
    public static final double MAXIMA = 40.0;

    /**
     * {@inheritDoc}
     */
    @Override
    protected double obtenerValor(Weather clima) {
        return clima.getVelocidadViento();
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
        return "Velocidad del viento ≤ 40 km/h";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getNombreMagnitud() {
        return "velocidad del viento";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getUnidad() {
        return "km/h";
    }
}