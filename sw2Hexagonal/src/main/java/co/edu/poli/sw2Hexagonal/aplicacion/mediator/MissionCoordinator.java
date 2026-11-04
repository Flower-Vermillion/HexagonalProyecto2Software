package co.edu.poli.sw2Hexagonal.aplicacion.mediator;

import co.edu.poli.sw2Hexagonal.aplicacion.mediator.components.BatterySystem;
import co.edu.poli.sw2Hexagonal.aplicacion.mediator.components.CommunicationSystem;
import co.edu.poli.sw2Hexagonal.aplicacion.mediator.components.NavigationSystem;
import co.edu.poli.sw2Hexagonal.aplicacion.mediator.components.SensorSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mediador concreto del patrón Mediator: coordinador de misión.
 * <p>
 * Centraliza la interacción entre los componentes y controla la secuencia de
 * una operación de vigilancia:
 * </p>
 * <ol>
 *   <li>Navegación prepara la ruta.</li>
 *   <li>Batería verifica la disponibilidad energética.</li>
 *   <li>Sensores se activan.</li>
 *   <li>Comunicación establece el enlace.</li>
 * </ol>
 * <p>
 * Si una condición falla (batería insuficiente o zona fuera del alcance del
 * enlace), aborta la misión: desactiva los sensores, si estaban activos, y
 * cancela la ruta. Todo lo ocurrido queda registrado en una bitácora.
 * </p>
 */
public class MissionCoordinator implements DroneMediator {

    /** Sistema de navegación. */
    private final NavigationSystem navegacion;
    /** Sistema de sensores. */
    private final SensorSystem sensores;
    /** Sistema de batería. */
    private final BatterySystem bateria;
    /** Sistema de comunicación. */
    private final CommunicationSystem comunicacion;

    /** Registro de todo lo ocurrido en la operación. */
    private final List<String> bitacora = new ArrayList<>();

    /** Indica si la última misión quedó iniciada. */
    private boolean misionIniciada;

    /**
     * Crea el coordinador con los componentes que va a coordinar.
     *
     * @param navegacion   sistema de navegación
     * @param sensores     sistema de sensores
     * @param bateria      sistema de batería
     * @param comunicacion sistema de comunicación
     */
    public MissionCoordinator(NavigationSystem navegacion, SensorSystem sensores,
                              BatterySystem bateria, CommunicationSystem comunicacion) {
        this.navegacion = navegacion;
        this.sensores = sensores;
        this.bateria = bateria;
        this.comunicacion = comunicacion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean iniciarMision(String codigoDron, String zona,
                                 double distanciaKm, double bateriaDisponible) {
        bitacora.clear();
        misionIniciada = false;

        registrar("Coordinador", String.format(
                "Iniciando operación de vigilancia del dron %s en '%s'", codigoDron, zona));

        // 1. Navegación: preparar la ruta
        registrar("Navegación", navegacion.prepararRuta(zona, distanciaKm));

        // 2. Batería: verificar la disponibilidad energética
        registrar("Batería", bateria.describirVerificacion(distanciaKm, bateriaDisponible));
        if (!bateria.esSuficiente(distanciaKm, bateriaDisponible)) {
            abortar("batería insuficiente", false);
            return false;
        }

        // 3. Sensores: activar los necesarios para la vigilancia
        registrar("Sensores", sensores.activar());

        // 4. Comunicación: establecer el enlace
        registrar("Comunicación", comunicacion.describirEnlace(distanciaKm));
        if (!comunicacion.estaEnAlcance(distanciaKm)) {
            abortar("sin enlace de comunicación", true);
            return false;
        }

        misionIniciada = true;
        registrar("Coordinador", "✔ MISIÓN INICIADA: todos los sistemas listos");
        return true;
    }

    /**
     * Aborta la misión: desactiva los sensores si estaban activos y cancela
     * la ruta.
     *
     * @param motivo          causa del aborto
     * @param sensoresActivos {@code true} si los sensores ya se habían activado
     */
    private void abortar(String motivo, boolean sensoresActivos) {
        if (sensoresActivos) {
            registrar("Sensores", sensores.desactivar());
        }
        registrar("Navegación", navegacion.cancelarRuta());
        registrar("Coordinador", "✘ MISIÓN ABORTADA: " + motivo);
    }

    /**
     * Agrega una línea a la bitácora.
     *
     * @param origen  quién genera la línea
     * @param mensaje texto a registrar
     */
    private void registrar(String origen, String mensaje) {
        bitacora.add("[" + origen + "] " + mensaje);
    }

    /**
     * @return {@code true} si la última misión quedó iniciada
     */
    public boolean isMisionIniciada() {
        return misionIniciada;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getBitacora() {
        return Collections.unmodifiableList(bitacora);
    }
}