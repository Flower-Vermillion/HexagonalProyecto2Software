package co.edu.poli.sw2Hexagonal.dominio.modelo.weather;

/**
 * Objeto de valor del dominio que representa el clima actual obtenido de la
 * API externa.
 * <p>
 * Contiene únicamente los cinco aspectos meteorológicos que exige el negocio:
 * temperatura, humedad, velocidad del viento, presión atmosférica y
 * visibilidad. Es inmutable: una vez creado, sus valores no cambian.
 * </p>
 * <p>
 * Es la información que el patrón Observer envía a los observadores en cada
 * notificación, y la que el Chain of Responsibility evalúa para decidir la
 * viabilidad de vuelo.
 * </p>
 *
 * @see WeatherObserver
 */
public class Weather {

    /** Temperatura del aire a 2 m, en grados Celsius. */
    private final double temperatura;

    /** Humedad relativa, en porcentaje (0 a 100). */
    private final double humedad;

    /** Velocidad del viento a 10 m, en km/h. */
    private final double velocidadViento;

    /** Presión atmosférica a nivel del mar, en hPa. */
    private final double presion;

    /** Visibilidad horizontal, en metros. */
    private final double visibilidad;

    /**
     * Crea un clima validando las reglas básicas de cada magnitud.
     *
     * @param temperatura     temperatura en °C
     * @param humedad         humedad relativa en %, entre 0 y 100
     * @param velocidadViento velocidad del viento en km/h, no negativa
     * @param presion         presión atmosférica en hPa, mayor que 0
     * @param visibilidad     visibilidad en metros, no negativa
     * @throws IllegalArgumentException si algún valor está fuera de su rango válido
     */
    public Weather(double temperatura, double humedad, double velocidadViento,
                   double presion, double visibilidad) {
        if (humedad < 0 || humedad > 100) {
            throw new IllegalArgumentException("La humedad debe estar entre 0 y 100");
        }
        if (velocidadViento < 0) {
            throw new IllegalArgumentException("La velocidad del viento no puede ser negativa");
        }
        if (presion <= 0) {
            throw new IllegalArgumentException("La presión debe ser mayor que 0");
        }
        if (visibilidad < 0) {
            throw new IllegalArgumentException("La visibilidad no puede ser negativa");
        }
        this.temperatura = temperatura;
        this.humedad = humedad;
        this.velocidadViento = velocidadViento;
        this.presion = presion;
        this.visibilidad = visibilidad;
    }

    /**
     * @return temperatura en °C
     */
    public double getTemperatura() {
        return temperatura;
    }

    /**
     * @return humedad relativa en %
     */
    public double getHumedad() {
        return humedad;
    }

    /**
     * @return velocidad del viento en km/h
     */
    public double getVelocidadViento() {
        return velocidadViento;
    }

    /**
     * @return presión atmosférica a nivel del mar en hPa
     */
    public double getPresion() {
        return presion;
    }

    /**
     * @return visibilidad en metros
     */
    public double getVisibilidad() {
        return visibilidad;
    }

    /**
     * @return representación legible de los cinco aspectos climáticos
     */
    @Override
    public String toString() {
        return String.format(
                "Temp=%.1f°C, Humedad=%.0f%%, Viento=%.1f km/h, Presión=%.1f hPa, Visibilidad=%.0f m",
                temperatura, humedad, velocidadViento, presion, visibilidad);
    }
}