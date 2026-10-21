package co.edu.poli.sw2Hexagonal.infraestructura.weather;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;

/**
 * Cliente HTTP de la API de clima actual de Open-Meteo ({@code /v1/forecast}).
 * <p>
 * Pide únicamente las variables que necesita la cadena de validaciones:
 * </p>
 * <ul>
 *   <li>{@code temperature_2m} (°C)</li>
 *   <li>{@code relative_humidity_2m} (%)</li>
 *   <li>{@code wind_speed_10m} (km/h)</li>
 *   <li>{@code pressure_msl}: presión a nivel del mar (hPa)</li>
 *   <li>{@code visibility} (metros)</li>
 * </ul>
 *
 * @see <a href="https://open-meteo.com/en/docs">Open-Meteo Forecast API</a>
 */
public class OpenWeatherClient {

    public static final String URL_BASE = "https://api.open-meteo.com/v1/forecast";

    public static final String VARIABLES =
            "temperature_2m,relative_humidity_2m,wind_speed_10m,pressure_msl,visibility";

    private final HttpClient http;

    public OpenWeatherClient() {
        this(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build());
    }

    /**
     * @param http cliente HTTP a usar (útil para pruebas)
     */
    public OpenWeatherClient(HttpClient http) {
        this.http = http;
    }

    /**
     * @param latitud  latitud en grados decimales
     * @param longitud longitud en grados decimales
     * @return cuerpo JSON de la respuesta
     * @throws IOException          si falla la conexión o el servicio responde con error
     * @throws InterruptedException si se interrumpe la petición
     */
    public String obtenerClimaActual(double latitud, double longitud)
            throws IOException, InterruptedException {

        // Locale.ROOT para que el separador decimal sea siempre el punto
        String url = String.format(Locale.ROOT,
                "%s?latitude=%.4f&longitude=%.4f&current=%s&wind_speed_unit=kmh&timezone=auto",
                URL_BASE, latitud, longitud, VARIABLES);

        HttpRequest peticion = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> respuesta = http.send(peticion,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (respuesta.statusCode() != 200) {
            throw new IOException("La API de clima respondió con código "
                    + respuesta.statusCode() + ": " + respuesta.body());
        }
        return respuesta.body();
    }
}
