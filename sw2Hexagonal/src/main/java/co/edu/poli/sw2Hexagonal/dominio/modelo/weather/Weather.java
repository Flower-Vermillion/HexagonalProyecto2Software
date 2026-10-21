package co.edu.poli.sw2Hexagonal.dominio.modelo.weather;

/** Value object con los cinco aspectos climáticos. */
public class Weather {

    private final double temperatura;      // °C
    private final double humedad;          // %
    private final double velocidadViento;  // km/h
    private final double presion;          // hPa
    private final double visibilidad;      // metros

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

    public double getTemperatura() { return temperatura; }
    public double getHumedad() { return humedad; }
    public double getVelocidadViento() { return velocidadViento; }
    public double getPresion() { return presion; }
    public double getVisibilidad() { return visibilidad; }

    @Override
    public String toString() {
        return String.format(
                "Temp=%.1f°C, Humedad=%.0f%%, Viento=%.1f km/h, Presión=%.1f hPa, Visibilidad=%.0f m",
                temperatura, humedad, velocidadViento, presion, visibilidad);
    }
}