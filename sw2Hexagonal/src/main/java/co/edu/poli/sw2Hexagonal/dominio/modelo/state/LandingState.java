package co.edu.poli.sw2Hexagonal.dominio.modelo.state;

/**
 * Estado ATERRIZANDO: el dron está ejecutando el aterrizaje y solo puede
 * finalizarlo. No puede iniciar un nuevo vuelo hasta terminar.
 */
public class LandingState implements DroneState {

    /** Contexto del dron que está en este estado. */
    private DroneContext context;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContext(DroneContext context) {
        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNombre() {
        return "ATERRIZANDO";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void encender() {
        throw new IllegalStateException("El dron ya está encendido y aterrizando.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apagar() {
        throw new IllegalStateException("Un dron ATERRIZANDO no puede apagarse: primero debe finalizar el aterrizaje.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void despegar() {
        throw new IllegalStateException("Un dron ATERRIZANDO no puede iniciar un nuevo vuelo hasta finalizar el aterrizaje.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void iniciarAterrizaje() {
        throw new IllegalStateException("El dron ya está aterrizando.");
    }

    /**
     * Pasa el dron a EN ESPERA.
     */
    @Override
    public void finalizarAterrizaje() {
        context.changeState(new ReadyState());
    }
}