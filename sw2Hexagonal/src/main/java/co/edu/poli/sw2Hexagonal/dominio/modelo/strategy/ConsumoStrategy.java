package co.edu.poli.sw2Hexagonal.dominio.modelo.strategy;

/**
 * Interfaz Strategy del patrón Strategy para estimar la batería que requiere
 * un dron durante un recorrido.
 * <p>
 * Cada estrategia concreta aplica un factor de consumo distinto sobre la
 * distancia recorrida. El Context ({@code CalcularConsumoServicio}) puede
 * cambiar de estrategia en tiempo de ejecución sin modificar su código.
 * </p>
 *
 * @see AhorroStrategy
 * @see NormalStrategy
 * @see AltoRendimientoStrategy
 */
public interface ConsumoStrategy {

    /**
     * Calcula la batería requerida para recorrer una distancia.
     *
     * @param distanciaKm distancia del recorrido en kilómetros
     * @return batería requerida
     */
    double calcularConsumo(double distanciaKm);

    /**
     * @return nombre de la estrategia, por ejemplo "Modo Ahorro"
     */
    String getNombre();

    /**
     * @return factor que se multiplica por la distancia
     */
    double getFactor();
}