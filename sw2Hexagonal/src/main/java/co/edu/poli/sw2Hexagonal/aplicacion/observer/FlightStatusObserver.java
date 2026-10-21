package co.edu.poli.sw2Hexagonal.aplicacion.observer;

import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;
import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;
import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.WeatherObserver;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Mantiene el último clima recibido por cada drone suscrito. */
public class FlightStatusObserver implements WeatherObserver {

    private final Set<String> dronesSuscritos = ConcurrentHashMap.newKeySet();
    private final Map<String, Weather> ultimoClimaPorDrone = new ConcurrentHashMap<>();

    @Override
    public void suscribir(Drone drone) {
        dronesSuscritos.add(drone.getId());
    }

    @Override
    public void desuscribir(Drone drone) {
        dronesSuscritos.remove(drone.getId());
        ultimoClimaPorDrone.remove(drone.getId());
    }

    @Override
    public void notificar(Drone drone, Weather clima) {
        if (!dronesSuscritos.contains(drone.getId())) {
            return;
        }
        ultimoClimaPorDrone.put(drone.getId(), clima);
        System.out.println("[ESTADO VUELO] Drone " + drone.getId() + " actualizado: " + clima);
    }

    public Weather getUltimoClima(String idDrone) {
        return ultimoClimaPorDrone.get(idDrone);
    }
}