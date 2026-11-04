package co.edu.poli.sw2Hexagonal.aplicacion.mediator;

import java.util.List;

/**
 * Interfaz Mediator del patrón Mediator para coordinar una operación de
 * vigilancia con drones.
 * <p>
 * El mediador es el único que conoce a los componentes de la operación
 * (navegación, sensores, batería y comunicación). Los componentes no se
 * comunican entre sí: el mediador los llama en orden y controla las
 * condiciones necesarias para iniciar la misión.
 * </p>
 *
 * @see MissionCoordinator
 */
public interface DroneMediator {

    /**
     * Coordina el inicio de una operación de vigilancia.
     *
     * @param codigoDron        código del dron
     * @param zona              zona de vigilancia
     * @param distanciaKm       distancia hasta la zona, en km
     * @param bateriaDisponible batería disponible, en %
     * @return {@code true} si la misión quedó iniciada
     */
    boolean iniciarMision(String codigoDron, String zona, double distanciaKm, double bateriaDisponible);

    /**
     * @return registro de todo lo ocurrido en la última operación, en orden
     */
    List<String> getBitacora();
}