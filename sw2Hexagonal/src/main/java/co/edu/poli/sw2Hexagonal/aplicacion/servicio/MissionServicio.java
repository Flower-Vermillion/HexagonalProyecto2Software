package co.edu.poli.sw2Hexagonal.aplicacion.servicio;

import co.edu.poli.sw2Hexagonal.aplicacion.mediator.MissionCoordinator;
import co.edu.poli.sw2Hexagonal.aplicacion.mediator.components.BatterySystem;
import co.edu.poli.sw2Hexagonal.aplicacion.mediator.components.CommunicationSystem;
import co.edu.poli.sw2Hexagonal.aplicacion.mediator.components.NavigationSystem;
import co.edu.poli.sw2Hexagonal.aplicacion.mediator.components.SensorSystem;
import co.edu.poli.sw2Hexagonal.aplicacion.port.in.ExecuteMissionCasosDeUso;

/**
 * Implementación del caso de uso {@link ExecuteMissionCasosDeUso}.
 * <p>
 * Valida los datos, arma los componentes con su mediador
 * ({@link MissionCoordinator}) y le pide iniciar la misión. No usa el
 * repositorio de drones ni ninguna operación CRUD.
 * </p>
 */
public class MissionServicio implements ExecuteMissionCasosDeUso {

    /**
     * Crea el servicio.
     */
    public MissionServicio() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Resultado ejecutar(String codigoDron, String zona,
                              double distanciaKm, double bateriaDisponible) {
        if (codigoDron == null || codigoDron.isBlank()) {
            throw new IllegalArgumentException("Debe indicar el código del dron");
        }
        if (zona == null || zona.isBlank()) {
            throw new IllegalArgumentException("Debe indicar la zona de vigilancia");
        }
        if (distanciaKm <= 0) {
            throw new IllegalArgumentException("La distancia debe ser mayor que 0");
        }
        if (bateriaDisponible < 0 || bateriaDisponible > 100) {
            throw new IllegalArgumentException("La batería debe estar entre 0 y 100");
        }

        MissionCoordinator coordinador = new MissionCoordinator(
                new NavigationSystem(),
                new SensorSystem(),
                new BatterySystem(),
                new CommunicationSystem());

        coordinador.iniciarMision(codigoDron.trim(), zona.trim(), distanciaKm, bateriaDisponible);

        return new Resultado(coordinador.isMisionIniciada(), coordinador.getBitacora());
    }
}