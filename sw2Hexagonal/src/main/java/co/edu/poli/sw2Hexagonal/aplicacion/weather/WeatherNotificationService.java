package co.edu.poli.sw2Hexagonal.aplicacion.weather;

import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;
import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;
import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.WeatherObserver;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Coordina el patrón Observer: cada Drone es un Subject y sus
 * observadores se registran, eliminan y notifican por medio de
 * los métodos suscribir, desuscribir y notificar de WeatherObserver.
 */
public class WeatherNotificationService {

    private final GetWeatherByCityCasoDeUso getWeatherByCity;

    // id del drone -> observadores suscritos a ese drone
    private final Map<String, List<WeatherObserver>> observadoresPorDrone = new ConcurrentHashMap<>();

    public WeatherNotificationService(GetWeatherByCityCasoDeUso getWeatherByCity) {
        this.getWeatherByCity = getWeatherByCity;
    }

    /** Registro dinámico de uno o varios observers en un drone. */
    public void suscribir(Drone drone, WeatherObserver... observers) {
        List<WeatherObserver> lista = observadoresPorDrone
                .computeIfAbsent(clave(drone), k -> new CopyOnWriteArrayList<>());
        for (WeatherObserver o : observers) {
            if (o != null && !lista.contains(o)) {
                o.suscribir(drone);
                lista.add(o);
            }
        }
    }

    /** Eliminación dinámica de un observer de un drone. */
    public void desuscribir(Drone drone, WeatherObserver observer) {
        List<WeatherObserver> lista = observadoresPorDrone.get(clave(drone));
        if (lista != null && lista.remove(observer)) {
            observer.desuscribir(drone);
        }
    }

    /** Consulta la API y notifica automáticamente a los observers de cada drone. */
    public Weather actualizarClima(String ciudad, List<? extends Drone> drones) {
        Weather clima = getWeatherByCity.ejecutar(ciudad);
        for (Drone drone : drones) {
            List<WeatherObserver> lista = observadoresPorDrone.get(clave(drone));
            if (lista == null) {
                continue;
            }
            for (WeatherObserver o : lista) {
                o.notificar(drone, clima);
            }
        }
        return clima;
    }

    public List<WeatherObserver> getObservadores(Drone drone) {
        List<WeatherObserver> lista = observadoresPorDrone.get(clave(drone));
        return lista == null ? Collections.emptyList() : Collections.unmodifiableList(lista);
    }

    private String clave(Drone drone) {
        if (drone == null || drone.getId() == null) {
            throw new IllegalArgumentException("El drone debe tener un id para suscribir observadores");
        }
        return drone.getId();
    }
}