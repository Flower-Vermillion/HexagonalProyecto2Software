package co.edu.poli.sw2Hexagonal.dominio.modelo.command;

/**
 * Comando concreto que encapsula la tarea de escanear la zona de vuelo.
 */
public class EscanearZonaCommand implements DroneCommand {

    /** Receptor que realiza la operación. */
    private final Receptor receptor;

    /**
     * Crea el comando.
     *
     * @param receptor receptor que escaneará la zona
     */
    public EscanearZonaCommand(Receptor receptor) {
        this.receptor = receptor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String ejecutar() {
        return receptor.escanearZona();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNombre() {
        return "Escanear zona";
    }
}