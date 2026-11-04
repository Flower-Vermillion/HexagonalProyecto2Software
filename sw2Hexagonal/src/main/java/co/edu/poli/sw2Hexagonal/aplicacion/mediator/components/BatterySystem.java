package co.edu.poli.sw2Hexagonal.aplicacion.mediator.components;

import co.edu.poli.sw2Hexagonal.aplicacion.mediator.DroneMediator;
import co.edu.poli.sw2Hexagonal.aplicacion.mediator.DroneMediator.Evento;

/**
 * Componente del patrón Mediator: sistema de batería.
 * <p>
 * Verifica que la batería disponible alcance para ir y volver de la zona de
 * vigilancia, conservando una reserva de seguridad.
 * </p>
 */
public class BatterySystem extends DroneMediator.Componente {

    /** Consumo de batería por kilómetro recorrido, en %. */
    public static final double CONSUMO_POR_KM = 1.0;

    /** Reserva mínima de seguridad, en %. */
    public static final double RESERVA = 20.0;

    /**
     * Verifica la disponibilidad energética para la misión.
     * Requerido = distancia × 2 (ida y vuelta) × consumo por km + reserva.
     *
     * @param distanciaKm        distancia hasta la zona, en km
     * @param bateriaDisponible  batería disponible, en %
     */
    public void verificarDisponibilidad(double distanciaKm, double bateriaDisponible) {
        double requerida = distanciaKm * 2 * CONSUMO_POR_KM + RESERVA;

        if (bateriaDisponible >= requerida) {
            notificar(Evento.BATERIA_SUFICIENTE, String.format(
                    "Batería suficiente: %.0f%% disponible, %.0f%% requerido (ida y vuelta + %.0f%% de reserva)",
                    bateriaDisponible, requerida, RESERVA));
        } else {
            notificar(Evento.BATERIA_INSUFICIENTE, String.format(
                    "Batería insuficiente: %.0f%% disponible, %.0f%% requerido (ida y vuelta + %.0f%% de reserva)",
                    bateriaDisponible, requerida, RESERVA));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNombre() {
        return "Batería";
    }
}