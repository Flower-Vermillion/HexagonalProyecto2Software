package co.edu.poli.sw2Hexagonal.aplicacion.mediator;

/**
 * Interfaz Mediator del patrón Mediator para coordinar una operación de
 * vigilancia con drones.
 * <p>
 * Los componentes de la operación (navegación, sensores, batería y
 * comunicación) no se comunican directamente entre sí: cada uno informa al
 * mediador lo que hizo mediante un {@link Evento}, y el mediador decide qué
 * componente debe actuar a continuación.
 * </p>
 *
 * @see MissionCoordinator
 */
public interface DroneMediator {

    /**
     * Eventos que los componentes reportan al mediador.
     */
    enum Evento {
        /** La ruta del dron quedó preparada. */
        RUTA_PREPARADA,
        /** La ruta fue cancelada. */
        RUTA_CANCELADA,
        /** La batería alcanza para la misión. */
        BATERIA_SUFICIENTE,
        /** La batería no alcanza para la misión. */
        BATERIA_INSUFICIENTE,
        /** Los sensores quedaron activos. */
        SENSORES_ACTIVADOS,
        /** Los sensores fueron desactivados. */
        SENSORES_DESACTIVADOS,
        /** El enlace de comunicación quedó establecido. */
        ENLACE_ESTABLECIDO,
        /** No se pudo establecer el enlace de comunicación. */
        ENLACE_FALLIDO
    }

    /**
     * Clase base de los componentes (colegas) del patrón Mediator.
     * <p>
     * Cada componente conoce únicamente al mediador, nunca a los demás
     * componentes.
     * </p>
     */
    abstract class Componente {

        /** Mediador al que el componente reporta sus eventos. */
        private DroneMediator mediador;

        /**
         * Asigna el mediador del componente.
         *
         * @param mediador mediador que coordinará al componente
         */
        public void setMediador(DroneMediator mediador) {
            this.mediador = mediador;
        }

        /**
         * Informa un evento al mediador.
         *
         * @param evento  evento ocurrido
         * @param detalle descripción de lo ocurrido
         * @throws IllegalStateException si el componente no tiene mediador
         */
        protected void notificar(Evento evento, String detalle) {
            if (mediador == null) {
                throw new IllegalStateException(getNombre() + " no tiene un mediador asignado");
            }
            mediador.notificar(this, evento, detalle);
        }

        /**
         * @return nombre del componente, usado en la bitácora
         */
        public abstract String getNombre();
    }

    /**
     * Recibe un evento de un componente y coordina la reacción de los demás.
     *
     * @param emisor  componente que generó el evento
     * @param evento  evento ocurrido
     * @param detalle descripción de lo ocurrido
     */
    void notificar(Componente emisor, Evento evento, String detalle);
}