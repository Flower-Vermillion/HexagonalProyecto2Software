package co.edu.poli.sw2Hexagonal.dominio.modelo.strategy;

/**
 * Objeto de valor que representa un punto geográfico (origen o destino de
 * una ruta), identificado por su nombre y sus coordenadas.
 * <p>
 * Es inmutable y valida que las coordenadas estén en rangos válidos.
 * </p>
 */
public class PuntoGeografico {

    /** Nombre descriptivo del punto. */
    private final String nombre;

    /** Latitud en grados decimales, entre -90 y 90. */
    private final double latitud;

    /** Longitud en grados decimales, entre -180 y 180. */
    private final double longitud;

    /**
     * Crea un punto geográfico.
     *
     * @param nombre   nombre descriptivo del punto
     * @param latitud  latitud en grados decimales, entre -90 y 90
     * @param longitud longitud en grados decimales, entre -180 y 180
     * @throws IllegalArgumentException si alguna coordenada está fuera de rango
     */
    public PuntoGeografico(String nombre, double latitud, double longitud) {
        if (latitud < -90 || latitud > 90) {
            throw new IllegalArgumentException("La latitud debe estar entre -90 y 90: " + latitud);
        }
        if (longitud < -180 || longitud > 180) {
            throw new IllegalArgumentException("La longitud debe estar entre -180 y 180: " + longitud);
        }
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    /**
     * @return nombre del punto
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @return latitud en grados decimales
     */
    public double getLatitud() {
        return latitud;
    }

    /**
     * @return longitud en grados decimales
     */
    public double getLongitud() {
        return longitud;
    }

    /**
     * @return nombre y coordenadas del punto
     */
    @Override
    public String toString() {
        return String.format("%s (%.5f, %.5f)", nombre, latitud, longitud);
    }
}