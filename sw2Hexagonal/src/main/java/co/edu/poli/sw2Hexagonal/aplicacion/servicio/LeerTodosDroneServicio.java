package co.edu.poli.sw2Hexagonal.aplicacion.servicio;

import java.util.List;

import co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada.LeerTodosDroneCasosDeUso;
import co.edu.poli.sw2Hexagonal.aplicacion.puerto.salida.DroneRepository;
import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;

/**
 * Servicio de aplicación — implementa el caso de uso de listar todos los drones.
 */
public class LeerTodosDroneServicio implements LeerTodosDroneCasosDeUso {

    private final DroneRepository droneRepository;

    public LeerTodosDroneServicio(DroneRepository droneRepository) {
        this.droneRepository = droneRepository;
    }

    @Override
    public List<Drone> leerTodos() throws Exception {
        return droneRepository.buscarTodos();
    }
}