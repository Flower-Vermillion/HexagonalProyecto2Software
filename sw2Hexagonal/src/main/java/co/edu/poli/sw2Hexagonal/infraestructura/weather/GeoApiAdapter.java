package co.edu.poli.sw2Hexagonal.infraestructura.weather;

import co.edu.poli.sw2Hexagonal.aplicacion.weather.GetWeatherByCityCasoDeUso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adaptador de salida que implementa
 * {@link GetWeatherByCityCasoDeUso.GeocodingPort} usando la API de
 * geocodificación de Open-Meteo.
 * <p>
 * Traduce el JSON que devuelve {@link GeoApiClient} a un par de coordenadas.
 * </p>
 */
public class GeoApiAdapter implements GetWeatherByCityCasoDeUso.GeocodingPort {

    /** Cliente HTTP de la API de geocodificación. */
    private final GeoApiClient client;

    /**
     * Crea el adaptador.
     *
     * @param client cliente HTTP de geocodificación
     */
    public GeoApiAdapter(GeoApiClient client) {
        this.client = client;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] obtenerCoordenadas(String ciudad) {
        String json = client.buscar(ciudad);
        if (!json.contains("\"results\"")) {
            throw new IllegalArgumentException("No se encontró la ciudad: " + ciudad);
        }
        return new double[]{
                extraerNumero(json, "latitude"),
                extraerNumero(json, "longitude")
        };
    }

    /**
     * Extrae el valor numérico de un campo de un JSON plano. Toma la primera
     * aparición del campo.
     *
     * @param json  texto JSON
     * @param campo nombre del campo, por ejemplo {@code latitude}
     * @return valor numérico del campo
     * @throws IllegalStateException si el campo no está en el JSON
     */
    static double extraerNumero(String json, String campo) {
        Pattern p = Pattern.compile("\"" + campo + "\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?(?:[eE][-+]?\\d+)?)");
        Matcher m = p.matcher(json);
        if (!m.find()) {
            throw new IllegalStateException("Campo '" + campo + "' no encontrado en la respuesta");
        }
        return Double.parseDouble(m.group(1));
    }
}