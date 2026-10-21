package co.edu.poli.sw2Hexagonal.infraestructura.weather;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/** Cliente HTTP de la API de clima actual de Open-Meteo. */
public class OpenWeatherClient {

    private static final String URL_BASE = "https://api.open-meteo.com/v1/forecast";
    private static final String VARIABLES =
            "temperature_2m,relative_humidity_2m,wind_speed_10m,surface_pressure,visibility";

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /** Devuelve el JSON crudo del clima actual en esas coordenadas. */
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