package co.edu.poli.sw2Hexagonal.infraestructura.weather;

import co.edu.poli.sw2Hexagonal.aplicacion.weather.GetWeatherByCityCasoDeUso;
import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Adapta la API de clima actual al puerto CurrentWeatherPort. */
public class OpenWeatherAdapter implements GetWeatherByCityCasoDeUso.CurrentWeatherPort {

    private static final Pattern BLOQUE_CURRENT = Pattern.compile("\"current\"\\s*:\\s*\\{([^}]*)}");

    private final OpenWeatherClient client;

    public OpenWeatherAdapter(OpenWeatherClient client) {
        this.client = client;
    }

    @Override
    public Weather obtenerClimaActual(double latitud, double longitud) {
        String json = client.consultarActual(latitud, longitud);

        Matcher m = BLOQUE_CURRENT.matcher(json); // solo "current", no "current_units"
        if (!m.find()) {
            throw new IllegalStateException("La respuesta no contiene el bloque 'current'");
        }
        String current = m.group(1);

        return new Weather(
                GeoApiAdapter.extraerNumero(current, "temperature_2m"),
                GeoApiAdapter.extraerNumero(current, "relative_humidity_2m"),
                GeoApiAdapter.extraerNumero(current, "wind_speed_10m"),
                GeoApiAdapter.extraerNumero(current, "surface_pressure"),
                GeoApiAdapter.extraerNumero(current, "visibility")
        );
    }
}