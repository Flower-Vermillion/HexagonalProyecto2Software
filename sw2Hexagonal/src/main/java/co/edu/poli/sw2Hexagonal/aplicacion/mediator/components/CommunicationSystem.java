package co.edu.poli.sw2Hexagonal.aplicacion.mediator.components;

/**
 * Componente del patrón Mediator: sistema de comunicación.
 * <p>
 * Establece el enlace entre el dron y la estación de control. El enlace solo
 * es posible si la zona está dentro del alcance máximo. No conoce a los demás
 * componentes; lo coordina el mediador.
 * </p>
 */
public class CommunicationSystem {

    /** Alcance máximo del enlace, en km. */
    public static final double ALCANCE_MAXIMO_KM = 15.0;

    /**
     * Indica si la zona está dentro del alcance del enlace.
     *
     * @param distanciaKm distancia hasta la zona, en km
     * @return {@code true} si se puede establecer el enlace
     */
    public boolean estaEnAlcance(double distanciaKm) {
        return distanciaKm <= ALCANCE_MAXIMO_KM;
    }

    /**
     * Describe el resultado de intentar establecer el enlace.
     *
     * @param distanciaKm distancia hasta la zona, en km
     * @return descripción del enlace
     */
    public String describirEnlace(double distanciaKm) {
        if (estaEnAlcance(distanciaKm)) {
            return String.format("Enlace establecido en canal 5.8 GHz (zona a %.1f km, alcance máximo %.0f km)",
                    distanciaKm, ALCANCE_MAXIMO_KM);
        }
        return String.format("Sin enlace: la zona está a %.1f km y el alcance máximo es %.0f km",
                distanciaKm, ALCANCE_MAXIMO_KM);
    }
}