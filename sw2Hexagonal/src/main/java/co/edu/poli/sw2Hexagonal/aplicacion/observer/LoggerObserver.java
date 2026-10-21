package co.edu.poli.sw2Hexagonal.aplicacion.observer;

import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;
import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;
import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.WeatherObserver;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/** Registra cada notificación climática en el log. */
public class LoggerObserver implements WeatherObserver {

    private static final Logger LOG = Logger.getLogger(LoggerObserver.class.getName());
    private final Set<String> dronesSuscritos = ConcurrentHashMap.newKeySet();

    @Override
    public void suscribir(Drone drone) {
        dronesSuscritos.add(drone.getId());
        LOG.info("LoggerObserver suscrito al drone " + drone.getId());
    }

    @Override
    public void desuscribir(Drone drone) {
        dronesSuscritos.remove(drone.getId());
        LOG.info("LoggerObserver desuscrito del drone " + drone.getId());
    }

    @Override
    public void notificar(Drone drone, Weather clima) {
        if (!dronesSuscritos.contains(drone.getId())) {
            return;
        }
        LOG.info("Drone " + drone.getId() + " -> " + clima);
    }
}