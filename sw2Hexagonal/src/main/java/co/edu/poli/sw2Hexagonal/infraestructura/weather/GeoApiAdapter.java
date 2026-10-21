package co.edu.poli.sw2Hexagonal.infraestructura.weather;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;

/**
 * Adaptador de salida de geocodificación.
 * <p>
 * Usa {@link GeoApiClient} y traduce su JSON a un {@link Weather} con la
 * ubicación (ciudad, región, país, latitud y longitud). El resto de los
 * datos los completa después {@link OpenWeatherAdapter}.
 * </p>
 * Respuesta esperada:
 * <pre>
 * { "results": [ { "name": "Bogotá", "latitude": 4.60971, "longitude": -74.08175,
 *                  "country": "Colombia", "admin1": "Bogota D.C." } ] }
 * </pre>
 */
public class GeoApiAdapter {

    private final GeoApiClient cliente;

    public GeoApiAdapter() {
        this(new GeoApiClient());
    }

    /**
     * @param cliente cliente HTTP de geocodificación
     */
    public GeoApiAdapter(GeoApiClient cliente) {
        this.cliente = cliente;
    }

    /**
     * @param ciudad nombre de la ciudad
     * @return un {@link Weather} con la ubicación encontrada
     * @throws IllegalStateException si la ciudad no existe o la API falla
     */
    public Weather buscarCiudad(String ciudad) {
        String json;
        try {
            json = cliente.buscar(ciudad);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Se interrumpió la consulta de geocodificación.", e);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "No se pudo consultar la API de geocodificación: " + e.getMessage(), e);
        }
        return convertir(json, ciudad);
    }

    /**
     * Convierte la respuesta JSON de la API en un {@link Weather} con la ubicación.
     *
     * @param json   cuerpo de la respuesta
     * @param ciudad nombre buscado (para el mensaje de error)
     * @return ubicación encontrada
     * @throws IllegalStateException si la respuesta no trae resultados
     */
    public Weather convertir(String json, String ciudad) {
        JsonObject raiz = JsonParser.parseString(json).getAsJsonObject();
        JsonArray resultados = raiz.has("results") && raiz.get("results").isJsonArray()
                ? raiz.getAsJsonArray("results")
                : new JsonArray();

        if (resultados.isEmpty()) {
            throw new IllegalStateException("No se encontró la ciudad \"" + ciudad + "\".");
        }

        JsonObject r = resultados.get(0).getAsJsonObject();
        return new Weather(
                texto(r, "name"),
                texto(r, "admin1"),
                texto(r, "country"),
                r.get("latitude").getAsDouble(),
                r.get("longitude").getAsDouble());
    }

    private static String texto(JsonObject obj, String campo) {
        JsonElement e = obj.get(campo);
        return e == null || e.isJsonNull() ? "" : e.getAsString();
    }
}
