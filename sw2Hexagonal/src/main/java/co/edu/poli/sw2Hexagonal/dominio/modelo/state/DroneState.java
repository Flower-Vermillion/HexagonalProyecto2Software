package co.edu.poli.sw2Hexagonal.dominio.modelo.state;

/**
 * Interfaz State del patrón State: declara las operaciones de un dron cuyo
 * resultado depende de su estado operativo.
 * <p>
 * Cada estado concreto conserva una referencia a su {@link DroneContext}. Si
 * una operación está permitida, el estado realiza la transición mediante
 * {@link DroneContext#changeState(DroneState)}. Si no lo está, lanza
 * {@link IllegalStateException} y el dron conserva su estado.
 * </p>
 * <p>
 * Ciclo operativo:
 * </p>
 * <pre>
 *   APAGADO → EN ESPERA → VOLANDO → ATERRIZANDO → EN ESPERA
 *                 ↓
 *              APAGADO
 * </pre>
 */
public interface DroneState {

    /**
     * Asigna el contexto de este estado.
     *
     * @param context contexto del dron que está en este estado
     */
    void setContext(DroneContext context);

    /**
     * @return nombre del estado operativo
     */
    String getNombre();

    /**
     * Enciende el dron.
     *
     * @throws IllegalStateException si la operación no está permitida
     */
    void encender();

    /**
     * Apaga el dron.
     *
     * @throws IllegalStateException si la operación no está permitida
     */
    void apagar();

    /**
     * Inicia el vuelo del dron.
     *
     * @throws IllegalStateException si la operación no está permitida
     */
    void despegar();

    /**
     * Inicia el proceso de aterrizaje del dron.
     *
     * @throws IllegalStateException si la operación no está permitida
     */
    void iniciarAterrizaje();

    /**
     * Finaliza el proceso de aterrizaje del dron.
     *
     * @throws IllegalStateException si la operación no está permitida
     */
    void finalizarAterrizaje();
}