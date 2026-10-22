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
 * Servicio de aplicación que coordina el patrón Observer del clima.
 * <p>
 * Cada {@link Drone} es un Subject. Como la clase {@code Drone} no se
 * modifica, este servicio guarda, para cada drone, la lista de observadores
 * suscritos (variante del Observer con gestor de cambios, o ChangeManager).
 * </p>
 * <p>
 * Responsabilidades:
 * </p>
 * <ul>
 *   <li>Registrar y eliminar observadores de un drone en tiempo de ejecución.</li>
 *   <li>Consultar el clima mediante {@link GetWeatherByCityCasoDeUso}.</li>
 *   <li>Notificar automáticamente a todos los observadores de cada drone
 *       con los cinco aspectos climáticos.</li>
 * </ul>
 * <p>
 * Los drones se identifican por su {@code id}, de modo que las suscripciones
 * se conservan aunque la lista de drones se recargue desde la base de datos.
 * </p>
 *
 * @see WeatherObserver
 */
public class WeatherNotificationService {

    /** Caso de uso que obtiene el clima de una ciudad. */
    private final GetWeatherByCityCasoDeUso getWeatherByCity;

    /** Observadores suscritos a cada drone, indexados por el id del drone. */
    private final Map<String, List<WeatherObserver>> observadoresPorDrone = new ConcurrentHashMap<>();

    /**
     * Crea el servicio.
     *
     * @param getWeatherByCity caso de uso para consultar el clima
     */
    public WeatherNotificationService(GetWeatherByCityCasoDeUso getWeatherByCity) {
        this.getWeatherByCity = getWeatherByCity;
    }

    /**
     * Suscribe uno o varios observadores a un drone. Los observadores que ya
     * estaban suscritos se ignoran, para no duplicar notificaciones.
     *
     * @param drone     drone (Subject) al que se suscriben
     * @param observers observadores a suscribir
     * @throws IllegalArgumentException si el drone es nulo o no tiene id
     */
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

    /**
     * Elimina la suscripción de un observador a un drone.
     *
     * @param drone    drone (Subject) del que se desuscribe
     * @param observer observador a eliminar
     * @throws IllegalArgumentException si el drone es nulo o no tiene id
     */
    public void desuscribir(Drone drone, WeatherObserver observer) {
        List<WeatherObserver> lista = observadoresPorDrone.get(clave(drone));
        if (lista != null && lista.remove(observer)) {
            observer.desuscribir(drone);
        }
    }

    /**
     * Consulta el clima de una ciudad y notifica automáticamente a todos los
     * observadores suscritos a cada drone de la lista. Los drones sin
     * observadores no generan notificaciones.
     *
     * @param ciudad nombre de la ciudad a consultar
     * @param drones drones (Subjects) que reciben la actualización
     * @return el clima obtenido
     * @throws IllegalArgumentException si la ciudad es inválida
     * @throws IllegalStateException    si falla la comunicación con la API
     */
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

    /**
     * Devuelve los observadores suscritos a un drone.
     *
     * @param drone drone a consultar
     * @return lista de solo lectura; vacía si el drone no tiene observadores
     */
    public List<WeatherObserver> getObservadores(Drone drone) {
        List<WeatherObserver> lista = observadoresPorDrone.get(clave(drone));
        return lista == null ? Collections.emptyList() : Collections.unmodifiableList(lista);
    }

    /**
     * Obtiene la clave con la que se indexa un drone.
     *
     * @param drone drone
     * @return el id del drone
     * @throws IllegalArgumentException si el drone es nulo o no tiene id
     */
    private String clave(Drone drone) {
        if (drone == null || drone.getId() == null) {
            throw new IllegalArgumentException("El drone debe tener un id para suscribir observadores");
        }
        return drone.getId();
    }
}