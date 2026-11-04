package co.edu.poli.sw2Hexagonal.aplicacion.mediator.components;

import co.edu.poli.sw2Hexagonal.aplicacion.mediator.DroneMediator;
import co.edu.poli.sw2Hexagonal.aplicacion.mediator.DroneMediator.Evento;

/**
 * Componente del patrón Mediator: sistema de navegación.
 * <p>
 * Prepara la ruta del dron hacia la zona de vigilancia. Al terminar,
 * informa al mediador; no conoce a los demás componentes.
 * </p>
 */
public class NavigationSystem extends DroneMediator.Componente {

    /**
     * Prepara la ruta hacia la zona de vigilancia, con un punto de control
     * cada 2 km aproximadamente.
     *
     * @param zona        zona de vigilancia
     * @param distanciaKm distancia hasta la zona, en km
     */
    public void prepararRuta(String zona, double distanciaKm) {
        int puntosControl = (int) Math.ceil(distanciaKm / 2.0) + 1;
        notificar(Evento.RUTA_PREPARADA, String.format(
                "Ruta hacia '%s' preparada: %.1f km, %d puntos de control",
                zona, distanciaKm, puntosControl));
    }

    /**
     * Cancela la ruta preparada.
     */
    public void cancelarRuta() {
        notificar(Evento.RUTA_CANCELADA, "Ruta cancelada");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNombre() {
        return "Navegación";
    }
}