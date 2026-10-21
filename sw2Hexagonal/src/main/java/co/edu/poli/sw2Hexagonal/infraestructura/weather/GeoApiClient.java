package co.edu.poli.sw2Hexagonal.infraestructura.weather;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/** Cliente HTTP de la API de geocodificación de Open-Meteo. */
public class GeoApiClient {

    private static final String URL_BASE = "https://geocoding-api.open-meteo.com/v1/search";

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /** Devuelve el JSON crudo de la búsqueda de la ciudad. */
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