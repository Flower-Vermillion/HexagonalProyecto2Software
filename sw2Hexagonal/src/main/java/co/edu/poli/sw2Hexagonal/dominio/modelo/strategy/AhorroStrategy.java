package co.edu.poli.sw2Hexagonal.dominio.modelo.strategy;

/**
 * Estrategia concreta: Modo Ahorro.
 * <p>
 * Minimiza el consumo de batería: distancia en km × 0.5.
 * </p>
 */
public class AhorroStrategy implements ConsumoStrategy {

    /** Factor de consumo del modo ahorro. */
    public static final double FACTOR = 0.5;

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
        return "Modo Ahorro";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getFactor() {
        return FACTOR;
    }
}