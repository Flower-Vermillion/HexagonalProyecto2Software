package co.edu.poli.sw2Hexagonal.infraestructura.weather;

import co.edu.poli.sw2Hexagonal.aplicacion.weather.GetWeatherByCityCasoDeUso;
import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adaptador de salida que implementa
 * {@link GetWeatherByCityCasoDeUso.CurrentWeatherPort} usando la API de
 * clima actual de Open-Meteo.
 * <p>
 * Traduce el bloque {@code current} del JSON que devuelve
 * {@link OpenWeatherClient} al objeto de dominio {@link Weather}.
 * </p>
 */
public class OpenWeatherAdapter implements GetWeatherByCityCasoDeUso.CurrentWeatherPort {

    /**
     * Patrón que captura el contenido del bloque {@code current}, sin tomar
     * el bloque {@code current_units}.
     */
    private static final Pattern BLOQUE_CURRENT = Pattern.compile("\"current\"\\s*:\\s*\\{([^}]*)}");

    /** Cliente HTTP de la API de clima. */
    private final OpenWeatherClient client;

    /**
     * Crea el adaptador.
     *
     * @param client cliente HTTP de clima actual
     */
    public OpenWeatherAdapter(OpenWeatherClient client) {
        this.client = client;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException si la respuesta no trae el bloque
     *                               {@code current} o le falta alguna variable
     */
    @Override
    public Weather obtenerClimaActual(double latitud, double longitud) {
        String json = client.consultarActual(latitud, longitud);

        Matcher m = BLOQUE_CURRENT.matcher(json);
        if (!m.find()) {
            throw new IllegalStateException("La respuesta no contiene el bloque 'current'");
        }
        String current = m.group(1);

        return new Weather(
                GeoApiAdapter.extraerNumero(current, "temperature_2m"),
                GeoApiAdapter.extraerNumero(current, "relative_humidity_2m"),
                GeoApiAdapter.extraerNumero(current, "wind_speed_10m"),
                GeoApiAdapter.extraerNumero(current, "pressure_msl"),
                GeoApiAdapter.extraerNumero(current, "visibility")
        );
    }
}