package co.edu.poli.sw2Hexagonal.aplicacion.observer;

import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;
import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;
import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.WeatherObserver;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Observador concreto que registra en el log del sistema
 * ({@link java.util.logging.Logger}) las suscripciones y cada notificación
 * climática de los drones a los que está suscrito.
 *
 * @see WeatherObserver
 */
public class LoggerObserver implements WeatherObserver {

    /** Logger de la clase. */
    private static final Logger LOG = Logger.getLogger(LoggerObserver.class.getName());

    /** Ids de los drones a los que este observador está suscrito. */
    private final Set<String> dronesSuscritos = ConcurrentHashMap.newKeySet();

    /**
     * Suscribe el observador al drone y deja constancia en el log.
     *
     * @param drone drone al que se suscribe
     */
    @Override
    public void suscribir(Drone drone) {
        dronesSuscritos.add(drone.getId());
        LOG.info("LoggerObserver suscrito al drone " + drone.getId());
    }

    /**
     * Elimina la suscripción al drone y deja constancia en el log.
     *
     * @param drone drone del que se desuscribe
     */
    @Override
    public void desuscribir(Drone drone) {
        dronesSuscritos.remove(drone.getId());
        LOG.info("LoggerObserver desuscrito del drone " + drone.getId());
    }

    /**
     * Registra en el log los cinco aspectos climáticos del drone, solo si
     * este observador está suscrito a él.
     *
     * @param drone drone cuyo clima cambió
     * @param clima clima actualizado
     */
    @Override
    public void notificar(Drone drone, Weather clima) {
        if (!dronesSuscritos.contains(drone.getId())) {
            return;
        }
        LOG.info("Drone " + drone.getId() + " -> " + clima);
    }
}