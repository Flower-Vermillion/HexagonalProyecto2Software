package co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;
import co.edu.poli.sw2Hexagonal.dominio.modelo.state.DroneContext;

/**
 * Caso de uso que gestiona el estado operativo de los drones.
 * <p>
 * Conserva un {@link DroneContext} por cada dron y le delega las operaciones.
 * No contiene reglas de transición. No usa persistencia: los contextos se
 * indexan por el id del dron y se manejan en memoria, así que al reiniciar la
 * aplicación todos los drones vuelven a APAGADO.
 * </p>
 */
public class ManageDroneStateCasosDeUso {

    /** Contexto de cada dron, indexado por su id. */
    private final Map<String, DroneContext> contextos = new ConcurrentHashMap<>();

    /**
     * Enciende el dron.
     *
     * @param drone dron
     * @return nombre del nuevo estado
     * @throws IllegalStateException si el estado actual no lo permite
     */
    public String encender(Drone drone) {
        DroneContext contexto = contexto(drone);
        contexto.encender();
        return contexto.getEstado().getNombre();
    }

    /**
     * Apaga el dron.
     *
     * @param drone dron
     * @return nombre del nuevo estado
     * @throws IllegalStateException si el estado actual no lo permite
     */
    public String apagar(Drone drone) {
        DroneContext contexto = contexto(drone);
        contexto.apagar();
        return contexto.getEstado().getNombre();
    }

    /**
     * Hace despegar el dron.
     *
     * @param drone dron
     * @return nombre del nuevo estado
     * @throws IllegalStateException si el estado actual no lo permite
     */
    public String despegar(Drone drone) {
        DroneContext contexto = contexto(drone);
        contexto.despegar();
        return contexto.getEstado().getNombre();
    }

    /**
     * Inicia el aterrizaje del dron.
     *
     * @param drone dron
     * @return nombre del nuevo estado
     * @throws IllegalStateException si el estado actual no lo permite
     */
    public String iniciarAterrizaje(Drone drone) {
        DroneContext contexto = contexto(drone);
        contexto.iniciarAterrizaje();
        return contexto.getEstado().getNombre();
    }

    /**
     * Finaliza el aterrizaje del dron.
     *
     * @param drone dron
     * @return nombre del nuevo estado
     * @throws IllegalStateException si el estado actual no lo permite
     */
    public String finalizarAterrizaje(Drone drone) {
        DroneContext contexto = contexto(drone);
        contexto.finalizarAterrizaje();
        return contexto.getEstado().getNombre();
    }

    /**
     * Consulta el estado operativo de un dron.
     *
     * @param drone dron
     * @return nombre del estado actual
     */
    public String obtenerEstado(Drone drone) {
        return contexto(drone).getEstado().getNombre();
    }

    /**
     * Descarta los contextos de los drones que ya no están registrados. Así,
     * un dron nuevo creado con el id de uno eliminado inicia APAGADO.
     *
     * @param drones drones registrados actualmente
     */
    public void sincronizar(List<? extends Drone> drones) {
        Set<String> vigentes = drones.stream().map(Drone::getId).collect(Collectors.toSet());
        contextos.keySet().retainAll(vigentes);
    }

    /**
     * Obtiene el contexto de un dron. Si no existe, lo crea.
     *
     * @param drone dron
     * @return contexto del dron
     */
    private DroneContext contexto(Drone drone) {
        return contextos.computeIfAbsent(drone.getId(), id -> new DroneContext(drone));
    }
}