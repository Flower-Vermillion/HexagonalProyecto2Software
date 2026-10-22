package co.edu.poli.sw2Hexagonal.aplicacion.observer;

import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;
import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;
import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.WeatherObserver;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Observador concreto que mantiene el último clima recibido por cada drone
 * suscrito, para poder consultar su estado climático actual.
 * <p>
 * No evalúa la viabilidad de vuelo: esa decisión le corresponde al Chain of
 * Responsibility, que funciona de forma independiente al Observer.
 * </p>
 *
 * @see WeatherObserver
 */
public class FlightStatusObserver implements WeatherObserver {

    /** Ids de los drones a los que este observador está suscrito. */
    private final Set<String> dronesSuscritos = ConcurrentHashMap.newKeySet();

    /** Último clima recibido, indexado por el id del drone. */
    private final Map<String, Weather> ultimoClimaPorDrone = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void suscribir(Drone drone) {
        dronesSuscritos.add(drone.getId());
    }

    /**
     * Elimina la suscripción al drone y descarta su último clima guardado.
     *
     * @param drone drone del que se desuscribe
     */
    @Override
    public void desuscribir(Drone drone) {
        dronesSuscritos.remove(drone.getId());
        ultimoClimaPorDrone.remove(drone.getId());
    }

    /**
     * Guarda el clima recibido como el estado actual del drone, solo si este
     * observador está suscrito a él.
     *
     * @param drone drone cuyo clima cambió
     * @param clima clima actualizado
     */
    @Override
    public void notificar(Drone drone, Weather clima) {
        if (!dronesSuscritos.contains(drone.getId())) {
            return;
        }
        ultimoClimaPorDrone.put(drone.getId(), clima);
        System.out.println("[ESTADO VUELO] Drone " + drone.getId() + " actualizado: " + clima);
    }

    /**
     * Devuelve el último clima recibido por un drone.
     *
     * @param idDrone id del drone
     * @return último clima recibido, o {@code null} si aún no hay datos
     */
    public Weather getUltimoClima(String idDrone) {
        return ultimoClimaPorDrone.get(idDrone);
    }
}