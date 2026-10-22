package co.edu.poli.sw2Hexagonal.infraestructura.weather;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Cliente HTTP de la API de clima actual de Open-Meteo
 * ({@code https://api.open-meteo.com/v1/forecast}).
 * <p>
 * Solicita únicamente las cinco variables que exige el negocio y devuelve el
 * JSON sin procesar. La traducción al dominio la hace
 * {@link OpenWeatherAdapter}.
 * </p>
 */
public class OpenWeatherClient {

    /** URL base de la API de clima. */
    private static final String URL_BASE = "https://api.open-meteo.com/v1/forecast";

    /**
     * Variables solicitadas: temperatura, humedad, viento, presión a nivel
     * del mar y visibilidad.
     */
    private static final String VARIABLES =
            "temperature_2m,relative_humidity_2m,wind_speed_10m,pressure_msl,visibility";

    /** Cliente HTTP reutilizable con tiempo de conexión limitado. */
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Consulta el clima actual en unas coordenadas.
     *
     * @param latitud  latitud en grados decimales
     * @param longitud longitud en grados decimales
     * @return JSON de respuesta con el bloque {@code current}
     * @throws IllegalStateException si la API responde con error, falla la
     *                               conexión o la consulta se interrumpe
     */
    public String consultarActual(double latitud, double longitud) {
        String url = URL_BASE
                + "?latitude=" + latitud
                + "&longitude=" + longitud
                + "&current=" + VARIABLES
                + "&timezone=auto";

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException("API de clima respondió HTTP " + response.statusCode());
            }
            return response.body();
        } catch (IOException e) {
            throw new IllegalStateException("Error de conexión con la API de clima", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Consulta de clima interrumpida", e);
        }
    }
}