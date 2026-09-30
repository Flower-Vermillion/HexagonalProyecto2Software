package co.edu.poli.sw2Hexagonal.aplicacion.servicio;

import co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada.EliminarDroneCasosDeUso;
import co.edu.poli.sw2Hexagonal.aplicacion.puerto.salida.DroneRepository;

/**
 * Servicio de aplicación — implementa el caso de uso de eliminar un dron.
 */
public class EliminarDroneServicio implements EliminarDroneCasosDeUso {

    private final DroneRepository droneRepository;

    public EliminarDroneServicio(DroneRepository droneRepository) {
        this.droneRepository = droneRepository;
    }

    @Override
    public void eliminar(String id) throws Exception {
        if (id == null || id.trim().isEmpty()) {
            throw new Exception("El id del dron es obligatorio.");
        }
        if (droneRepository.buscarPorId(id) == null) {
            throw new Exception("No existe un dron con el id " + id + ".");
        }

        droneRepository.eliminar(id);
    }
}