package co.edu.poli.sw2Hexagonal.aplicacion.mediator.components;

/**
 * Componente del patrón Mediator: sistema de sensores.
 * <p>
 * Activa los sensores necesarios para una operación de vigilancia y los
 * desactiva si la misión se aborta. No conoce a los demás componentes; lo
 * coordina el mediador.
 * </p>
 */
public class SensorSystem {

    /** Sensores que se usan en una operación de vigilancia. */
    private static final String SENSORES_VIGILANCIA =
            "cámara HD, cámara infrarroja, detector de movimiento";

    /**
     * Activa los sensores de vigilancia.
     *
     * @return descripción de los sensores activados
     */
    public String activar() {
        return "Sensores activados: " + SENSORES_VIGILANCIA;
    }

    /**
     * Desactiva los sensores.
     *
     * @return descripción de la desactivación
     */
    public String desactivar() {
        return "Sensores desactivados";
    }
}