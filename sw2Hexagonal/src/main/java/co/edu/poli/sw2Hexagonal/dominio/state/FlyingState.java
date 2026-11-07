package co.edu.poli.sw2Hexagonal.dominio.state;

/**
 * Estado VOLANDO: el dron está en vuelo y solo puede iniciar el aterrizaje.
 */
public class FlyingState implements DroneState {

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
        return "VOLANDO";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void encender() {
        throw new IllegalStateException("El dron ya está encendido y en vuelo.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apagar() {
        throw new IllegalStateException("Un dron VOLANDO no puede apagarse: primero debe aterrizar.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void despegar() {
        throw new IllegalStateException("El dron ya está en vuelo.");
    }

    /**
     * Pasa el dron a ATERRIZANDO.
     */
    @Override
    public void iniciarAterrizaje() {
        context.changeState(new LandingState());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finalizarAterrizaje() {
        throw new IllegalStateException("Un dron VOLANDO debe iniciar el aterrizaje antes de finalizarlo.");
    }
}