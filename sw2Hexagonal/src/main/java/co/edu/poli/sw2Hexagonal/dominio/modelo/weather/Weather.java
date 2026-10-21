package co.edu.poli.sw2Hexagonal.dominio.modelo.weather;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entidad de dominio con el clima actual de una ciudad y el resultado de su
 * evaluación para la operación de drones.
 * <p>
 * Se llena en tres momentos:
 * </p>
 * <ol>
 *   <li>Geocodificación: ciudad, región, país, latitud y longitud.</li>
 *   <li>Clima actual: temperatura, humedad, viento, presión y visibilidad.</li>
 *   <li>Cadena de validaciones: reglas aprobadas, regla incumplida y motivo.</li>
 * </ol>
 * Las magnitudes se guardan en las unidades de las reglas de negocio
 * (°C, %, km/h, hPa, km). {@link Double#NaN} indica un dato no disponible.
 */
public class Weather {

    public static final String MENSAJE_APTO = "CLIMA APTO PARA OPERACIÓN DE DRONES";
    public static final String MENSAJE_NO_APTO = "CLIMA NO APTO PARA OPERACIÓN DE DRONES";

    // ---------- Ubicación ----------
    private String ciudad;
    private String region = "";
    private String pais = "";
    private double latitud;
    private double longitud;

    // ---------- Clima actual ----------
    private String fechaHora = "";
    private double temperatura = Double.NaN;      // °C
    private double humedad = Double.NaN;          // %
    private double velocidadViento = Double.NaN;  // km/h
    private double presion = Double.NaN;          // hPa
    private double visibilidad = Double.NaN;      // km

    // ---------- Resultado de la cadena ----------
    private final List<String> validacionesAprobadas = new ArrayList<>();
    private boolean evaluado;
    private boolean apto;
    private String reglaIncumplida;
    private String motivoRechazo;

    public Weather() {
    }

    /**
     * @param ciudad   nombre de la ciudad
     * @param region   departamento, estado o provincia
     * @param pais     país
     * @param latitud  latitud en grados decimales
     * @param longitud longitud en grados decimales
     */
    public Weather(String ciudad, String region, String pais, double latitud, double longitud) {
        this.ciudad = ciudad;
        this.region = region == null ? "" : region;
        this.pais = pais == null ? "" : pais;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    // ---------- Comportamiento de la evaluación ----------

    /**
     * Registra una regla que se cumplió. Lo usan los handlers de la cadena.
     *
     * @param regla descripción de la regla con el valor medido
     */
    public void registrarAprobacion(String regla) {
        validacionesAprobadas.add(regla);
    }

    /**
     * Marca el clima como NO apto y guarda el motivo del rechazo.
     *
     * @param regla  regla que no se cumplió
     * @param motivo explicación con el valor medido
     */
    public void rechazar(String regla, String motivo) {
        this.evaluado = true;
        this.apto = false;
        this.reglaIncumplida = regla;
        this.motivoRechazo = motivo;
    }

    /**
     * Marca el clima como apto. Lo invoca el último handler cuando todas las
     * reglas se cumplieron.
     */
    public void aprobar() {
        this.evaluado = true;
        this.apto = true;
        this.reglaIncumplida = null;
        this.motivoRechazo = null;
    }

    /**
     * Borra un resultado anterior para poder evaluar de nuevo.
     */
    public void reiniciarEvaluacion() {
        validacionesAprobadas.clear();
        evaluado = false;
        apto = false;
        reglaIncumplida = null;
        motivoRechazo = null;
    }

    /**
     * @return {@code true} solo si la cadena terminó y todas las reglas se cumplieron
     */
    public boolean isApto() {
        return evaluado && apto;
    }

    public boolean isEvaluado() {
        return evaluado;
    }

    public String getReglaIncumplida() {
        return reglaIncumplida;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public List<String> getValidacionesAprobadas() {
        return Collections.unmodifiableList(validacionesAprobadas);
    }

    /**
     * @return mensaje final que se le informa al usuario
     */
    public String getMensajeEvaluacion() {
        if (!evaluado) {
            return "Clima sin evaluar.";
        }
        if (apto) {
            return MENSAJE_APTO;
        }
        return MENSAJE_NO_APTO + " — Regla incumplida: " + reglaIncumplida + ". " + motivoRechazo;
    }

    /**
     * @return por ejemplo "Bogotá, Bogota D.C., Colombia"
     */
    public String getNombreCompleto() {
        StringBuilder sb = new StringBuilder(ciudad == null ? "?" : ciudad);
        if (!region.isBlank() && !region.equalsIgnoreCase(ciudad)) {
            sb.append(", ").append(region);
        }
        if (!pais.isBlank()) {
            sb.append(", ").append(pais);
        }
        return sb.toString();
    }

    // ---------- Getters y setters ----------

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region == null ? "" : region;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais == null ? "" : pais;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora == null ? "" : fechaHora;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public double getHumedad() {
        return humedad;
    }

    public void setHumedad(double humedad) {
        this.humedad = humedad;
    }

    public double getVelocidadViento() {
        return velocidadViento;
    }

    public void setVelocidadViento(double velocidadViento) {
        this.velocidadViento = velocidadViento;
    }

    public double getPresion() {
        return presion;
    }

    public void setPresion(double presion) {
        this.presion = presion;
    }

    public double getVisibilidad() {
        return visibilidad;
    }

    public void setVisibilidad(double visibilidad) {
        this.visibilidad = visibilidad;
    }

    @Override
    public String toString() {
        return String.format(
                "%s | %s | Temp: %.1f °C | Humedad: %.0f %% | Viento: %.1f km/h | Presión: %.1f hPa | Visibilidad: %.1f km",
                getNombreCompleto(), fechaHora, temperatura, humedad, velocidadViento, presion, visibilidad);
    }
}
