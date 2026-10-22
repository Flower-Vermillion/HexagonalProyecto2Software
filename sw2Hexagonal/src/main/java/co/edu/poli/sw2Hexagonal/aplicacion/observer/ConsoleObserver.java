package co.edu.poli.sw2Hexagonal.aplicacion.observer;

import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;
import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;
import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.WeatherObserver;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Observador concreto que imprime en la consola los cinco aspectos
 * climáticos de cada drone al que está suscrito.
 *
 * @see WeatherObserver
 */
public class ConsoleObserver implements WeatherObserver {

    /** Ids de los drones a los que este observador está suscrito. */
    private final Set<String> dronesSuscritos = ConcurrentHashMap.newKeySet();

    /**
     * {@inheritDoc}
     */
    @Override
    public void suscribir(Drone drone) {
        dronesSuscritos.add(drone.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void desuscribir(Drone drone) {
        dronesSuscritos.remove(drone.getId());
    }

    /**
     * Imprime en consola el id y tipo del drone junto con los cinco aspectos
     * climáticos, solo si este observador está suscrito al drone.
     *
     * @param drone drone cuyo clima cambió
     * @param clima clima actualizado
     */
    @Override
    public void notificar(Drone drone, Weather clima) {
        if (!dronesSuscritos.contains(drone.getId())) {
            return;
        }
        System.out.println("[CONSOLA] Drone " + drone.getId() + " (" + drone.getTipo() + ")");
        System.out.printf("  Temperatura: %.1f °C%n", clima.getTemperatura());
        System.out.printf("  Humedad: %.0f %%%n", clima.getHumedad());
        System.out.printf("  Viento: %.1f km/h%n", clima.getVelocidadViento());
        System.out.printf("  Presión: %.1f hPa%n", clima.getPresion());
        System.out.printf("  Visibilidad: %.0f m%n", clima.getVisibilidad());
    }
}