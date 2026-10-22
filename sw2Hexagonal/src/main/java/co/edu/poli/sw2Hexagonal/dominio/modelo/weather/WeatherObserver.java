package co.edu.poli.sw2Hexagonal.dominio.modelo.weather;

import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;

/**
 * Contrato del Observer en el patrón Observer del clima.
 * <p>
 * El Subject es {@link Drone}: cada observador se suscribe a uno o varios
 * drones y recibe una notificación con los cinco aspectos climáticos cada
 * vez que el clima de esos drones se actualiza desde la API externa.
 * </p>
 * <p>
 * Los observadores se registran y eliminan dinámicamente, en tiempo de
 * ejecución, mediante {@link #suscribir(Drone)} y {@link #desuscribir(Drone)}.
 * </p>
 *
 * @see Weather
 * @see Drone
 */
public interface WeatherObserver {

    /**
     * Suscribe este observador al drone indicado. A partir de ese momento
     * recibirá las actualizaciones climáticas de ese drone.
     *
     * @param drone drone (Subject) al que se suscribe
     */
    void suscribir(Drone drone);

    /**
     * Elimina la suscripción de este observador al drone indicado. Deja de
     * recibir las actualizaciones climáticas de ese drone.
     *
     * @param drone drone (Subject) del que se desuscribe
     */
    void desuscribir(Drone drone);

    /**
     * Recibe la actualización climática de un drone. Si el observador no
     * está suscrito a ese drone, ignora la notificación.
     *
     * @param drone drone (Subject) cuyo clima cambió
     * @param clima los cinco aspectos climáticos actualizados
     */
    void notificar(Drone drone, Weather clima);
}