package co.edu.poli.sw2Hexagonal.infraestructura.util;

import co.edu.poli.sw2Hexagonal.dominio.modelo.strategy.PuntoGeografico;

/**
 * Utilidad que calcula la distancia entre dos puntos geográficos con la
 * fórmula de Haversine, que considera la curvatura de la Tierra.
 */
public final class HaversineUtil {

    /** Radio medio de la Tierra en kilómetros. */
    public static final double RADIO_TIERRA_KM = 6371.0;

    /**
     * Clase de utilidad: no se instancia.
     */
    private HaversineUtil() {
    }

    /**
     * Calcula la distancia en kilómetros entre dos puntos geográficos.
     *
     * @param origen  punto de origen
     * @param destino punto de destino
     * @return distancia en kilómetros
     */
    public static double distanciaKm(PuntoGeografico origen, PuntoGeografico destino) {
        double lat1 = Math.toRadians(origen.getLatitud());
        double lat2 = Math.toRadians(destino.getLatitud());
        double dLat = Math.toRadians(destino.getLatitud() - origen.getLatitud());
        double dLon = Math.toRadians(destino.getLongitud() - origen.getLongitud());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RADIO_TIERRA_KM * c;
    }
}