package co.edu.poli.sw2Hexagonal.aplicacion.port.in;

import java.util.List;

/**
 * Puerto de entrada del caso de uso que inicia una operación de vigilancia
 * coordinada mediante el patrón Mediator.
 * <p>
 * No usa las operaciones CRUD de drones: recibe únicamente el código del
 * dron, que la interfaz toma de la lista de drones ya cargada.
 * </p>
 */
public interface ExecuteMissionCasosDeUso {

    /**
     * Resultado de la operación.
     *
     * @param iniciada {@code true} si la misión quedó iniciada
     * @param bitacora registro de todo lo ocurrido, en orden
     */
    record Resultado(boolean iniciada, List<String> bitacora) {
    }

    /**
     * Coordina el inicio de una operación de vigilancia.
     *
     * @param codigoDron        código del dron
     * @param zona              zona de vigilancia
     * @param distanciaKm       distancia hasta la zona, en km (mayor que 0)
     * @param bateriaDisponible batería disponible, en % (de 0 a 100)
     * @return resultado de la operación con su bitácora
     * @throws IllegalArgumentException si algún dato es inválido
     */
    Resultado ejecutar(String codigoDron, String zona, double distanciaKm, double bateriaDisponible);
}