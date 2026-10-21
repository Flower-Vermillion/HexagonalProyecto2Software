package co.edu.poli.sw2Hexagonal.infraestructura.weather;

import co.edu.poli.sw2Hexagonal.aplicacion.weather.GetWeatherByCityCasoDeUso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Adapta la API de geocodificación al puerto GeocodingPort. */
public class GeoApiAdapter implements GetWeatherByCityCasoDeUso.GeocodingPort {

    private final GeoApiClient client;

    public GeoApiAdapter(GeoApiClient client) {
        this.client = client;
    }

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

    /** Extrae el valor numérico de un campo del JSON. */
    static double extraerNumero(String json, String campo) {
        Pattern p = Pattern.compile("\"" + campo + "\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?(?:[eE][-+]?\\d+)?)");
        Matcher m = p.matcher(json);
        if (!m.find()) {
            throw new IllegalStateException("Campo '" + campo + "' no encontrado en la respuesta");
        }
        return Double.parseDouble(m.group(1));
    }
}