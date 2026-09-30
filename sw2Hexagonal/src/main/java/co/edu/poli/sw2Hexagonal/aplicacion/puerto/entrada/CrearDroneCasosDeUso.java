package co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada;

import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;

/**
 * Puerto de entrada — caso de uso: crear un dron.
 * Define el contrato que el adaptador de entrada (UI) usa para
 * invocar la lógica de aplicación sin conocer su implementación.
 */
public interface CrearDroneCasosDeUso {

    /**
     * Registra un nuevo dron.
     *
     * @param drone dron a registrar
     * @throws Exception si ocurre un error durante el registro
     */
    void crear(Drone drone) throws Exception;
}