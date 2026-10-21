package co.edu.poli.sw2Hexagonal.dominio.modelo.weather;

import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;

/**
 * Observer del clima.
 * Un observer se suscribe a uno o varios drones y es notificado
 * con los cinco aspectos climáticos cada vez que se actualizan.
 */
public interface WeatherObserver {

    /** Suscribe este observer al drone indicado. */
    void suscribir(Drone drone);

    /** Elimina la suscripción de este observer al drone indicado. */
    void desuscribir(Drone drone);

    /** Recibe la actualización climática de un drone. */
    void notificar(Drone drone, Weather clima);
}