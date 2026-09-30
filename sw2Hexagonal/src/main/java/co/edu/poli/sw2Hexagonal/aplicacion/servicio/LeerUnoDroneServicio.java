package co.edu.poli.sw2Hexagonal.aplicacion.servicio;

import co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada.LeerUnoDroneCasosDeUso;
import co.edu.poli.sw2Hexagonal.aplicacion.puerto.salida.DroneRepository;
import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;

/**
 * Servicio de aplicación — implementa el caso de uso de leer un dron por id.
 */
public class LeerUnoDroneServicio implements LeerUnoDroneCasosDeUso {

    private final DroneRepository droneRepository;

    public LeerUnoDroneServicio(DroneRepository droneRepository) {
        this.droneRepository = droneRepository;
    }

    @Override
    public Drone leerUno(String id) throws Exception {
        if (id == null || id.trim().isEmpty()) {
            throw new Exception("El id del dron es obligatorio.");
        }
        return droneRepository.buscarPorId(id);
    }
}