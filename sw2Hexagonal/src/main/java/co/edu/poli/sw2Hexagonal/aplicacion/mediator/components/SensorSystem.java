package co.edu.poli.sw2Hexagonal.aplicacion.mediator.components;

import co.edu.poli.sw2Hexagonal.aplicacion.mediator.DroneMediator;
import co.edu.poli.sw2Hexagonal.aplicacion.mediator.DroneMediator.Evento;

/**
 * Componente del patrón Mediator: sistema de sensores.
 * <p>
 * Activa los sensores necesarios para una operación de vigilancia y los
 * desactiva si la misión se aborta.
 * </p>
 */
public class SensorSystem extends DroneMediator.Componente {

    /** Sensores que se usan en una operación de vigilancia. */
    private static final String SENSORES_VIGILANCIA =
            "cámara HD, cámara infrarroja, detector de movimiento";

    /**
     * Activa los sensores de vigilancia.
     */
    public void activar() {
        notificar(Evento.SENSORES_ACTIVADOS, "Sensores activados: " + SENSORES_VIGILANCIA);
    }

    /**
     * Desactiva los sensores.
     */
    public void desactivar() {
        notificar(Evento.SENSORES_DESACTIVADOS, "Sensores desactivados");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNombre() {
        return "Sensores";
    }
}