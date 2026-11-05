package co.edu.poli.sw2Hexagonal.dominio.state;

/**
 * Estado EN ESPERA: el dron está encendido, en tierra y preparado para
 * iniciar una operación. Puede despegar o apagarse.
 */
public class ReadyState implements DroneState {

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
        return "EN ESPERA";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void encender() {
        throw new IllegalStateException("El dron ya está encendido.");
    }

    /**
     * Pasa el dron a APAGADO.
     */
    @Override
    public void apagar() {
        context.changeState(new OffState());
    }

    /**
     * Pasa el dron a VOLANDO.
     */
    @Override
    public void despegar() {
        context.changeState(new FlyingState());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void iniciarAterrizaje() {
        throw new IllegalStateException("Un dron EN ESPERA no puede aterrizar: está en tierra.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finalizarAterrizaje() {
        throw new IllegalStateException("Un dron EN ESPERA no tiene un aterrizaje en curso.");
    }
}