package co.edu.poli.sw2Hexagonal.aplicacion.observer;

import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;
import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;
import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.WeatherObserver;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Observador concreto que envía a la interfaz gráfica un resumen de una línea
 * con los cinco aspectos climáticos de cada drone suscrito.
 * <p>
 * No depende de JavaFX: entrega el texto a un {@link Consumer} que recibe por
 * constructor. Así la capa de aplicación queda desacoplada de la UI, que
 * decide cómo mostrarlo (en este proyecto, en una ventana emergente).
 * </p>
 *
 * @see WeatherObserver
 */
public class DashboardObserver implements WeatherObserver {

    /** Ids de los drones a los que este observador está suscrito. */
    private final Set<String> dronesSuscritos = ConcurrentHashMap.newKeySet();

    /** Destino del resumen (la vista). */
    private final Consumer<String> vista;

    /**
     * Crea el observador.
     *
     * @param vista función que recibe cada resumen y lo muestra en la UI
     */
    public DashboardObserver(Consumer<String> vista) {
        this.vista = vista;
    }

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
     * Construye un resumen con el id del drone y los cinco aspectos
     * climáticos y lo envía a la vista, solo si este observador está
     * suscrito al drone.
     *
     * @param drone drone cuyo clima cambió
     * @param clima clima actualizado
     */
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