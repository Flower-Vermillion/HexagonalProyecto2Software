package co.edu.poli.sw2Hexagonal.aplicacion.mediator.components;

/**
 * Componente del patrón Mediator: sistema de navegación.
 * <p>
 * Prepara la ruta del dron hacia la zona de vigilancia. No conoce a los
 * demás componentes; lo coordina el mediador.
 * </p>
 */
public class NavigationSystem {

    /**
     * Prepara la ruta hacia la zona, con un punto de control cada 2 km
     * aproximadamente.
     *
     * @param zona        zona de vigilancia
     * @param distanciaKm distancia hasta la zona, en km
     * @return descripción de la ruta preparada
     */
    public String prepararRuta(String zona, double distanciaKm) {
        int puntosControl = (int) Math.ceil(distanciaKm / 2.0) + 1;
        return String.format("Ruta hacia '%s' preparada: %.1f km, %d puntos de control",
                zona, distanciaKm, puntosControl);
    }

    /**
     * Cancela la ruta preparada.
     *
     * @return descripción de la cancelación
     */
    public String cancelarRuta() {
        return "Ruta cancelada";
    }
}