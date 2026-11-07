package co.edu.poli.sw2Hexagonal.dominio.modelo.state;

/**
 * Estado APAGADO: el dron no está operativo y únicamente puede encenderse.
 */
public class OffState implements DroneState {

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
        return "APAGADO";
    }

    /**
     * Pasa el dron a EN ESPERA.
     */
    @Override
    public void encender() {
        context.changeState(new ReadyState());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apagar() {
        throw new IllegalStateException("El dron ya está apagado.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void despegar() {
        throw new IllegalStateException("Un dron APAGADO no puede despegar: primero debe encenderse.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void iniciarAterrizaje() {
        throw new IllegalStateException("Un dron APAGADO no puede aterrizar: no está en vuelo.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finalizarAterrizaje() {
        throw new IllegalStateException("Un dron APAGADO no tiene un aterrizaje en curso.");
    }
}