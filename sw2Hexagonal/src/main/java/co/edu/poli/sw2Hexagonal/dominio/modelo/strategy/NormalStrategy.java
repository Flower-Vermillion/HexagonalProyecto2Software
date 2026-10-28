package co.edu.poli.sw2Hexagonal.dominio.modelo.strategy;

/**
 * Estrategia concreta: Modo Normal.
 * <p>
 * Consumo estándar: distancia en km × 1.
 * </p>
 */
public class NormalStrategy implements ConsumoStrategy {

    /** Factor de consumo del modo normal. */
    public static final double FACTOR = 1.0;

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
        return "Modo Normal";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getFactor() {
        return FACTOR;
    }
}