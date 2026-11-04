package co.edu.poli.sw2Hexagonal.aplicacion.mediator.components;

/**
 * Componente del patrón Mediator: sistema de batería.
 * <p>
 * Verifica que la batería disponible alcance para ir y volver de la zona de
 * vigilancia, conservando una reserva de seguridad. No conoce a los demás
 * componentes; lo coordina el mediador.
 * </p>
 */
public class BatterySystem {

    /** Consumo de batería por kilómetro recorrido, en %. */
    public static final double CONSUMO_POR_KM = 1.0;

    /** Reserva mínima de seguridad, en %. */
    public static final double RESERVA = 20.0;

    /**
     * Calcula la batería requerida: distancia × 2 (ida y vuelta) × consumo
     * por km + reserva.
     *
     * @param distanciaKm distancia hasta la zona, en km
     * @return batería requerida, en %
     */
    public double calcularRequerida(double distanciaKm) {
        return distanciaKm * 2 * CONSUMO_POR_KM + RESERVA;
    }

    /**
     * Indica si la batería alcanza para la misión.
     *
     * @param distanciaKm       distancia hasta la zona, en km
     * @param bateriaDisponible batería disponible, en %
     * @return {@code true} si la batería es suficiente
     */
    public boolean esSuficiente(double distanciaKm, double bateriaDisponible) {
        return bateriaDisponible >= calcularRequerida(distanciaKm);
    }

    /**
     * Describe el resultado de la verificación energética.
     *
     * @param distanciaKm       distancia hasta la zona, en km
     * @param bateriaDisponible batería disponible, en %
     * @return descripción de la verificación
     */
    public String describirVerificacion(double distanciaKm, double bateriaDisponible) {
        String estado = esSuficiente(distanciaKm, bateriaDisponible) ? "suficiente" : "insuficiente";
        return String.format("Batería %s: %.0f%% disponible, %.0f%% requerido (ida y vuelta + %.0f%% de reserva)",
                estado, bateriaDisponible, calcularRequerida(distanciaKm), RESERVA);
    }
}