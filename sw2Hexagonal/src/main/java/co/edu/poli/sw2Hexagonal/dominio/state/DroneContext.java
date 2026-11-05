package co.edu.poli.sw2Hexagonal.dominio.state;

import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;

/**
 * Wrapper de {@link Drone} y Context del patrón State.
 * <p>
 * Trae un dron, sin modificar la clase {@code Drone}, mantiene una referencia
 * a su estado actual y le delega todas las operaciones. Los estados concretos
 * realizan las transiciones mediante {@link #changeState(DroneState)}. Todo
 * dron inicia APAGADO.
 * </p>
 */
public class DroneContext {

    /** Dron envuelto. */
    private final Drone drone;

    /** Estado operativo actual. */
    private DroneState estado;

    /**
     * Crea el contexto de un dron en estado APAGADO.
     *
     * @param drone dron a envolver
     */
    public DroneContext(Drone drone) {
        this.drone = drone;
        changeState(new OffState());
    }

    /**
     * @return dron envuelto
     */
    public Drone getDrone() {
        return drone;
    }

    /**
     * @return estado operativo actual
     */
    public DroneState getEstado() {
        return estado;
    }

    /**
     * Cambia el estado actual y le asigna este contexto.
     *
     * @param estado nuevo estado
     */
    public final void changeState(DroneState estado) {
        this.estado = estado;
        this.estado.setContext(this);
    }

    /**
     * Enciende el dron.
     *
     * @throws IllegalStateException si el estado actual no lo permite
     */
    public void encender() {
        estado.encender();
    }

    /**
     * Apaga el dron.
     *
     * @throws IllegalStateException si el estado actual no lo permite
     */
    public void apagar() {
        estado.apagar();
    }

    /**
     * Hace despegar el dron.
     *
     * @throws IllegalStateException si el estado actual no lo permite
     */
    public void despegar() {
        estado.despegar();
    }

    /**
     * Inicia el aterrizaje del dron.
     *
     * @throws IllegalStateException si el estado actual no lo permite
     */
    public void iniciarAterrizaje() {
        estado.iniciarAterrizaje();
    }

    /**
     * Finaliza el aterrizaje del dron.
     *
     * @throws IllegalStateException si el estado actual no lo permite
     */
    public void finalizarAterrizaje() {
        estado.finalizarAterrizaje();
    }
}