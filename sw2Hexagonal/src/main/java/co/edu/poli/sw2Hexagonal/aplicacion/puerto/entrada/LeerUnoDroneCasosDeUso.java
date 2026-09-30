package co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada;

import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;

/**
 * Puerto de entrada — caso de uso: leer un dron por su identificador.
 * Define el contrato que el adaptador de entrada (UI) usa para
 * invocar la lógica de aplicación sin conocer su implementación.
 */
public interface LeerUnoDroneCasosDeUso {

    /**
     * Busca y retorna un dron dado su identificador.
     *
     * @param id identificador del dron a buscar
     * @return dron encontrado, o {@code null} si no existe
     * @throws Exception si ocurre un error durante la búsqueda
     */
    Drone leerUno(String id) throws Exception;
}