package co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada;

/**
 * Puerto de entrada — caso de uso: eliminar un dron.
 * Define el contrato que el adaptador de entrada (UI) usa para
 * invocar la lógica de aplicación sin conocer su implementación.
 */
public interface EliminarDroneCasosDeUso {

    /**
     * Elimina el dron con el identificador dado.
     *
     * @param id identificador del dron a eliminar
     * @throws Exception si el dron no existe o ocurre un error al eliminar
     */
    void eliminar(String id) throws Exception;
}