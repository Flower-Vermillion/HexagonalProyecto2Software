package co.edu.poli.sw2Hexagonal.aplicacion.servicio;

import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;
import co.edu.poli.sw2Hexagonal.dominio.modelo.command.DroneCommand;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Receiver del patrón Command y Wrapper de {@link Drone}.
 * <p>
 * Envuelve un dron, sin modificar la clase {@code Drone}, y sabe cómo
 * realizar cada operación de la misión. Los comandos le delegan el trabajo.
 * </p>
 * <p>
 * Como el sistema no tiene sensores reales, los resultados son simulados:
 * nombre de la fotografía con la hora de captura, área escaneada con objetos
 * detectados y temperatura dentro de un rango realista.
 * </p>
 */
public class DroneOperacionServicio implements DroneCommand.Receptor {

    /** Formato de hora usado en el nombre de las fotografías. */
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HHmmss");

    /** Dron envuelto. */
    private final Drone drone;

    /** Generador de valores simulados. */
    private final Random random = new Random();

    /**
     * Crea el receptor para un dron.
     *
     * @param drone dron que realizará las operaciones
     * @throws IllegalArgumentException si el dron es nulo
     */
    public DroneOperacionServicio(Drone drone) {
        if (drone == null) {
            throw new IllegalArgumentException("Debe indicar un dron");
        }
        this.drone = drone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Drone getDrone() {
        return drone;
    }

    /**
     * Simula la captura de una fotografía.
     *
     * @return nombre del archivo generado
     */
    @Override
    public String tomarFotografia() {
        String archivo = "IMG_" + drone.getId() + "_" + LocalTime.now().format(FORMATO_HORA) + ".jpg";
        return "Fotografía capturada: " + archivo;
    }

    /**
     * Simula el escaneo de la zona de vuelo.
     *
     * @return área escaneada y cantidad de objetos detectados
     */
    @Override
    public String escanearZona() {
        double areaKm2 = 0.5 + random.nextDouble() * 1.5;
        int objetos = random.nextInt(11);
        return String.format("Zona escaneada: %.2f km², %d objeto(s) detectado(s)", areaKm2, objetos);
    }

    /**
     * Simula la medición de la temperatura ambiente.
     *
     * @return temperatura medida en °C
     */
    @Override
    public String medirTemperatura() {
        double temperatura = 10 + random.nextDouble() * 20;
        return String.format("Temperatura medida: %.1f °C", temperatura);
    }
}