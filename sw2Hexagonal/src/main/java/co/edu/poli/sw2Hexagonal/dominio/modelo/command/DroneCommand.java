package co.edu.poli.sw2Hexagonal.dominio.modelo.command;

import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;

/**
 * Interfaz Command del patrón Command: encapsula una tarea que un dron
 * ejecuta durante una misión.
 * <p>
 * Cada comando concreto guarda una referencia a su {@link Receptor} y, al
 * ejecutarse, le delega la operación. El Invoker
 * ({@code ProgramadorTareas}) programa los comandos y los ejecuta en el
 * orden en que se registraron, sin conocer cómo se realiza cada tarea.
 * </p>
 *
 * @see TomarFotografiaCommand
 * @see EscanearZonaCommand
 * @see MedirTemperaturaCommand
 */
public interface DroneCommand {

    /**
     * Receiver del patrón Command: operaciones que un dron puede realizar.
     * <p>
     * Se define aquí, en el dominio, para que los comandos no dependan de la
     * capa de aplicación. La implementa {@code DroneOperacionServicio}.
     * </p>
     */
    interface Receptor {

        /**
         * @return dron que realiza las operaciones
         */
        Drone getDrone();

        /**
         * Toma una fotografía.
         *
         * @return descripción del resultado
         */
        String tomarFotografia();

        /**
         * Escanea la zona de vuelo.
         *
         * @return descripción del resultado
         */
        String escanearZona();

        /**
         * Mide la temperatura del ambiente.
         *
         * @return descripción del resultado
         */
        String medirTemperatura();
    }

    /**
     * Ejecuta la tarea delegando en el receptor.
     *
     * @return resultado obtenido
     */
    String ejecutar();

    /**
     * @return nombre legible de la tarea, por ejemplo "Tomar fotografía"
     */
    String getNombre();
}