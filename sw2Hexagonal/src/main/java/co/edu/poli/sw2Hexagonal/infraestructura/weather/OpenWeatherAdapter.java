package co.edu.poli.sw2Hexagonal.infraestructura.weather;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;

/**
 * Adaptador de salida de clima actual.
 * <p>
 * Usa {@link OpenWeatherClient} con las coordenadas del {@link Weather}
 * recibido y le completa las mediciones, normalizadas a las unidades del
 * dominio (la visibilidad llega en metros y se pasa a kilómetros).
 * </p>
 * Respuesta esperada:
 * <pre>
 * { "current": { "time": "2026-10-21T12:00", "temperature_2m": 15.2,
 *                "relative_humidity_2m": 72, "wind_speed_10m": 9.4,
 *                "pressure_msl": 1021.3, "visibility": 24140.0 } }
 * </pre>
 */
public class OpenWeatherAdapter {

    private final OpenWeatherClient cliente;

    public OpenWeatherAdapter() {
        this(new OpenWeatherClient());
    }

    /**
     * @param cliente cliente HTTP de clima
     */
    public OpenWeatherAdapter(OpenWeatherClient cliente) {
        this.cliente = cliente;
    }

    /**
     * @param ubicacion {@link Weather} con latitud y longitud (salida de {@link GeoApiAdapter})
     * @return el mismo objeto con las mediciones del clima actual
     * @throws IllegalStateException si la API falla o la respuesta es inválida
     */
    public Weather obtenerClimaActual(Weather ubicacion) {
        String json;
        try {
            json = cliente.obtenerClimaActual(ubicacion.getLatitud(), ubicacion.getLongitud());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Se interrumpió la consulta del clima.", e);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "No se pudo consultar la API de clima: " + e.getMessage(), e);
        }
        return completar(ubicacion, json);
    }

    /**
     * Copia las mediciones del JSON en el {@link Weather}.
     *
     * @param clima objeto a completar
     * @param json  cuerpo de la respuesta de la API
     * @return el mismo objeto, ya completado
     * @throws IllegalStateException si la respuesta no trae el bloque "current"
     */
    public Weather completar(Weather clima, String json) {
        JsonObject raiz = JsonParser.parseString(json).getAsJsonObject();
        if (!raiz.has("current") || !raiz.get("current").isJsonObject()) {
            throw new IllegalStateException("La API de clima no devolvió datos actuales.");
        }
        JsonObject actual = raiz.getAsJsonObject("current");

        JsonElement hora = actual.get("time");
        clima.setFechaHora(hora == null || hora.isJsonNull() ? "" : hora.getAsString().replace('T', ' '));
        clima.setTemperatura(numero(actual, "temperature_2m"));
        clima.setHumedad(numero(actual, "relative_humidity_2m"));
        clima.setVelocidadViento(numero(actual, "wind_speed_10m"));
        clima.setPresion(numero(actual, "pressure_msl"));

        double visibilidadMetros = numero(actual, "visibility");
        clima.setVisibilidad(Double.isNaN(visibilidadMetros) ? Double.NaN : visibilidadMetros / 1000.0);

        return clima;
    }

    /**
     * @return el valor numérico, o {@link Double#NaN} si no viene o es null
     */
    private static double numero(JsonObject obj, String campo) {
        JsonElement e = obj.get(campo);
        return e == null || e.isJsonNull() ? Double.NaN : e.getAsDouble();
    }
}
