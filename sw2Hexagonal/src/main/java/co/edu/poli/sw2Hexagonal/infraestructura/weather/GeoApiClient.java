package co.edu.poli.sw2Hexagonal.infraestructura.weather;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Cliente HTTP de la API de geocodificación de Open-Meteo
 * ({@code https://geocoding-api.open-meteo.com/v1/search}).
 * <p>
 * Solo se encarga de la comunicación HTTP y devuelve el JSON sin procesar.
 * La traducción a coordenadas la hace {@link GeoApiAdapter}.
 * </p>
 */
public class GeoApiClient {

    /** URL base de la API de geocodificación. */
    private static final String URL_BASE = "https://geocoding-api.open-meteo.com/v1/search";

    /** Cliente HTTP reutilizable con tiempo de conexión limitado. */
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Busca una ciudad en la API de geocodificación.
     *
     * @param ciudad nombre de la ciudad
     * @return JSON de respuesta, con a lo sumo un resultado
     * @throws IllegalStateException si la API responde con error, falla la
     *                               conexión o la consulta se interrumpe
     */
    public String buscar(String ciudad) {
        String url = URL_BASE
                + "?name=" + URLEncoder.encode(ciudad, StandardCharsets.UTF_8)
                + "&count=1&language=es&format=json";

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException("Geocodificación respondió HTTP " + response.statusCode());
            }
            return response.body();
        } catch (IOException e) {
            throw new IllegalStateException("Error de conexión con la API de geocodificación", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Consulta de geocodificación interrumpida", e);
        }
    }
}