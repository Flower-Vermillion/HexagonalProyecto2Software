package co.edu.poli.sw2Hexagonal.aplicacion.observer;

import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;
import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;
import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.WeatherObserver;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Muestra en consola los cinco aspectos climáticos recibidos. */
public class ConsoleObserver implements WeatherObserver {

    private final Set<String> dronesSuscritos = ConcurrentHashMap.newKeySet();

    @Override
    public void suscribir(Drone drone) {
        dronesSuscritos.add(drone.getId());
    }

    @Override
    public void desuscribir(Drone drone) {
        dronesSuscritos.remove(drone.getId());
    }

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