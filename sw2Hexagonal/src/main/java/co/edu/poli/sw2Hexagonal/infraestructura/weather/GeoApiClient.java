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
 * Cliente HTTP de la API de geocodificación de Open-Meteo.
 * <p>
 * Solo hace la petición y devuelve el JSON crudo; la conversión a objetos
 * de dominio la hace {@link GeoApiAdapter}.
 * </p>
 *
 * @see <a href="https://open-meteo.com/en/docs/geocoding-api">Open-Meteo Geocoding API</a>
 */
public class GeoApiClient {

    public static final String URL_BASE = "https://geocoding-api.open-meteo.com/v1/search";

    private final HttpClient http;

    public GeoApiClient() {
        this(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build());
    }

    /**
     * @param http cliente HTTP a usar (útil para pruebas)
     */
    public GeoApiClient(HttpClient http) {
        this.http = http;
    }

    /**
     * Busca una ciudad por nombre y devuelve la mejor coincidencia.
     *
     * @param ciudad nombre de la ciudad
     * @return cuerpo JSON de la respuesta
     * @throws IOException          si falla la conexión o el servicio responde con error
     * @throws InterruptedException si se interrumpe la petición
     */
    public String buscar(String ciudad) throws IOException, InterruptedException {
        String url = URL_BASE
                + "?name=" + URLEncoder.encode(ciudad, StandardCharsets.UTF_8)
                + "&count=1&language=es&format=json";

        HttpRequest peticion = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> respuesta = http.send(peticion,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (respuesta.statusCode() != 200) {
            throw new IOException("La API de geocodificación respondió con código "
                    + respuesta.statusCode() + ": " + respuesta.body());
        }
        return respuesta.body();
    }
}
