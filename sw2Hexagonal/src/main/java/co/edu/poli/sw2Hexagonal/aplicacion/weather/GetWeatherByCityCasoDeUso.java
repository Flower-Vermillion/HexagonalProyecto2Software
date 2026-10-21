package co.edu.poli.sw2Hexagonal.aplicacion.weather;

import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;

/**
 * Caso de uso: obtener el clima actual de una ciudad.
 * Primero geocodifica y luego consulta el clima actual.
 */
public class GetWeatherByCityCasoDeUso {

    /** Lo implementa GeoApiAdapter. */
    public interface GeocodingPort {
        /** @return {latitud, longitud} */
        double[] obtenerCoordenadas(String ciudad);
    }

    /** Lo implementa OpenWeatherAdapter. */
    public interface CurrentWeatherPort {
        Weather obtenerClimaActual(double latitud, double longitud);
    }

    private final GeocodingPort geocoding;
    private final CurrentWeatherPort currentWeather;

    public GetWeatherByCityCasoDeUso(GeocodingPort geocoding, CurrentWeatherPort currentWeather) {
        this.geocoding = geocoding;
        this.currentWeather = currentWeather;
    }

    public Weather ejecutar(String ciudad) {
        if (ciudad == null || ciudad.isBlank()) {
            throw new IllegalArgumentException("Debe indicar una ciudad");
        }
        double[] coordenadas = geocoding.obtenerCoordenadas(ciudad);
        return currentWeather.obtenerClimaActual(coordenadas[0], coordenadas[1]);
    }
}