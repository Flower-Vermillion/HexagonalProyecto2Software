package co.edu.poli.sw2Hexagonal.dominio.modelo.command;

/**
 * Comando concreto que encapsula la tarea de medir la temperatura.
 */
public class MedirTemperaturaCommand implements DroneCommand {

    /** Receptor que realiza la operación. */
    private final Receptor receptor;

    /**
     * Crea el comando.
     *
     * @param receptor receptor que medirá la temperatura
     */
    public MedirTemperaturaCommand(Receptor receptor) {
        this.receptor = receptor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String ejecutar() {
        return receptor.medirTemperatura();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNombre() {
        return "Medir temperatura";
    }
}