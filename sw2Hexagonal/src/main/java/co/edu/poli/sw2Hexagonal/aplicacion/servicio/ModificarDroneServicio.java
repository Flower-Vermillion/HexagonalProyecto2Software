package co.edu.poli.sw2Hexagonal.aplicacion.servicio;

import co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada.ModificarDroneCasosDeUso;
import co.edu.poli.sw2Hexagonal.aplicacion.puerto.salida.DroneRepository;
import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;

/**
 * Servicio de aplicación — implementa el caso de uso de modificar un dron.
 */
public class ModificarDroneServicio implements ModificarDroneCasosDeUso {

    private final DroneRepository droneRepository;

    public ModificarDroneServicio(DroneRepository droneRepository) {
        this.droneRepository = droneRepository;
    }

    @Override
    public void modificar(Drone drone) throws Exception {
        if (drone == null) {
            throw new Exception("El dron no puede ser nulo.");
        }
        if (esVacio(drone.getId())) {
            throw new Exception("El id del dron es obligatorio.");
        }
        if (esVacio(drone.getSerial())) {
            throw new Exception("El serial del dron es obligatorio.");
        }
        if (esVacio(drone.getModelo())) {
            throw new Exception("El modelo del dron es obligatorio.");
        }
        if (esVacio(drone.getFabricante())) {
            throw new Exception("El fabricante del dron es obligatorio.");
        }
        if (drone.getPeso() <= 0) {
            throw new Exception("El peso del dron debe ser mayor que cero.");
        }
        if (droneRepository.buscarPorId(drone.getId()) == null) {
            throw new Exception("No existe un dron con el id " + drone.getId() + ".");
        }

        droneRepository.actualizar(drone);
    }

    private boolean esVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}