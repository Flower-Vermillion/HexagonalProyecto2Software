package co.edu.poli.sw2Hexagonal.dominio.modelo.strategy;

/**
 * Estrategia concreta: Modo Alto Rendimiento.
 * <p>
 * Aumenta la velocidad sacrificando batería: distancia en km × 1.5.
 * </p>
 */
public class AltoRendimientoStrategy implements ConsumoStrategy {

    /** Factor de consumo del modo alto rendimiento. */
    public static final double FACTOR = 1.5;

    /**
     * {@inheritDoc}
     */
    @Override
    public double calcularConsumo(double distanciaKm) {
        return distanciaKm * FACTOR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNombre() {
        return "Modo Alto Rendimiento";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getFactor() {
        return FACTOR;
    }
}