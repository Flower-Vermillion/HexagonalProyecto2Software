package co.edu.poli.sw2Hexagonal.aplicacion.weather;

import java.util.function.Function;

import co.edu.poli.sw2Hexagonal.dominio.modelo.chain.WeatherHandler;
import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;

/**
 * Caso de uso: consultar el clima de una ciudad y evaluar si es apto para
 * volar drones.
 * <ol>
 *   <li>Geocodifica la ciudad (nombre → latitud/longitud).</li>
 *   <li>Consulta el clima actual con esas coordenadas.</li>
 *   <li>Evalúa el clima con la cadena de responsabilidad del dominio.</li>
 * </ol>
 * <p>
 * Los dos servicios externos llegan como funciones, que hacen el papel de
 * puertos de salida. Así este caso de uso no importa nada de
 * infraestructura (HTTP, JSON) ni de la interfaz (JavaFX). Quien hace el
 * wiring ({@code App}) le pasa {@code GeoApiAdapter::buscarCiudad} y
 * {@code OpenWeatherAdapter::obtenerClimaActual}.
 * </p>
 */
public class GetWeatherByCityCasoDeUso {

    private final Function<String, Weather> geocodificar;
    private final Function<Weather, Weather> consultarClima;

    /**
     * @param geocodificar   recibe el nombre de la ciudad y devuelve un
     *                       {@link Weather} con ciudad, país y coordenadas
     * @param consultarClima recibe ese {@link Weather} y lo devuelve con las
     *                       mediciones del clima actual
     */
    public GetWeatherByCityCasoDeUso(Function<String, Weather> geocodificar,
                                     Function<Weather, Weather> consultarClima) {
        this.geocodificar = geocodificar;
        this.consultarClima = consultarClima;
    }

    /**
     * @param ciudad nombre de la ciudad
     * @return el clima consultado, ya evaluado por la cadena
     * @throws Exception si la ciudad está vacía, no existe o falla algún servicio
     */
    public Weather ejecutar(String ciudad) throws Exception {
        if (ciudad == null || ciudad.trim().isEmpty()) {
            throw new Exception("Debes escribir el nombre de una ciudad.");
        }

        Weather clima;
        try {
            Weather ubicacion = geocodificar.apply(ciudad.trim());
            clima = consultarClima.apply(ubicacion);
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage(), e);
        }

        return WeatherHandler.evaluar(clima);
    }
}
