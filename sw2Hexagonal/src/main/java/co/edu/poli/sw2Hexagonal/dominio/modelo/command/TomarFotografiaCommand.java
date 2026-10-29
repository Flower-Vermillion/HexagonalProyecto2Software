package co.edu.poli.sw2Hexagonal.dominio.modelo.command;

/**
 * Comando concreto que encapsula la tarea de tomar una fotografía.
 */
public class TomarFotografiaCommand implements DroneCommand {

    /** Receptor que realiza la operación. */
    private final Receptor receptor;

    /**
     * Crea el comando.
     *
     * @param receptor receptor que tomará la fotografía
     */
    public TomarFotografiaCommand(Receptor receptor) {
        this.receptor = receptor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String ejecutar() {
        return receptor.tomarFotografia();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNombre() {
        return "Tomar fotografía";
    }
}