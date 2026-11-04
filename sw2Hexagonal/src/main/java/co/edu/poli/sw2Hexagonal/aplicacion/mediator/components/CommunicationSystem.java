package co.edu.poli.sw2Hexagonal.aplicacion.mediator.components;

import co.edu.poli.sw2Hexagonal.aplicacion.mediator.DroneMediator;
import co.edu.poli.sw2Hexagonal.aplicacion.mediator.DroneMediator.Evento;

/**
 * Componente del patrón Mediator: sistema de comunicación.
 * <p>
 * Establece el enlace entre el dron y la estación de control. El enlace solo
 * es posible si la zona está dentro del alcance máximo.
 * </p>
 */
public class CommunicationSystem extends DroneMediator.Componente {

    /** Alcance máximo del enlace, en km. */
    public static final double ALCANCE_MAXIMO_KM = 15.0;

    /**
     * Intenta establecer el enlace de comunicación con el dron.
     *
     * @param distanciaKm distancia hasta la zona, en km
     */
    public void establecerEnlace(double distanciaKm) {
        if (distanciaKm <= ALCANCE_MAXIMO_KM) {
            notificar(Evento.ENLACE_ESTABLECIDO, String.format(
                    "Enlace establecido en canal 5.8 GHz (zona a %.1f km, alcance máximo %.0f km)",
                    distanciaKm, ALCANCE_MAXIMO_KM));
        } else {
            notificar(Evento.ENLACE_FALLIDO, String.format(
                    "Sin enlace: la zona está a %.1f km y el alcance máximo es %.0f km",
                    distanciaKm, ALCANCE_MAXIMO_KM));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNombre() {
        return "Comunicación";
    }
}