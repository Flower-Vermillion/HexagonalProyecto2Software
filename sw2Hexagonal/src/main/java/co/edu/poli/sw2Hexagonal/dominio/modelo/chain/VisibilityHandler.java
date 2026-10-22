package co.edu.poli.sw2Hexagonal.dominio.modelo.chain;

import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;

/**
 * Último eslabón de la cadena. Si aprueba, el clima queda marcado como apto.
 * <p>
 * Regla: la visibilidad debe ser mayor o igual a 5 km. Como {@link Weather}
 * guarda la visibilidad en metros, el valor se convierte a kilómetros.
 * </p>
 */
public class VisibilityHandler extends WeatherHandler {

    /** Visibilidad mínima permitida, en km. */
    public static final double MINIMA = 5.0;

    /**
     * Obtiene la visibilidad del clima convertida de metros a kilómetros.
     *
     * @param clima clima
     * @return visibilidad en km
     */
    @Override
    protected double obtenerValor(Weather clima) {
        return clima.getVisibilidad() / 1000.0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean cumple(double valor) {
        return valor >= MINIMA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRegla() {
        return "Visibilidad ≥ 5 km";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getNombreMagnitud() {
        return "visibilidad";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getUnidad() {
        return "km";
    }
}