package co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada;

import java.util.List;

import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;

/**
 * Puerto de entrada — caso de uso: listar todos los drones.
 * Define el contrato que el adaptador de entrada (UI) usa para
 * invocar la lógica de aplicación sin conocer su implementación.
 */
public interface LeerTodosDroneCasosDeUso {

    /**
     * Retorna todos los drones registrados.
     *
     * @return lista de drones, vacía si no hay registros
     * @throws Exception si ocurre un error durante la consulta
     */
    List<Drone> leerTodos() throws Exception;
}