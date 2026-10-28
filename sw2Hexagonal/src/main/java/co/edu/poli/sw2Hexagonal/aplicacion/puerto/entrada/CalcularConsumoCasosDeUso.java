package co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada;

import co.edu.poli.sw2Hexagonal.dominio.modelo.strategy.ConsumoStrategy;
import co.edu.poli.sw2Hexagonal.dominio.modelo.strategy.PuntoGeografico;

/**
 * Caso de uso que estima la batería requerida por un dron entre un punto de
 * origen y uno de destino.
 * <p>
 * Es el <b>Context</b> del patrón Strategy: mantiene una referencia a la
 * {@link ConsumoStrategy} seleccionada y le delega el cálculo. La estrategia
 * se puede cambiar en tiempo de ejecución con
 * {@link #setEstrategia(ConsumoStrategy)}.
 * </p>
 * <p>
 * No usa persistencia: el cálculo se hace en memoria con los puntos cargados
 * desde el archivo JSON. El cálculo de distancia lo recibe como una
 * {@link CalculadoraDistancia}, de modo que la capa de aplicación no depende
 * de la infraestructura. En {@code App} se le pasa
 * {@code HaversineUtil::distanciaKm}.
 * </p>
 */
public class CalcularConsumoCasosDeUso {

    /**
     * Puerto para calcular la distancia entre dos puntos geográficos.
     * Lo provee la infraestructura (fórmula de Haversine).
     */
    @FunctionalInterface
    public interface CalculadoraDistancia {

        /**
         * @param origen  punto de origen
         * @param destino punto de destino
         * @return distancia en kilómetros
         */
        double calcularKm(PuntoGeografico origen, PuntoGeografico destino);
    }

    /** Implementación del cálculo de distancia. */
    private final CalculadoraDistancia calculadoraDistancia;

    /** Estrategia de consumo actual. */
    private ConsumoStrategy estrategia;

    /**
     * Crea el Context.
     *
     * @param calculadoraDistancia implementación del cálculo de distancia
     */
    public CalcularConsumoCasosDeUso(CalculadoraDistancia calculadoraDistancia) {
        this.calculadoraDistancia = calculadoraDistancia;
    }

    /**
     * Cambia la estrategia de consumo energético en tiempo de ejecución.
     *
     * @param estrategia estrategia a usar
     */
    public void setEstrategia(ConsumoStrategy estrategia) {
        this.estrategia = estrategia;
    }

    /**
     * @return estrategia actual, o {@code null} si no se ha seleccionado
     */
    public ConsumoStrategy getEstrategia() {
        return estrategia;
    }

    /**
     * Calcula la distancia entre dos puntos geográficos.
     *
     * @param origen  punto de origen
     * @param destino punto de destino
     * @return distancia en kilómetros
     * @throws IllegalArgumentException si algún punto es nulo
     */
    public double calcularDistanciaKm(PuntoGeografico origen, PuntoGeografico destino) {
        if (origen == null || destino == null) {
            throw new IllegalArgumentException("Debe cargar el origen y el destino");
        }
        return calculadoraDistancia.calcularKm(origen, destino);
    }

    /**
     * Calcula la batería requerida delegando en la estrategia actual.
     *
     * @param distanciaKm distancia del recorrido en kilómetros
     * @return batería requerida
     * @throws IllegalStateException    si no se ha seleccionado una estrategia
     * @throws IllegalArgumentException si la distancia es negativa
     */
    public double calcularConsumo(double distanciaKm) {
        if (estrategia == null) {
            throw new IllegalStateException("Debe seleccionar una estrategia de consumo");
        }
        if (distanciaKm < 0) {
            throw new IllegalArgumentException("La distancia no puede ser negativa");
        }
        return estrategia.calcularConsumo(distanciaKm);
    }
}