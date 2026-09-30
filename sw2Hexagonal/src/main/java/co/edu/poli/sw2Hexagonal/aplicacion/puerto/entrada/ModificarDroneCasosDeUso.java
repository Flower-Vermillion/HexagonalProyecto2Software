package co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada;

import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;

/**
 * Puerto de entrada — caso de uso: modificar un dron existente.
 * Define el contrato que el adaptador de entrada (UI) usa para
 * invocar la lógica de aplicación sin conocer su implementación.
 */
public interface ModificarDroneCasosDeUso {

    /**
     * Actualiza los datos de un dron existente.
     *
     * @param drone dron con los datos actualizados
     * @throws Exception si el dron no existe o ocurre un error al actualizar
     */
    void modificar(Drone drone) throws Exception;
}