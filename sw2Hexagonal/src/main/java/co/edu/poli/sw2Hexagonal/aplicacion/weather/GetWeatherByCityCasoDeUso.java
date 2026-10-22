package co.edu.poli.sw2Hexagonal.aplicacion.weather;

import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;

/**
 * Caso de uso que obtiene el clima actual de una ciudad.
 * <p>
 * Respeta el orden que exige el negocio:
 * </p>
 * <ol>
 *   <li>Consulta la API de geocodificación para obtener las coordenadas de
 *       la ciudad.</li>
 *   <li>Consulta la API de clima actual con esas coordenadas.</li>
 * </ol>
 * <p>
 * Para no depender de la infraestructura, define mediante interfaces
 * anidadas lo que necesita ({@link GeocodingPort} y
 * {@link CurrentWeatherPort}). Los adaptadores de infraestructura las
 * implementan, de modo que las dependencias siempre apuntan hacia adentro.
 * </p>
 *
 * @see WeatherNotificationService
 */
public class GetWeatherByCityCasoDeUso {

    /**
     * Puerto para obtener las coordenadas geográficas de una ciudad.
     * Lo implementa el adaptador de la API de geocodificación.
     */
    public interface GeocodingPort {

        /**
         * Busca una ciudad y devuelve sus coordenadas.
         *
         * @param ciudad nombre de la ciudad a buscar
         * @return arreglo de dos posiciones: {@code [latitud, longitud]}
         * @throws IllegalArgumentException si la ciudad no existe
         */
        double[] obtenerCoordenadas(String ciudad);
    }

    /**
     * Puerto para obtener el clima actual en unas coordenadas.
     * Lo implementa el adaptador de la API de clima.
     */
    public interface CurrentWeatherPort {

        /**
         * Consulta el clima actual en las coordenadas indicadas.
         *
         * @param latitud  latitud en grados decimales
         * @param longitud longitud en grados decimales
         * @return clima actual con sus cinco aspectos
         */
        Weather obtenerClimaActual(double latitud, double longitud);
    }

    /** Puerto de geocodificación. */
    private final GeocodingPort geocoding;

    /** Puerto de clima actual. */
    private final CurrentWeatherPort currentWeather;

    /**
     * Crea el caso de uso con sus dos puertos.
     *
     * @param geocoding      implementación del puerto de geocodificación
     * @param currentWeather implementación del puerto de clima actual
     */
    public GetWeatherByCityCasoDeUso(GeocodingPort geocoding, CurrentWeatherPort currentWeather) {
        this.geocoding = geocoding;
        this.currentWeather = currentWeather;
    }

    /**
     * Obtiene el clima actual de una ciudad: primero geocodifica y luego
     * consulta el clima.
     *
     * @param ciudad nombre de la ciudad
     * @return clima actual de la ciudad
     * @throws IllegalArgumentException si la ciudad está vacía o no se encuentra
     * @throws IllegalStateException    si falla la comunicación con alguna API
     */
    public Weather ejecutar(String ciudad) {
        if (ciudad == null || ciudad.isBlank()) {
            throw new IllegalArgumentException("Debe indicar una ciudad");
        }
        double[] coordenadas = geocoding.obtenerCoordenadas(ciudad);
        return currentWeather.obtenerClimaActual(coordenadas[0], coordenadas[1]);
    }
}