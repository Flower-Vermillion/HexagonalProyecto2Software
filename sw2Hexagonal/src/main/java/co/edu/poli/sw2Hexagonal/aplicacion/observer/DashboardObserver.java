package co.edu.poli.sw2Hexagonal.aplicacion.observer;

import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;
import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;
import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.WeatherObserver;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/** Envía un resumen a la UI (ventana emergente) sin depender de JavaFX. */
public class DashboardObserver implements WeatherObserver {

    private final Set<String> dronesSuscritos = ConcurrentHashMap.newKeySet();
    private final Consumer<String> vista;

    public DashboardObserver(Consumer<String> vista) {
        this.vista = vista;
    }

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
        vista.accept(String.format(
                "Drone %s | %.1f°C | %.0f%% | %.1f km/h | %.1f hPa | %.0f m",
                drone.getId(),
                clima.getTemperatura(), clima.getHumedad(), clima.getVelocidadViento(),
                clima.getPresion(), clima.getVisibilidad()));
    }
}