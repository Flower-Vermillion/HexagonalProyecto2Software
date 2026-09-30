package co.edu.poli.sw2Hexagonal.aplicacion.puerto.salida;

import java.util.List;

import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;

/**
 * Puerto de salida — repositorio de drones.
 * Define el contrato que la capa de aplicación usa para persistir y
 * consultar drones, sin conocer la tecnología de almacenamiento
 * (MySQL, memoria, etc.). El adaptador de salida lo implementa.
 */
public interface DroneRepository {

    /**
     * Persiste un nuevo dron.
     *
     * @param drone dron a guardar (concreto: de agricultura o vigilancia)
     * @throws Exception si ocurre un error al guardar o el id ya existe
     */
    void guardar(Drone drone) throws Exception;

    /**
     * Busca un dron por su identificador.
     *
     * @param id identificador del dron
     * @return dron encontrado, o {@code null} si no existe
     * @throws Exception si ocurre un error durante la consulta
     */
    Drone buscarPorId(String id) throws Exception;

    /**
     * Retorna todos los drones registrados.
     *
     * @return lista de drones, vacía si no hay registros
     * @throws Exception si ocurre un error durante la consulta
     */
    List<Drone> buscarTodos() throws Exception;

    /**
     * Actualiza los datos de un dron existente.
     *
     * @param drone dron con los datos actualizados
     * @throws Exception si el dron no existe o ocurre un error al actualizar
     */
    void actualizar(Drone drone) throws Exception;

    /**
     * Elimina el dron con el identificador dado.
     *
     * @param id identificador del dron a eliminar
     * @throws Exception si el dron no existe o ocurre un error al eliminar
     */
    void eliminar(String id) throws Exception;
}